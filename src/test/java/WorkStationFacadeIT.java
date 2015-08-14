import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import pl.salonea.ejb.interfaces.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.WorkStation;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.ProviderType;
import pl.salonea.enums.WorkStationType;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * WorkStationFacade Tester.
 *
 * @author Michal Ziobro
 * @since <pre>Aug 11, 2015</pre>
 * @version 1.0
 */
@RunWith(Arquillian.class)
public class WorkStationFacadeIT {

    private static final Logger logger = Logger.getLogger(WorkStationFacadeIT.class.getName());

    @Deployment
    public static WebArchive createDeployment() {

        File[] dependencies = Maven.resolver().resolve(
                "org.slf4j:slf4j-simple:1.7.7"
        )
                .withoutTransitivity().asFile();

        WebArchive war = ShrinkWrap.create(ZipImporter.class, "salonea-1.0.war")
                .importFrom(new File("./out/artifacts/salonea/salonea-1.0.war"))
                .as(WebArchive.class);

        war.addAsLibraries(dependencies);

        return war;
    }

    @Inject
    private WorkStationFacadeInterface.Local workStationFacade;

    @Inject
    private ServicePointFacadeInterface.Local pointFacade;

    @Inject
    private ProviderFacadeInterface.Local providerFacade;

    @Inject
    private ProviderServiceFacadeInterface.Local providerServiceFacade;

    @Inject
    private ServiceFacadeInterface.Local serviceFacade;

    @Inject
    private UserTransaction utx;

    @Test
    public void shouldCreateNewWorkStation() throws Exception {

        // create instance of Provider
        Address address = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@medical24.pl", "medical24", "mEDiC24@", "Medical24 Sp. z o.o.",
                "2234567890", "2234567890", address, "Medical 24 Poland", ProviderType.SIMPLE);

        // create instance of ServicePoint
        ServicePoint servicePoint = new ServicePoint(provider, 1, address);

        // create instance of WorkStation
        WorkStation workStation = new WorkStation(servicePoint, 1, WorkStationType.ROOM);

        // persist all entities
        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(servicePoint);
        workStationFacade.create(workStation);
        utx.commit();

        assertTrue("There should be one WorkStation persisted in database.", workStationFacade.count() == 1);

        WorkStation foundWorkStation = workStationFacade.find( new WorkStationId(provider.getUserId(),
                                                                 servicePoint.getServicePointNumber(), workStation.getWorkStationNumber()) );
        assertEquals("WorkStation persisted and found in database should be the same.", workStation, foundWorkStation);

        providerFacade.remove(provider);

        assertTrue("There should not be any Work Station entity in database.", workStationFacade.count() == 0);
        assertTrue("There should not be any Service Point entity in database.", pointFacade.count() == 0);
        assertTrue("There should not be any Provider entity in database.", providerFacade.count() == 0);
    }

    @Test
    public void shouldFindWorkStationByServicePoint() throws Exception {

        // create instance of Provider entity
        Address address1 =  new Address("Poznanska", "25", "29-100", "Poznan", "Wielkopolska", "Poland");
        Address address2 = new Address("Wielkopolska", "15", "29-100", "Poznan", "Wielkopolska", "Poland");
        Address address3 = new Address("Warszawska", "50", "61-066", "Poznan", "Wielkopolska", "Poland");
        Provider provider = new Provider("firma@fryzjer.pl", "fryzjer_pl", "aAle2@_", "FryzjerPl Sp. z o.o..",
                "2234567890", "2234567890", address1, "FryzjerPl", ProviderType.SIMPLE);

        // create some instances of ServicePoint entity
        ServicePoint point1 = new ServicePoint(provider, 1, address1);
        ServicePoint point2 = new ServicePoint(provider, 2, address2);
        ServicePoint point3 = new ServicePoint(provider, 3, address3);

        // create some instances of WorkStation entity
        WorkStation station11 = new WorkStation(point1, 1, WorkStationType.ROOM);
        WorkStation station12 = new WorkStation(point1, 2, WorkStationType.OFFICE);
        WorkStation station13 = new WorkStation(point1, 3, WorkStationType.OFFICE);
        WorkStation station21 = new WorkStation(point2, 1, WorkStationType.ROOM);
        WorkStation station22 = new WorkStation(point2, 2, WorkStationType.OFFICE);
        WorkStation station31 = new WorkStation(point3, 1, WorkStationType.OFFICE);
        WorkStation station32 = new WorkStation(point3, 2, WorkStationType.OFFICE);

        utx.begin();
        providerFacade.create(provider);
        pointFacade.create(point1);
        pointFacade.create(point2);
        pointFacade.create(point3);
        workStationFacade.create(station11);
        workStationFacade.create(station12);
        workStationFacade.create(station13);
        workStationFacade.create(station21);
        workStationFacade.create(station22);
        workStationFacade.create(station31);
        workStationFacade.create(station32);
        utx.commit();

        assertTrue("There should be one provider persisted in database.", providerFacade.count() == 1);
        assertTrue("There should be three service points persisted in database.", pointFacade.count() == 3);
        assertTrue("There should be seven work stations persisted in database.", workStationFacade.count() == 7);

        utx.begin();
        point1 = pointFacade.find( new ServicePointId(point1.getProvider().getUserId(), point1.getServicePointNumber()) );
        List<WorkStation> firstPointStations = workStationFacade.findByServicePoint(point1);
        utx.commit();
        assertTrue("There should be three work stations for given service point.", firstPointStations.size() == 3);
        assertTrue(firstPointStations.contains(station11) && firstPointStations.contains(station12) && firstPointStations.contains(station13));

        utx.begin();
        point2 = pointFacade.find( new ServicePointId(point2.getProvider().getUserId(), point2.getServicePointNumber()) );
        List<WorkStation> secondPointStations = workStationFacade.findByServicePoint(point2);
        utx.commit();
        assertTrue("There should be two work stations for given service point.", secondPointStations.size() == 2);
        assertTrue(secondPointStations.contains(station21) && secondPointStations.contains(station22));

        utx.begin();
        point3 = pointFacade.find( new ServicePointId(point3.getProvider().getUserId(), point3.getServicePointNumber()) );
        List<WorkStation> thirdPointStations = workStationFacade.findByServicePoint(point3);
        utx.commit();
        assertTrue("There should be two work stations for given service point.", thirdPointStations.size() == 2);
        assertTrue(thirdPointStations.contains(station31) && thirdPointStations.contains(station32));

        List<WorkStation> roomStations = workStationFacade.findByType(WorkStationType.ROOM);
        assertTrue("There should be two room work stations in database.", roomStations.size() == 2);
        assertTrue(roomStations.contains(station11) && roomStations.contains(station21));

        List<WorkStation> officeStations = workStationFacade.findByType(WorkStationType.OFFICE);
        assertTrue("There should be five office work stations in database.", officeStations.size() == 5);
        assertTrue(officeStations.contains(station12) && officeStations.contains(station13) && officeStations.contains(station22)
                    && officeStations.contains(station31) && officeStations.contains(station32));

        utx.begin();
        point1 = pointFacade.find( new ServicePointId(point1.getProvider().getUserId(), point1.getServicePointNumber()) );
        List<WorkStation> firstPointOffices = workStationFacade.findByServicePointAndType(point1, WorkStationType.OFFICE);
        utx.commit();
        assertTrue("There should be two office work stations for first service point.", firstPointOffices.size() == 2);
        assertTrue(firstPointOffices.contains(station12) && firstPointOffices.contains(station13));

        utx.begin();
        point2 = pointFacade.find( new ServicePointId(point2.getProvider().getUserId(), point2.getServicePointNumber()) );
        List<WorkStation> secondPointRooms = workStationFacade.findByServicePointAndType(point2, WorkStationType.ROOM);
        utx.commit();
        assertTrue("There should be one room work station for second service point.", secondPointRooms.size() == 1);
        assertTrue(secondPointRooms.contains(station21));

        // delete by Service Point
        utx.begin();
        point3 = pointFacade.find( new ServicePointId(point3.getProvider().getUserId(), point3.getServicePointNumber()) );
        assertTrue(workStationFacade.deleteByServicePoint(point3) == 2);
        utx.commit();

        assertTrue("There should remain five work stations in database.", workStationFacade.count() == 5);

        // remove all entities
        providerFacade.remove(provider);

        assertTrue("There should not be any provider in database.", providerFacade.count() == 0);
        assertTrue("There should not be any service point in database.", pointFacade.count() == 0);
        assertTrue("There should not be any work station in database.", workStationFacade.count() == 0);
    }

    // TODO implement other integration test methods to find work stations by service, provider service, employee and term
}
