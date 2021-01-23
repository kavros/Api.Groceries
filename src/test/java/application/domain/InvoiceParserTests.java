package application.domain;

import application.domain.importer.parser.InvoiceParser;
import application.domain.importer.parser.ParserResult;
import application.domain.importer.price_calculator.PriceCalculator;
import application.hibernate.IHibernateUtil;
import application.model.mappings.Mappings;
import application.model.mappings.services.IMappingsRepository;
import application.model.records.Record;
import application.model.rules.Rules;
import application.model.rules.services.IRulesRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
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
                dbConnection
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
}
