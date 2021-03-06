package application.domain;

import application.domain.importer.parser.dateTime.DateTimeParser;
import application.domain.importer.parser.dateTime.IDateTimeParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Timestamp;
import java.text.ParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DateTimeParserTests {

    @Autowired
    IDateTimeParser parser;

    @BeforeAll
    public void Setup()
    {
        parser  = new DateTimeParser();
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
    public void getTimeStamp_whenTheYearHas4Digits_works() throws ParseException {
        String content = "ΚΩ∆ΙΚΟΣ ΗΜ/ΝΙΑ ΩΡΑ\n" +
                "Τ∆Π00550255 Τρι  7/4/2021 06:39\n"+
                "ΤΙΜΟΛΟΓΙΟ ΠΩΛΗΣΗΣ - ∆ΕΛ. ΑΠΟΣΤΟΛΗΣ";

        Timestamp dateTime = parser.getTimeStamp(content);

        assertEquals("2021-04-07 06:39:00.0", dateTime.toString());
    }
}
