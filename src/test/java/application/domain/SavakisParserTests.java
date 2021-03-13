package application.domain;

import application.domain.importer.parser.Product;
import application.domain.importer.parser.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SavakisParserTests {

    private IParsers parser;

    @BeforeAll
    public void setup()
    {
        parser  = new SavakisParser();
    }

    private String getInvoiceContent(String row) {
        String invoiceStart = "ΛΙΑΝΙΚΟ ΕΜΠΟΡΙΟ ΕΙΔΩΝ"+"\n"+
                "Ημερομηνία        : 24/12/2020"+"\n"+
                "Ώρα Αποστολής     : 11:24" +"\n"+
                "ΑΞΙΑ %"+"\n";
        String invoiceEnd = "ΣΥΝΟΛΑ";

        return  invoiceStart + row + invoiceEnd;
    }

    @Test
    public void parse_WhenThePartNumberMissing_ReturnsNotAssigned() throws IOException, ParseException {

            String invoiceContent =
                    getInvoiceContent(
                    "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 0 0 20 0.84 13 16.8 0"+"\n"
                    );

            List<Product> data = null;

            data = parser.parse(invoiceContent);

            assertEquals(data.get(0).getNumber(), "N/A");
    }

    @Test
    public void parse_WhenThePartNumberSet_ReturnsCorrect() throws ParseException {

        String invoiceContent = getInvoiceContent(
                "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 2939220720 0 0 20 0.84 13 16.8 0"+"\n");


        List<Product> data = parser.parse(invoiceContent);

        assertEquals("2939220720", data.get(0).getNumber());
    }

    @Test
    public void parse_WhenDocumentContainsTwoPages_ReturnsCorrectContent() throws ParseException {

        String invoiceContent = getInvoiceContent(
                "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 2939220720 0 0 20 0.84 13 16.8 0"+"\n"+
                "ΕΠΩΝΥΜΙΑ"+"\n"+
                "ΑΞΙΑ %"+"\n"+
                "120 ΜΑΡΑΘΟ ΜΕΓΑΡΑ 791812202 0 0 5 0.24 13 1.2 0"+"\n"
            );

        List<Product> data = parser.parse(invoiceContent);

        assertEquals("ΜΑΝΤΑΡΙΝΙΑ", data.get(0).getName());
        assertEquals("ΜΑΡΑΘΟ", data.get(1).getName());
    }

    @Test
    public void parse_WhenProductNameExistsOnTwoLines_ReturnsTheCorrectName() throws ParseException {
        String invoiceContent = getInvoiceContent(
                "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 2939220720 0 0 20 0.84 13 16.8 0"+"\n"+
                "ΒΙΟΛΟΓΙΚΑ\n");

        List<Product> data = parser.parse(invoiceContent);

        assertEquals("ΜΑΝΤΑΡΙΝΙΑ ΒΙΟΛΟΓΙΚΑ", data.get(0).getName());
    }

    @Test
    public void parse_WhenProductNameExistsOnTwoLines_ReturnsTheCorrectName_2() throws ParseException {
        String invoiceContent = getInvoiceContent(
                "340 ΠΑΤΑΤΑ ΑΙΓΥΠΤΟΥ 2939220720 0 0 20 0.84 13 16.8 0"+"\n"+
                        "ΜΕ ΑΜΜΟ\n");

        List<Product> data = parser.parse(invoiceContent);

        assertEquals("ΠΑΤΑΤΑ ΜΕ ΑΜΜΟ", data.get(0).getName());
    }

    @Test
    public void parse_WhenProductNextLineNotValid_ReturnsTheCorrectName() throws ParseException {
        String invoiceContent = getInvoiceContent(
                "340 ΠΑΤΑΤΑ ΑΙΓΥΠΤΟΥ 2939220720 0 0 20 0.84 13 16.8 0"+"\n"+
                        "1/2\n");

        List<Product> data = parser.parse(invoiceContent);

        assertEquals("ΠΑΤΑΤΑ", data.get(0).getName());
    }
}
