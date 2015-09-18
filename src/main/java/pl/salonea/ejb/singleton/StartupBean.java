package pl.salonea.ejb.singleton;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.enums.Gender;
import pl.salonea.enums.ProviderType;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * Created by michzio on 01/09/2015.
 */

/** @DataSourceDefinition(
name = "java:app/jdbc/LocalServicesMySqlDataSource",
className = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource",
serverName   = "localhost",
portNumber   = 8889,
databaseName = "salonea",
user         = "root",
password     = "root",
properties   = {"connectionAttributes=;create=true"})
 */

@Singleton
@Startup
public class StartupBean {

    private static final Logger logger = Logger.getLogger(StartupBean.class.getName());

    @Inject
    private UserAccountFacade userAccountFacade;
    @Inject
    private NaturalPersonFacade naturalPersonFacade;
    @Inject
    private FirmFacade firmFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private IndustryFacade industryFacade;
    @Inject
    private CorporationFacade corporationFacade;
    @Inject
    private PaymentMethodFacade paymentMethodFacade;
    @Inject
    private ServiceFacade serviceFacade;
    @Inject
    private ProviderServiceFacade providerServiceFacade;
    @Inject
    private ClientFacade clientFacade;
    @Inject
    private ProviderRatingFacade providerRatingFacade;

    public StartupBean() { }

    @PostConstruct
    public void onStartup() {

        populateDatabase();
    }

    private void populateDatabase() {
        logger.info("populating database with sample entities on application startup...");

        populateUserAccounts();
        populateNaturalPersons();
        populateFirms();
        populateProviders();

    }

    private void populateUserAccounts() {

        UserAccount user1 = new UserAccount("michzio@hotmail.com", "michzio", "sAmPL3#e");
        UserAccount user2 = new UserAccount("alicja@krainaczarow.com", "alicja", "zAczka!00");
        userAccountFacade.create(user1);
        userAccountFacade.create(user2);
    }

    private void populateNaturalPersons() {

        Date dateOfBirth1 = new GregorianCalendar(1975, Calendar.OCTOBER, 10).getTime();
        Date dateOfBirth2 = new GregorianCalendar(1985, Calendar.APRIL, 25).getTime();
        NaturalPerson naturalPerson1 = new NaturalPerson("weronika@gmail.com", "weronika", "WeAdk!3%", "Weronika", "Kwiatkowska", dateOfBirth1, Gender.female);
        NaturalPerson naturalPerson2 = new NaturalPerson("jan.nowak@gmail.com", "jan.nowak", "jAn3@owaK", "Jan", "Nowak", dateOfBirth2, Gender.male );
        naturalPerson1.setHomeAddress(new Address("Bobrzyńskiego", "16", null, "30-340", "Kraków", "małopolskie", "Poland"));
        naturalPerson2.setHomeAddress(new Address("Wrocławska", "56", null, "30-345", "Kraków", "małopolskie", "Poland"));
        naturalPerson1.setDeliveryAsHome(true);;
        naturalPerson2.setDeliveryAsHome(true);

        naturalPersonFacade.create(naturalPerson1);
        naturalPersonFacade.create(naturalPerson2);
    }

    private void populateFirms() {
        Firm firm1 = new Firm("firma@allegro.pl", "allegro", "aAle2@", "Allegro Ltd.");
        Firm firm2 = new Firm("firma@fryzjer.pl", "fryzjer", "fRyZj2@", "Fryzjer Sp. z o.o.");
        firm1.setAddress(new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland"));
        firm2.setAddress(new Address("Wrocławska", "50", "30-150", "Kraków", "Małopolska", "Poland"));
        firm1.setVatin("1234567890");
        firm2.setVatin("2234567890");
        firm1.setCompanyNumber("1234567890");
        firm2.setCompanyNumber("2234567890");

        firmFacade.create(firm1);
        firmFacade.create(firm2);
    }

    private void populateProviders() {

        // create some instances of Provider entity
        Address address1 =  new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider1 = new Provider("firma@dentysta24.pl", "dentysta24", "aAle2@_", "Dentysta24 Sp. z o.o.",
                "9234567890", "9234567890", address1, "Dentysta24 Polska", ProviderType.FRANCHISE);

        Address address2 = new Address("Wrocławska", "45", "10-140", "Szczecin", "Zachodnio Pomorskie", "Poland");
        Provider provider2 = new Provider("firma@medyk.pl", "medyk", "tIe%13?", "Tieto Sp. z o.o.",
                "6593878688", "6510029930", address2, "Medyk Poland", ProviderType.CORPORATE);

        Address address3 = new Address("Kijowska", "09", "20-160", "Lublin", "Lubelskie", "Poland");
        Provider provider3 = new Provider("firma@fryzjer24.pl", "fryzjerka_pl", "fRyZU123?", "Fryzjer24 Sp. z o.o.",
                "1910020030", "1930040050", address3, "Fryzjer24 Polska", ProviderType.CORPORATE);

        Address address4 = new Address("Pomorska", "12", "99-200", "Gdańsk", "Pomorze", "Poland");
        Provider provider4 = new Provider("kontakt@przeprowadzki24.pl", "przeprowadzki24", "prZEP_M24%", "Przeprowadzki24 Sp. z o.o.",
                "4530040050", "4530040050", address4, "Przeprowadzki24 Pomorze", ProviderType.SIMPLE);

        Industry industry1 = new Industry("Branża medyczna");
        industry1.getProviders().add(provider1);
        industry1.getProviders().add(provider2);
        provider1.getIndustries().add(industry1);
        provider2.getIndustries().add(industry1);

        Address address5 = new Address("Wrocławska", "15", "29-100", "Kraków", "Małopolska", "Poland");
        Corporation corporation1 = new Corporation("Medical Corporation", "medical.png", address5);
        Address address6 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation2 = new Corporation("Hair and Style Corporation", "hair_and_style.png", address6);

        provider1.setCorporation(corporation1);
        provider2.setCorporation(corporation1);
        provider3.setCorporation(corporation2);

        PaymentMethod cash = new PaymentMethod("cash", false);
        provider1.getAcceptedPaymentMethods().add(cash);
        provider4.getAcceptedPaymentMethods().add(cash);
        cash.getAcceptingProviders().add(provider1);
        cash.getAcceptingProviders().add(provider4);

        Service hairCut = new Service("Hair cut");
        Service dentalFilling = new Service("Dental filling");

        ProviderService prov1DentalFilling = new ProviderService(provider1, dentalFilling, 1800000L /* 30 min */);
        ProviderService prov3HairCut = new ProviderService(provider3, hairCut, 1800000L /* 30 min */);

        Client client1 = new Client("some client");

        ProviderRating prov1Rating = new ProviderRating(provider1, client1, (short) 5);
        ProviderRating prov2Rating = new ProviderRating(provider2, client1, (short) 6);

        providerFacade.create(provider1);
        providerFacade.create(provider2);
        providerFacade.create(provider3);
        providerFacade.create(provider4);
        industryFacade.create(industry1);
        corporationFacade.create(corporation1);
        corporationFacade.create(corporation2);
        paymentMethodFacade.create(cash);
        serviceFacade.create(hairCut);
        serviceFacade.create(dentalFilling);
        providerServiceFacade.create(prov1DentalFilling);
        providerServiceFacade.create(prov3HairCut);
        clientFacade.create(client1);
        providerRatingFacade.create(prov1Rating);
        providerRatingFacade.create(prov2Rating);

    }


}
