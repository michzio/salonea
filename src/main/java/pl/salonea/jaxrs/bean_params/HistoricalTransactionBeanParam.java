package pl.salonea.jaxrs.bean_params;

import pl.salonea.enums.TransactionCompletionStatus;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by michzio on 10/05/2016.
 */
public class HistoricalTransactionBeanParam extends TransactionBeanParam {

    private @QueryParam("completionStatus") List<TransactionCompletionStatus> completionStatuses;
    private @QueryParam("minClientRating") Short minClientRating;
    private @QueryParam("maxClientRating") Short maxClientRating;
    private @QueryParam("clientComment") List<String> clientComments;
    private @QueryParam("minProviderRating") Short minProviderRating;
    private @QueryParam("maxProviderRating") Short maxProviderRating;
    private @QueryParam("providerDementi") List<String> providerDementis;

    public List<TransactionCompletionStatus> getCompletionStatuses() {
        return completionStatuses;
    }

    public void setCompletionStatuses(List<TransactionCompletionStatus> completionStatuses) {
        this.completionStatuses = completionStatuses;
    }

    public Short getMinClientRating() {
        return minClientRating;
    }

    public void setMinClientRating(Short minClientRating) {
        this.minClientRating = minClientRating;
    }

    public Short getMaxClientRating() {
        return maxClientRating;
    }

    public void setMaxClientRating(Short maxClientRating) {
        this.maxClientRating = maxClientRating;
    }

    public List<String> getClientComments() {
        return clientComments;
    }

    public void setClientComments(List<String> clientComments) {
        this.clientComments = clientComments;
    }

    public Short getMinProviderRating() {
        return minProviderRating;
    }

    public void setMinProviderRating(Short minProviderRating) {
        this.minProviderRating = minProviderRating;
    }

    public Short getMaxProviderRating() {
        return maxProviderRating;
    }

    public void setMaxProviderRating(Short maxProviderRating) {
        this.maxProviderRating = maxProviderRating;
    }

    public List<String> getProviderDementis() {
        return providerDementis;
    }

    public void setProviderDementis(List<String> providerDementis) {
        this.providerDementis = providerDementis;
    }
}
