package pl.salonea.ejb.interfaces;

/**
 * Created by michzio on 17/11/2015.
 */
public interface ProviderPaymentMethodRelationshipManagerInterface {

    void addProviderAcceptPaymentMethod(Long providerId, Integer paymentMethodId);
    void removeProviderAcceptPaymentMethod(Long providerId, Integer paymentMethodId);

    @javax.ejb.Remote
    interface Remote extends ProviderPaymentMethodRelationshipManagerInterface { }

    @javax.ejb.Local
    interface Local extends ProviderPaymentMethodRelationshipManagerInterface { }
}
