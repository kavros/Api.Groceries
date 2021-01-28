package application.domain;

import application.domain.importer.parser.InvoiceParser;
import application.domain.importer.parser.ParserResult;
import application.domain.importer.parser.SavakisParser;
import application.domain.importer.price_calculator.PriceCalculator;
import application.hibernate.IHibernateUtil;
import application.model.mapping.Mapping;
import application.model.mapping.services.IMappingsRepository;
import application.model.record.Record;
import application.model.rule.Rule;
import application.model.rule.services.IRulesRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.hibernate.query.Query;
import static org.mockito.ArgumentMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PrepareForTest({IRulesRepository.class, IMappingsRepository.class})
public class InvoiceParserTests {

    private IRulesRepository rulesRepo;
    private IMappingsRepository mappingsRepo;
    private InvoiceParser parser;
    IHibernateUtil dbConnection;

    @BeforeAll
    public void Setup()
    {

        dbConnection = mock(IHibernateUtil.class);
        rulesRepo = mock(IRulesRepository.class);
        mappingsRepo = mock(IMappingsRepository.class);
        setupMockData();
        parser = new InvoiceParser(
                rulesRepo,
                mappingsRepo,
                new PriceCalculator(),
                dbConnection,
                new SavakisParser()
        );


    }

    private void setupMockData() {

        List<Mapping> mappings = Arrays.asList(
                new Mapping("ΜΑΝΤΑΡΙΝΙΑ", "361"),
                new Mapping("ΜΑΡΑΘΟ", "362")
        );

        List<Rule> rules  = Arrays.asList(
            new Rule("361",0.5f,0.3f),
            new Rule("362",0.5f,0.3f)
        );

        when(rulesRepo.getRules()).thenReturn(rules);
        when(mappingsRepo.getMappings()).thenReturn(mappings);

        // mock database
        SessionFactory mockedSessionFactory =  Mockito.mock(SessionFactory.class);
        Session mockedSession = Mockito.mock(Session.class);
        Transaction mockedTransaction = Mockito.mock(Transaction.class);
        Query mockedQuery = Mockito.mock(Query.class);

        Mockito.when(mockedQuery.list()).thenReturn(new ArrayList<Record>());
        Mockito.when(mockedSession.createQuery(any(String.class))).thenReturn(mockedQuery);
        Mockito.when(mockedSessionFactory.openSession()).thenReturn(mockedSession);
        Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
        Mockito.when(mockedSession.getTransaction()).thenReturn(mockedTransaction);
        when(dbConnection.getSessionFactory()).thenReturn(mockedSessionFactory);
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
    public void ParseAndLoad_WhenThePartNumberMissing_ReturnsNotAssigned() throws IOException, ParseException {

            String invoiceContent =
                    getInvoiceContent(
                    "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 0 0 20 0.84 13 16.8 0"+"\n"
                    );

            ParserResult data = null;

            data = parser.parseAndLoad(invoiceContent);

            assertEquals(data.records.get(0).getNumber(), "N/A");
    }

    @Test
    public void ParseAndLoad_WhenThePartNumberSet_ReturnsCorrect() throws ParseException {

        String invoiceContent = getInvoiceContent(
                "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 2939220720 0 0 20 0.84 13 16.8 0"+"\n");

        ParserResult data = null;

        data = parser.parseAndLoad(invoiceContent);

        assertEquals(data.records.get(0).getNumber(), "2939220720");
    }

    @Test
    public void ParseAndLoad_WhenDocumentContainsTwoPages_ReturnsCorrectContent() throws ParseException {

        String invoiceContent = getInvoiceContent(
                "340 ΜΑΝΤΑΡΙΝΙΑ ΝΟΒΑ 2939220720 0 0 20 0.84 13 16.8 0"+"\n"+
                "ΕΠΩΝΥΜΙΑ"+"\n"+
                "ΑΞΙΑ %"+"\n"+
                "120 ΜΑΡΑΘΟ ΜΕΓΑΡΑ 791812202 0 0 5 0.24 13 1.2 0"+"\n"
            );

        ParserResult data = null;

        data = parser.parseAndLoad(invoiceContent);

        assertEquals(data.records.get(0).getName(), "ΜΑΝΤΑΡΙΝΙΑ");
        assertEquals(data.records.get(1).getName(), "ΜΑΡΑΘΟ");
    }

}
