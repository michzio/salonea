package pl.salonea.ejb.singleton;

import pl.salonea.ejb.stateless.UserAccountFacade;
import pl.salonea.entities.UserAccount;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
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
    }
}
