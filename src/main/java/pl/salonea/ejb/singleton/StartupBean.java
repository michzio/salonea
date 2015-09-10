package pl.salonea.ejb.singleton;

import pl.salonea.ejb.stateless.FirmFacade;
import pl.salonea.ejb.stateless.NaturalPersonFacade;
import pl.salonea.ejb.stateless.UserAccountFacade;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Firm;
import pl.salonea.entities.NaturalPerson;
import pl.salonea.entities.UserAccount;
import pl.salonea.enums.Gender;

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

    public StartupBean() { }

    @PostConstruct
    public void onStartup() {

        populateDatabase();
    }

    private void populateDatabase() {
        logger.info("populating database with sample entities on application startup...");

        UserAccount user1 = new UserAccount("michzio@hotmail.com", "michzio", "sAmPL3#e");
        UserAccount user2 = new UserAccount("alicja@krainaczarow.com", "alicja", "zAczka!00");
        userAccountFacade.create(user1);
        userAccountFacade.create(user2);

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
}
