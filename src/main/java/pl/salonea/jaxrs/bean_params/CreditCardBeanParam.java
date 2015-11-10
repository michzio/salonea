package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.entities.Client;
import pl.salonea.enums.CreditCardType;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 10/11/2015.
 */
public class CreditCardBeanParam extends PaginationBeanParam {

    private @QueryParam("clientId") List<Long> clientIds;
    private @QueryParam("cardType") List<CreditCardType> cardTypes;
    private @QueryParam("cardNumber") String cardNumber;
    private @QueryParam("cardHolder") String cardHolder;
    private @QueryParam("expired") Boolean expired;
    private @QueryParam("theEarliestExpirationDate") RESTDateTime theEarliestExpirationDate;
    private @QueryParam("theLatestExpirationDate") RESTDateTime theLatestExpirationDate;

    @Inject
    private ClientFacade clientFacade;

    public List<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<Client> getClients() {
        if(getClientIds() != null && getClientIds().size() > 0) {
            final List<Client> clients = clientFacade.find( new ArrayList<>(getClientIds()) );
            if(clients.size() != getClientIds().size()) throw new NotFoundException("Could not find clients for all provided ids.");
            return clients;
        }
        return null;
    }

    public List<CreditCardType> getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(List<CreditCardType> cardTypes) {
        this.cardTypes = cardTypes;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public RESTDateTime getTheEarliestExpirationDate() {
        return theEarliestExpirationDate;
    }

    public void setTheEarliestExpirationDate(RESTDateTime theEarliestExpirationDate) {
        this.theEarliestExpirationDate = theEarliestExpirationDate;
    }

    public RESTDateTime getTheLatestExpirationDate() {
        return theLatestExpirationDate;
    }

    public void setTheLatestExpirationDate(RESTDateTime theLatestExpirationDate) {
        this.theLatestExpirationDate = theLatestExpirationDate;
    }
}
