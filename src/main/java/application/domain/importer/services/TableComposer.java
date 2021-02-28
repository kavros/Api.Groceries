package application.domain.importer.services;

import application.controllers.dtos.ImportDTO;
import application.domain.importer.parser.Product;
import application.domain.importer.parser.IParsers;
import application.domain.importer.parser.ParserType;
import application.domain.importer.parser.ParsersFactory;
import application.model.record.Record;
import application.model.record.services.IRecordsRepository;
import application.model.erp.Smast;
import application.model.erp.services.IERPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("tableCreator")
public class TableComposer implements ITableComposer {

    @Autowired
    IRecordsRepository recordsRepo;
    @Autowired
    IERPRepository erpRepo;
    @Autowired
    IRecordsRepository recordsRepository;
    @Autowired
    ParsersFactory factory;

    @Override
    public ImportDTO createTable(String invoiceContent) {

        ImportDTO dto = new ImportDTO();
        List<Record> records = null;
        List<Product> products = null;
        try {
            //parse pdf
            IParsers docParser = getParser(invoiceContent);
            products = docParser.parse(invoiceContent);
            Timestamp timestamp = docParser.getTimeStamp(invoiceContent);

            // build records
            records = recordsRepository.buildAndGetRecords(products,timestamp);

            //store records
            recordsRepository.storeRecords(records,timestamp);
            dto.invoiceDate = timestamp.toString();
        } catch (ParseException ex) {
            ImportDTO.Error err  = getError (
                        ImportDTO.ErrorCode.FAILED_TO_PARSE_DATE,
                        ex.getMessage()
                    );
            dto.errors.add(err);
            return dto;
        }catch (NoSuchElementException ex ) {
            // NoSuchElementException: failed to get Setting for a product name
            ImportDTO.Error err  = getError (
                    ImportDTO.ErrorCode.FAILED_TO_RETRIEVE_SETTING,
                    ex.getMessage()
            );
            dto.errors.add(err);
            return dto;
        }

        buildImportDTO(records, dto);

        return dto;
    }

    public void buildImportDTO(List<Record> records, ImportDTO dto){
        List<String> sCodes = records
                .stream()
                .map(Record::getsCode)
                .collect(Collectors.toList());

        //ERP call
        List<Smast> erpData = erpRepo.getProducts(sCodes);
        //Records call
        Map<String, List<Float>> latestPrices = recordsRepo
                .getLatestNewPrices( sCodes );


        records.forEach(x -> {

            ImportDTO.Entry r = dto.new Entry();
            r.name = x.getName();
            r.invoicePrice = x.getPrice().floatValue();
            r.origin = x.getOrigin();
            r.retailPrice = getKefalaioData(x.getsCode(), erpData).getsRetailPrice();
            r.sCode = x.getsCode();
            r.number = x.getNumber();
            r.newPrice = x.getNewPrice();
            r.profitInEuro = getActualProfit(r.newPrice.floatValue(),r.invoicePrice);
            r.records = latestPrices.get(x.getsCode());

            dto.data.add(r);

        });
    }

    private IParsers getParser(String invoiceContent){
        if(invoiceContent.contains("ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ")) {
            return factory.getService(ParserType.Savakis);
        } else {
            return factory.getService(ParserType.Kapnisis);
        }
    }


    private ImportDTO.Error getError(ImportDTO.ErrorCode code, String msg){
        ImportDTO.Error err  = new ImportDTO.Error();
        err.code = code;
        err.msg = msg;
        return err;
    }

    private Smast getKefalaioData(String sCode, List<Smast> smastList ) {
        Optional<Smast> smast = smastList
                .stream()
                .filter(x -> x.getsCode().equals(sCode))
                .findFirst();
        if(!smast.isPresent()){
            throw new NoSuchElementException("Failed to retrieve retail price for "+sCode);
        }
        return smast.get();
    }

    private float getActualProfit(float newPrice,float invoicePrice){
        float actualProfit = newPrice- (float)(invoicePrice*1.13);

        return round2Decimals(actualProfit);
    }

    private float round2Decimals(float number){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String roundedNumber = df.format(number).replace(',','.');

        return Float.parseFloat(roundedNumber);
    }
}
