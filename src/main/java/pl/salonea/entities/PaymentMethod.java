package pl.salonea.entities;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_method")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = PaymentMethod.FIND_ALL_EAGERLY, query = "SELECT pm FROM PaymentMethod pm LEFT JOIN FETCH pm.acceptingProviders"),
        @NamedQuery(name = PaymentMethod.FIND_BY_ID_EAGERLY, query = "SELECT pm FROM PaymentMethod pm LEFT JOIN FETCH pm.acceptingProviders WHERE pm.id = :paymentMethodId"),
        @NamedQuery(name = PaymentMethod.FIND_FOR_NAME, query = "SELECT pm FROM PaymentMethod pm WHERE pm.name = :name"),
        @NamedQuery(name = PaymentMethod.FIND_BY_NAME, query = "SELECT pm FROM PaymentMethod pm WHERE pm.name LIKE :name"),
        @NamedQuery(name = PaymentMethod.FIND_IN_ADVANCE, query = "SELECT pm FROM PaymentMethod pm WHERE pm.inAdvance = :in_advance"),
        @NamedQuery(name = PaymentMethod.FIND_BY_NAME_AND_IN_ADVANCE, query = "SELECT pm FROM PaymentMethod pm WHERE pm.name LIKE :name AND pm.inAdvance = :in_advance"),
        @NamedQuery(name = PaymentMethod.FIND_BY_PROVIDER, query = "SELECT pm FROM PaymentMethod pm WHERE :provider MEMBER OF pm.acceptingProviders"),
        @NamedQuery(name = PaymentMethod.FIND_BY_PROVIDER_EAGERLY, query = "SELECT pm FROM PaymentMethod pm INNER JOIN FETCH pm.acceptingProviders p WHERE p = :provider")
})
public class PaymentMethod implements Serializable {

    public static final String FIND_ALL_EAGERLY = "PaymentMethod.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "PaymentMethod.findByIdEagerly";
    public static final String FIND_FOR_NAME = "PaymentMethod.findForName";
    public static final String FIND_BY_NAME = "PaymentMethod.findByName";
    public static final String FIND_IN_ADVANCE = "PaymentMethod.findInAdvance";
    public static final String FIND_BY_NAME_AND_IN_ADVANCE = "PaymentMethod.findByNameAndInAdvance";
    public static final String FIND_BY_PROVIDER = "PaymentMethod.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "PaymentMethod.findByProviderEagerly";

    private Integer id;
    private String name;
    private String description;
    private Boolean inAdvance;

    private Set<Provider> acceptingProviders = new HashSet<>();

    // HATEOAS support for RESTFul web service in JAX-RS
    private Set<Link> links = new HashSet<>();

    /* constructors */

    public PaymentMethod() { }

    public PaymentMethod(String name) {
        this.name = name;
    }

    public PaymentMethod(String name, Boolean inAdvance) {
        this.name = name;
        this.inAdvance = inAdvance;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "payment_method_id", nullable = false /*, columnDefinition = "INT UNSIGNED" */)
    public Integer getId() {
        return id;
    }

    public void setId(Integer paymentMethodId) {
        this.id = paymentMethodId;
    }

    @NotNull
    @Size(min=2, max=45)
    @Column(name = "payment_method_name", nullable = false, unique = true, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT default NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="in_advance", nullable = false, columnDefinition = "BOOL DEFAULT 0")
    public Boolean getInAdvance() {
        return inAdvance;
    }

    public void setInAdvance(Boolean inAdvance) {
        this.inAdvance = inAdvance;
    }

    /* many-to-many relationships to providers that accept this payment method */

    @XmlTransient
    @ManyToMany(mappedBy = "acceptedPaymentMethods")
    public Set<Provider> getAcceptingProviders() {
        return acceptingProviders;
    }

    public void setAcceptingProviders(Set<Provider> acceptingProviders) {
        this.acceptingProviders = acceptingProviders;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(getName())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PaymentMethod))
            return false;
        if (obj == this)
            return true;

        PaymentMethod rhs = (PaymentMethod) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getName(), rhs.getName()).
                isEquals();
    }

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @Transient
    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }
}
