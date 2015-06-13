package pl.salonea.entities;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "payment_method")
@Access(AccessType.PROPERTY)
public class PaymentMethod implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Boolean inAdvance;

    private Set<Provider> acceptingProviders;

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
    @Column(name = "payment_method_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getId() {
        return id;
    }

    public void setId(Long paymentMethodId) {
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

    @ManyToMany(mappedBy = "acceptedPaymentMethods")
    public Set<Provider> getAcceptingProviders() {
        return acceptingProviders;
    }

    public void setAcceptingProviders(Set<Provider> acceptingProviders) {
        this.acceptingProviders = acceptingProviders;
    }
}
