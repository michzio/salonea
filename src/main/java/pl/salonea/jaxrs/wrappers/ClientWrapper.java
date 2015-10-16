package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 14/10/2015.
 */
@XmlRootElement(name = "client")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ClientWrapper {

    private Client client;
    private NaturalPerson naturalPerson;
    private Firm firm;
    private Set<CreditCard> creditCards;
    private Set<ProviderRating> providerRatings;
    private Set<EmployeeRating> employeeRatings;

    // default no-args constructor
    public ClientWrapper() { }

    public ClientWrapper(Client client) {
        this.client = client;
        this.naturalPerson = client.getNaturalPerson();
        this.firm = client.getFirm();
        this.creditCards = client.getCreditCards();
        this.providerRatings = client.getProviderRatings();
        this.employeeRatings = client.getEmployeeRatings();
    }

    public static List<ClientWrapper> wrap(List<Client> clients) {

        List<ClientWrapper> wrappedClients = new ArrayList<>();

        for(Client client : clients)
            wrappedClients.add(new ClientWrapper(client));

        return wrappedClients;
    }

    @XmlElement(name = "entity", nillable = true)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @XmlElement(name = "natural-person", nillable = true)
    public NaturalPerson getNaturalPerson() {
        return naturalPerson;
    }

    public void setNaturalPerson(NaturalPerson naturalPerson) {
        this.naturalPerson = naturalPerson;
    }

    @XmlElement(name = "firm", nillable = true)
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    @XmlElement(name = "credit-cards", nillable = true)
    public Set<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(Set<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    @XmlElement(name = "provider-ratings", nillable = true)
    public Set<ProviderRating> getProviderRatings() {
        return providerRatings;
    }

    public void setProviderRatings(Set<ProviderRating> providerRatings) {
        this.providerRatings = providerRatings;
    }

    @XmlElement(name = "employee-ratings", nillable = true)
    public Set<EmployeeRating> getEmployeeRatings() {
        return employeeRatings;
    }

    public void setEmployeeRatings(Set<EmployeeRating> employeeRatings) {
        this.employeeRatings = employeeRatings;
    }
}
