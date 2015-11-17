package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderPaymentMethodRelationshipManagerInterface;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 17/11/2015.
 */
@Stateless
@LocalBean
public class ProviderPaymentMethodRelationshipManager
        implements ProviderPaymentMethodRelationshipManagerInterface.Local, ProviderPaymentMethodRelationshipManagerInterface.Remote {


    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private PaymentMethodFacade paymentMethodFacade;

    @Override
    public void addProviderAcceptPaymentMethod(Long providerId, Integer paymentMethodId) throws NotFoundException {

        Provider provider = providerFacade.find(providerId);
        if(provider == null)
            throw new NotFoundException("Provider entity could not be found for id " + providerId);
        PaymentMethod paymentMethod = paymentMethodFacade.find(paymentMethodId);
        if(paymentMethod == null)
            throw new NotFoundException("Payment Method entity could not be found for id " + paymentMethodId);

        provider.getAcceptedPaymentMethods().add(paymentMethod);
        paymentMethod.getAcceptingProviders().add(provider);

    }

    @Override
    public void removeProviderAcceptPaymentMethod(Long providerId, Integer paymentMethodId) throws NotFoundException {

        Provider provider = providerFacade.find(providerId);
        if(provider == null)
            throw new NotFoundException("Provider entity could not be found for id " + providerId);
        PaymentMethod paymentMethod = paymentMethodFacade.find(paymentMethodId);
        if(paymentMethod == null)
            throw new NotFoundException("Payment Method entity could not be found for id " + paymentMethodId);

        provider.getAcceptedPaymentMethods().remove(paymentMethod);
        paymentMethod.getAcceptingProviders().remove(provider);

    }
}
