package pl.salonea.ejb.singleton;

import pl.salonea.ejb.stateless.*;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.enums.CreditCardType;
import pl.salonea.enums.Gender;
import pl.salonea.enums.ProviderType;
import pl.salonea.enums.WorkStationType;

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
    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private WorkStationFacade workStationFacade;
    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;
    @Inject
    private EmployeeFacade employeeFacade;
    @Inject
    private EmployeeRatingFacade employeeRatingFacade;
    @Inject
    private CreditCardFacade cardFacade;
    @Inject
    private TermFacade termFacade;
    @Inject
    private TermEmployeeWorkOnFacade termEmployeeWorkOnFacade;
    @Inject
    private ServicePointPhotoFacade photoFacade;
    @Inject
    private TagFacade tagFacade;
    @Inject
    private VirtualTourFacade virtualTourFacade;
    @Inject
    private SkillFacade skillFacade;
    @Inject
    private EducationFacade educationFacade;

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
        populateClients();
        populateCreditCards();
        populatePaymentMethods();
        populateEmployeeWorkOnWorkStation();
        populateServicePointAssets();
        populateSkillsAndEducation();
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

    private void populateClients() {

        Client client3 = new Client("personal client 3");
        Client client4 = new Client("personal client 4");
        Client client5 = new Client("firm client 5");
        Client client6 = new Client("firm client 6");
        Client client7 = new Client("provider client 7");
        Client client11 = new Client("employee client 11");

        NaturalPerson person3 = naturalPersonFacade.find(3l);
        NaturalPerson person4 = naturalPersonFacade.find(4l);
        Firm firm5 = firmFacade.find(5l);
        Firm firm6 = firmFacade.find(6l);
        Provider provider7 = providerFacade.find(7l);
        Employee employee11 = employeeFacade.find(11l);

        person3.setClient(client3);
        person4.setClient(client4);
        firm5.setClient(client5);
        firm6.setClient(client6);
        provider7.setClient(client7);
        employee11.setClient(client11);

        client3.setNaturalPerson(person3);
        client4.setNaturalPerson(person4);
        client5.setFirm(firm5);
        client6.setFirm(firm6);
        client7.setFirm(provider7);
        client11.setNaturalPerson(employee11);

        clientFacade.create(client3);
        clientFacade.create(client4);
        clientFacade.create(client5);
        clientFacade.create(client6);
        clientFacade.create(client7);
        clientFacade.create(client11);

        naturalPersonFacade.update(person3);
        naturalPersonFacade.update(person4);
        firmFacade.update(firm5);
        firmFacade.update(firm6);
        providerFacade.update(provider7);
        employeeFacade.update(employee11);
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

        Calendar openingDate = Calendar.getInstance();
        openingDate.add(Calendar.YEAR, -10);

        Address address5 = new Address("Wrocławska", "15", "29-100", "Kraków", "Małopolska", "Poland");
        Corporation corporation1 = new Corporation("Medical Corporation", "medical.png", address5);
        corporation1.setOpeningDate(openingDate.getTime());

        Address address6 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Corporation corporation2 = new Corporation("Hair and Style Corporation", "hair_and_style.png", address6);
        openingDate.add(Calendar.YEAR, -10);
        corporation2.setOpeningDate(openingDate.getTime());

        provider1.setCorporation(corporation1);
        provider2.setCorporation(corporation1);
        provider3.setCorporation(corporation2);

        PaymentMethod cash = new PaymentMethod("cash", false);
        cash.setDescription("This method enables to pay in cash after service.");
        provider1.getAcceptedPaymentMethods().add(cash);
        provider4.getAcceptedPaymentMethods().add(cash);
        cash.getAcceptingProviders().add(provider1);
        cash.getAcceptingProviders().add(provider4);

        Service hairCut = new Service("Hair cut");
        Service dentalFilling = new Service("Dental filling");

        ServiceCategory medicalServices = new ServiceCategory("Medical Services");
        ServiceCategory hairDressingServices = new ServiceCategory("Hairdressing Services");

        medicalServices.getServices().add(dentalFilling);
        hairDressingServices.getServices().add(hairCut);
        dentalFilling.setServiceCategory(medicalServices);
        hairCut.setServiceCategory(hairDressingServices);

        ProviderService prov1DentalFilling = new ProviderService(provider1, dentalFilling, 1800000L /* 30 min */);
        ProviderService prov3HairCut = new ProviderService(provider3, hairCut, 1800000L /* 30 min */);

        // create instance of Employee
        Date dateOfBirth = new GregorianCalendar(1988, Calendar.OCTOBER, 3).getTime();
        Employee hairDresser = new Employee("hairdresser.1@hairstyles.com", "hairdresser", "pAs12#", "Tomasz", "Fryzjer", dateOfBirth, Gender.male, "assistant");

        hairDresser.getSuppliedServices().add(prov3HairCut);
        prov3HairCut.getSupplyingEmployees().add(hairDresser);

        Client client1 = new Client("some client");
        Client client2 = new Client("some client");

        ProviderRating prov1Rating = new ProviderRating(provider1, client1, (short) 5);
        ProviderRating prov2Rating = new ProviderRating(provider2, client1, (short) 6);

        EmployeeRating emplRating1 = new EmployeeRating(hairDresser, client1, (short) 7);
        EmployeeRating emplRating2 = new EmployeeRating(hairDresser, client2, (short) 4);

        Address address11 = new Address("Poznańska", "15", "29-100", "Poznań", "Wielkopolska", "Poland");
        Address address12 = new Address("Pomorska", "109", "30-102", "Luboń", "Wielkopolska", "Poland");
        ServicePoint point11 = new ServicePoint(provider1, 1, address11);
                     point11.setLongitudeWGS84(16.908212f);
                     point11.setLatitudeWGS84(52.413730f);
        ServicePoint point12 = new ServicePoint(provider1, 2, address12);
                     point12.setLongitudeWGS84(16.916990f);
                     point12.setLatitudeWGS84(52.417640f);

        WorkStation workStation111 = new WorkStation(point11, 1, WorkStationType.ROOM);
        WorkStation workStation112 = new WorkStation(point11, 2, WorkStationType.OFFICE);
        WorkStation workStation121 = new WorkStation(point12, 1, WorkStationType.CHAIR);
        WorkStation workStation122 = new WorkStation(point12, 2, WorkStationType.CHAIR);

        prov1DentalFilling.getWorkStations().add(workStation111);
        prov1DentalFilling.getWorkStations().add(workStation121);
        workStation111.getProvidedServices().add(prov1DentalFilling);
        workStation121.getProvidedServices().add(prov1DentalFilling);

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
        clientFacade.create(client2);
        providerRatingFacade.create(prov1Rating);
        providerRatingFacade.create(prov2Rating);
        servicePointFacade.create(point11);
        servicePointFacade.create(point12);
        workStationFacade.create(workStation111);
        workStationFacade.create(workStation112);
        workStationFacade.create(workStation121);
        workStationFacade.create(workStation122);
        serviceCategoryFacade.create(medicalServices);
        serviceCategoryFacade.create(hairDressingServices);
        employeeFacade.create(hairDresser);
        employeeRatingFacade.create(emplRating1);
        employeeRatingFacade.create(emplRating2);

    }

    private void populateCreditCards() {

        // find currently stored clients
        Client foundClient1 = clientFacade.find(1L);
        Client foundClient2 = clientFacade.find(2L);

        // calculate expiration date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);

        if(foundClient1 != null) {
            CreditCard card1 = new CreditCard(foundClient1, "4539345259115377", cal.getTime(), "Michał Ziobro", CreditCardType.VISA);
            cal.add(Calendar.YEAR, 2);
            CreditCard card2 = new CreditCard(foundClient1, "4485249741691678", cal.getTime(), "Michał Ziobro", CreditCardType.MASTERCARD);

            foundClient1.getCreditCards().add(card1);
            foundClient1.getCreditCards().add(card2);
            cardFacade.create(card1);
            cardFacade.create(card2);
        }

        if(foundClient2 != null) {
            CreditCard card3 = new CreditCard(foundClient2, "4532661830539670", cal.getTime(), "Jan Nowak", CreditCardType.AMERICAN_EXPRESS);
            cal.add(Calendar.MONTH, -10);
            CreditCard card4 = new CreditCard(foundClient2, "5266864683957747", cal.getTime(), "Jan Nowak", CreditCardType.VISA);

            foundClient2.getCreditCards().add(card3);
            foundClient2.getCreditCards().add(card4);

            cardFacade.create(card3);
            cardFacade.create(card4);
        }
    }

    private void populatePaymentMethods() {

        PaymentMethod creditCardInAdvance = new PaymentMethod("Credit Card", true);
        creditCardInAdvance.setDescription("Paying by credit card via internet in advance.");
        PaymentMethod creditCardInTerminal = new PaymentMethod("Credit Card in Terminal", false);
        creditCardInTerminal.setDescription("Paying by credit card in terminal after service on spot.");
        PaymentMethod bankTransfer = new PaymentMethod("Bank Transfer", true);
        bankTransfer.setDescription("Bank Transfer in advance");
        PaymentMethod transactionalSystem = new PaymentMethod("Transactional System", true);
        transactionalSystem.setDescription("Paying by transactional system provided by third party company.");

        paymentMethodFacade.create(creditCardInAdvance);
        paymentMethodFacade.create(creditCardInTerminal);
        paymentMethodFacade.create(bankTransfer);
        paymentMethodFacade.create(transactionalSystem);
    }

    private void populateEmployeeWorkOnWorkStation() {

        Provider hairdressingProvider = providerFacade.find(9L);
        Employee hairdresserEmployee = employeeFacade.find(11L);

        Address hairdressingPointAddress = new Address("Mazowiecka", "160", "00-001", "Warszawa", "Mazowieckie", "Poland");
        ServicePoint hairdressingPoint = new ServicePoint(hairdressingProvider, 1, hairdressingPointAddress);
        hairdressingPoint.setLongitudeWGS84(21.0049911f);
        hairdressingPoint.setLatitudeWGS84(52.2261594f);

        WorkStation hairdressingChair1 = new WorkStation(hairdressingPoint, 1, WorkStationType.CHAIR);
        WorkStation hairdressingChair2 = new WorkStation(hairdressingPoint, 2, WorkStationType.CHAIR);

        servicePointFacade.create(hairdressingPoint);
        workStationFacade.create(hairdressingChair1);
        workStationFacade.create(hairdressingChair2);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.DECEMBER, 20, 9, 0, 0);
        Date startDate20122017 = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        Date endDate20122017 = calendar.getTime();

        calendar.set(2018, Calendar.JANUARY, 10, 8, 0, 0);
        Date startDate10012018 = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        Date endDate10012018 = calendar.getTime();

        Term term20122017 = new Term(startDate20122017, endDate20122017);
        Term term10012018 = new Term(startDate10012018, endDate10012018);

        TermEmployeeWorkOn hairdresserTermOnChair1 = new TermEmployeeWorkOn(hairdresserEmployee, term20122017, hairdressingChair1);
        TermEmployeeWorkOn hairdresserTermOnChair2 = new TermEmployeeWorkOn(hairdresserEmployee, term10012018, hairdressingChair2);

        termFacade.create(term20122017);
        termFacade.create(term10012018);
        termEmployeeWorkOnFacade.create(hairdresserTermOnChair1);
        termEmployeeWorkOnFacade.create(hairdresserTermOnChair2);
    }

    private void populateServicePointAssets() {

        ServicePoint servicePoint = servicePointFacade.find(new ServicePointId(7L, 2));

        ServicePointPhoto photo1 = new ServicePointPhoto("photo1OfServicePoint72.png", servicePoint);
        ServicePointPhoto photo2 = new ServicePointPhoto("photo2OfServicePoint72.png", servicePoint);
        photo1.setDescription("Reception photo.");
        photo2.setDescription("Room photo.");

        photoFacade.create(photo1);
        photoFacade.create(photo2);

        VirtualTour tour1 = new VirtualTour("virtualTour1OfServicePoint72.swf", servicePoint);
        VirtualTour tour2 = new VirtualTour("virtualTour2OfServicePoint72.swf", servicePoint);
        tour1.setDescription("Reception virtual tour movie.");
        tour2.setDescription("Room virtual tour movie.");

        virtualTourFacade.create(tour1);
        virtualTourFacade.create(tour2);

        Tag roomTag = new Tag("room");
        Tag receptionTag = new Tag("reception");

        tagFacade.create(roomTag);
        tagFacade.create(receptionTag);

        photo1.getTags().add(receptionTag);
        photo2.getTags().add(roomTag);
        tour1.getTags().add(receptionTag);
        tour2.getTags().add(roomTag);

        roomTag.getTaggedPhotos().add(photo2);
        roomTag.getTaggedVirtualTours().add(tour2);
        receptionTag.getTaggedPhotos().add(photo1);
        receptionTag.getTaggedVirtualTours().add(tour1);
    }

    private void populateSkillsAndEducation() {

        Employee hairdresser = employeeFacade.find(11L);
        // add some new employees
        Date dateOfBirth = new GregorianCalendar(1995, Calendar.FEBRUARY, 23).getTime();
        Employee dentist = new Employee("dentist@dentomed.pl", "dentist", "pAs12#", "Angelika", "Pstryczek", dateOfBirth, Gender.female, "specialist");
        employeeFacade.create(dentist);

        // skills
        Skill hairCutting = new Skill("Hair Cutting");
        hairCutting.setDescription("Hair Cutting is the ability to cut women or man hairs");
        Skill hairStyling = new Skill("Hair Styling");
        hairStyling.setDescription("Hair Styling is the ability to style women or man hairs");

        Skill dentalFilling = new Skill("Dental filling");
        dentalFilling.setDescription("Dental filling of teeth cavities");

        skillFacade.create(hairCutting);
        skillFacade.create(hairStyling);
        skillFacade.create(dentalFilling);

        hairdresser.getSkills().add(hairCutting);
        hairdresser.getSkills().add(hairStyling);
        dentist.getSkills().add(dentalFilling);

        hairCutting.getSkilledEmployees().add(hairdresser);
        hairStyling.getSkilledEmployees().add(hairdresser);
        dentalFilling.getSkilledEmployees().add(dentist);

        // education
        Education hairdressingCollege = new Education("Hairdressing College", "Hair Dresser Certification");
        Education dentistDegree = new Education("Collegium medicum of Jagielonian University", "Master of Dental Science");
        dentistDegree.setFaculty("Faculty of Dental Medicine");

        educationFacade.create(hairdressingCollege);
        educationFacade.create(dentistDegree);

        hairdresser.getEducations().add(hairdressingCollege);
        dentist.getEducations().add(dentistDegree);

    }

}
