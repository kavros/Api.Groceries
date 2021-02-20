package application.domain;

import application.domain.importer.parser.IParsers;
import application.domain.importer.parser.KapnisisParser;
import application.domain.importer.parser.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KapnisisParserTests {

    private IParsers parser;

    @BeforeAll
    public void Setup()
    {
        parser  = new KapnisisParser();
    }

    @Test
    public void getTimeStamp_whenDateIsAtTheThirdLine_getParsedCorrectly() throws ParseException {
        String content = "ΚΩ∆ΙΚΟΣ ΗΜ/ΝΙΑ ΩΡΑ\n" +
                "ΤΙΜΟΛΟΓΙΟ ΠΩΛΗΣΗΣ - ∆ΕΛ. ΑΠΟΣΤΟΛΗΣ\n" +
                "Τ∆Π00550255 Τρι  7/4/20 06:39";

        Timestamp dateTime = parser.getTimeStamp(content);

        assertEquals("2020-04-07 06:39:00.0", dateTime.toString());
    }

    @Test
    public void getTimeStamp_whenDateIsAtTheSecondLine_getParsedCorrectly() throws ParseException {
        String content = "ΚΩ∆ΙΚΟΣ ΗΜ/ΝΙΑ ΩΡΑ\n" +
                "Τ∆Π00550255 Τρι  7/4/20 06:39\n"+
                "ΤΙΜΟΛΟΓΙΟ ΠΩΛΗΣΗΣ - ∆ΕΛ. ΑΠΟΣΤΟΛΗΣ";

        Timestamp dateTime = parser.getTimeStamp(content);

        assertEquals("2020-04-07 06:39:00.0", dateTime.toString());
    }

    @Test
    void parse_onCorrectInput_returnsCorrectOutput(){
        String content = "ΣΧΕΤΙΚΑ ΠΑΡΑΣΤΑΤΙΚΑ\n" +
                "ΑΓΓΟΥΡΙΑ-ΛΑΣΙΘΙΟΥ-ΠΟΙΟΤΗΤΑ Α 02344403020420 ΚΙΛ 0,00 5,50 0,850 4,68 0,00 4,68 13\n" +
                "ΚΟΛΟΚΥΘΙΑ-ΧΑΝΙΩΝ-ΠΟΙΟΤΗΤΑ Α 17491850060420 ΚΙΛ 0,00 8,00 1,200 9,60 0,00 9,60 13\n" +
                "ΤΕΛΑΡΑ IFCO-4320- ΤΕΜ 0,00 1,00 5,000 5,00 0,00 5,00 0\n" +
                "ΜΑΙΝΤΑΝΟΣ-ΧΑΝΙΩΝ-ΜΑΤΣΑΚΙ 17491852060420 ΤΕΜ 0,00 10,00 0,280 2,80 0,00 2,80 13\n"+
                "ΑΝΙΘΟ-ΧΑΝΙΩΝ-ΜΑΤΣΑΚΙ 29871846060420 ΜΑΤ 0,00 5,00 0,280 1,40 0,00 1,40 13\n"+
                "ΑΓΚΙΝΑΡΕΣ-ΝΑΥΠΛΙΟΥ-ΗΜΕΡΕΣ 328194280320 ΖΕΥ 0,00 3,00 1,200 3,60 0,00 3,60 13\n"+
                "ΥΠΟΛΟΙΠΑ" ;

        List<Product> expected = Arrays.asList(
            new Product(
                    "ΑΓΓΟΥΡΙΑ","ΛΑΣΙΘΙΟΥ",
                    "ΚΙΛ ", "02344403020420",
                    new BigDecimal("5.50"),new BigDecimal("0.00"),
                    13,new BigDecimal("0.850"),null
            ),
            new Product(
                    "ΚΟΛΟΚΥΘΙΑ","ΧΑΝΙΩΝ",
                    "ΚΙΛ ", "17491850060420",
                    new BigDecimal("8.00"),new BigDecimal("0.00"),
                    13,new BigDecimal("1.200"),null
            ),
            new Product(
                    "ΜΑΙΝΤΑΝΟΣ","ΧΑΝΙΩΝ",
                    "TEM ", "17491852060420",
                    new BigDecimal("10.00"),new BigDecimal("0.00"),
                    13,new BigDecimal("0.280"),null
            ),
            new Product(
                    "ΑΝΙΘΟ","ΧΑΝΙΩΝ",
                    "ΜΑΤ ", "29871846060420",
                    new BigDecimal("5.00"),new BigDecimal("0.00"),
                    13,new BigDecimal("0.280"),null
            ),
            new Product(
                    "ΑΓΚΙΝΑΡΕΣ","ΝΑΥΠΛΙΟΥ",
                    "ΖΕΥ ", "328194280320",
                    new BigDecimal("3.00"),new BigDecimal("0.00"),
                    13,new BigDecimal("1.200"),null
            )

        );

        List<Product> actual = parser.parse(content);

        assertEqual(expected, actual);
    }

    private void assertEqual(List<Product> expected, List<Product> actual){
        assertEquals(expected.size(), actual.size());

        for(int i=0; i < actual.size(); i++){
            Product e = expected.get(i);
            Product a = actual.get(i);
            assertEquals(e.getName(), a.getName());
            assertEquals(e.getOrigin(), a.getOrigin());
            assertEquals(e.getMeasurementUnit(), a.getMeasurementUnit());

            assertEquals(e.getNumber(), a.getNumber());
            assertEquals(e.getQuantity(), a.getQuantity());
            assertEquals(e.getDiscount(), a.getDiscount());

            assertEquals(e.getTax(), a.getTax());
            assertEquals(e.getpDate(), a.getpDate());
            assertEquals(e.getPrice(), a.getPrice());
        }

    }



}
