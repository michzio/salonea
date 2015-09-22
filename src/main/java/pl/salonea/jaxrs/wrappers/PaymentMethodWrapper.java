package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.Provider;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 21/09/2015.
 */
@XmlRootElement(name = "payment-method")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PaymentMethodWrapper {

    private PaymentMethod paymentMethod;
    private Set<Provider> providers;

    // default no-args constructor
    public PaymentMethodWrapper() { }

    public PaymentMethodWrapper(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.providers = paymentMethod.getAcceptingProviders();
    }

    public static List<PaymentMethodWrapper> wrap(List<PaymentMethod> paymentMethods) {

        List<PaymentMethodWrapper> wrappedPaymentMethods = new ArrayList<>();

        for(PaymentMethod paymentMethod : paymentMethods)
            wrappedPaymentMethods.add(new PaymentMethodWrapper(paymentMethod));

        return wrappedPaymentMethods;
    }

    @XmlElement(name = "entity", nillable = true)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @XmlElement(name = "providers", nillable = true)
    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }
}
