package application.domain.importer.parser;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class SavakisParser implements IParsers {

    @Override
    public ParserType getType() {
        return ParserType.Savakis;
    }

    @Override
    public List<Product> parse(String invoiceContent) {
        List<Product> list = new ArrayList<>();
        String[] lines = invoiceContent.split("\n");
        boolean shouldRead = false;
        for( int i=0; i < lines.length;++i) {
           if (lines[i].contains("ΑΞΙΑ %")) {
                shouldRead = true;
                continue;
            } else if( shouldSkip(lines[i]) ) {
                shouldRead = false;
            }

            if(shouldRead) {
                String[] cols = lines[i].split(" ");
                int offset = 0;
                if(cols.length < 9) {
                    continue;
                }
                Product product = new Product();

                if( cols.length == 12 ) {
                    product.setOrigin(cols[3].trim());
                    product.setName(cols[1] + " " + cols[2].trim());
                    offset = 1;
                    product.setNumber(cols[offset+3].trim());
                } else if (cols.length == 11) {
                    product.setName(cols[1].trim());
                    product.setOrigin(cols[2].trim());
                    product.setNumber(cols[offset+3].trim());
                } else if(cols.length == 13) {
                    product.setName(cols[1] + " " + cols[2].trim());
                    product.setOrigin(cols[4].trim());
                    offset = 2;
                    product.setNumber(cols[offset+3].trim());
                } else if (cols.length == 10) {
                    // missing product number
                    product.setName(cols[1].trim());
                    product.setOrigin(cols[2].trim());
                    offset = -1;
                    product.setNumber("N/A");
                } else if(cols.length == 9) {
                    // missing origin and product number
                    product.setName(cols[1].trim());
                    product.setOrigin(" ");
                    offset = -2;
                    product.setNumber(" ");
                }
                addNameSuffix(product, lines, i);

                product.setQuantity(cols[offset+6].trim());
                product.setPrice(cols[offset+7].trim());
                product.setTax(cols[offset+8].trim());
                product.setDiscount(cols[offset+10].trim());
                product.setMeasurementUnit("-");
                list.add(product);
            }
        }

        return list;
    }

    private void addNameSuffix(Product product, String[] lines, int i){
        boolean hasNextLine =  i+1 < lines.length;
        if(hasNextLine && isValidNameSuffix(lines[i+1])) {
            String nextLine = lines[i+1].trim();
            int nextLineCols = nextLine.split(" ").length;
            if(nextLineCols <= 2){
                product.setName(product.getName()+" "+nextLine);
            }
        }
    }

    private boolean shouldSkip(String line) {
        return (line.contains("ΣΥΝΟΛΑ") ||
                line.contains("ΑΝΑΛΥΣΗ") ||
                line.contains("ΕΠΩΝΥΜΙΑ"));
    }

    private boolean isValidNameSuffix(String nexLine){
       return  !shouldSkip(nexLine)
                && !nexLine.trim().matches("[0-9]/[0-9]");
    }
}