package application.domain;

import application.domain.importer.parser.InvoiceParser;
import application.domain.importer.parser.ParserResult;
import application.domain.importer.price_calculator.PriceCalculator;
import application.model.mappings.Mappings;
import application.model.mappings.services.IMappingsRepository;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IRulesRepository.class, IMappingsRepository.class})
public class InvoiceParserTests {

    private IRulesRepository rulesRepo;
    private IMappingsRepository mappingsRepo;
    private InvoiceParser parser;
    @Before
    public void Setup()
    {
        rulesRepo = mock(IRulesRepository.class);
        mappingsRepo = mock(IMappingsRepository.class);
        setupMockData();
        parser = new InvoiceParser(
                rulesRepo,
                mappingsRepo,
                new PriceCalculator()
        );


    }

    private void setupMockData() {
        List<Mappings> mappings = new ArrayList<Mappings>();
        Mappings mapping = new Mappings();
        mapping.setpName("ΜΑΝΤΑΡΙΝΙΑ");
        mapping.setsCode("361");
        mappings.add(mapping);

        List<Rules> rules  = new ArrayList<Rules>();
        Rules rule =  new Rules();
        rule.setsCode("361");
        rule.setMinProfit(new BigDecimal(0.5));
        rule.setProfitPercentage(new BigDecimal(0.3));
        rules.add(rule);
        when(rulesRepo.getRules()).thenReturn(rules);
        when(mappingsRepo.getMappings()).thenReturn(mappings);
    }

    private String getInvoiceContent(String filePath) throws IOException{
        filePath = ".\\src\\test\\java\\application\\domain\\missing_col_lot_number.txt";
        List<String> lines =
                Files.readAllLines(
                        Paths.get(filePath)
                );
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append('\n');

        }
        return content.toString();
    }

    @Test
    public void ParseAndLoad_WhenThePartNumberMissing_ReturnsNotAssigned() throws IOException, ParseException {

        String invoiceContent = "ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ"+"\n"+
                "Ημερομηνία        : 24/12/2020"+"\n"+
                "Ώρα Αποστολής     : 11:24" +"\n"+
                "ΑΞΙΑ %"+"\n"+
                "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 0 0 20 0.84 13 16.8 0"+"\n"+
                "ΣΥΝΟΛΑ";
        ParserResult data = null;

        //data = parser.parseAndLoad(invoiceContent);

        //assertEquals(data.records.get(0).getNumber(), "N/A");
    }
}
