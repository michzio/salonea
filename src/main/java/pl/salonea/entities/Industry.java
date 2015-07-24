package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="industry")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Industry.FIND_FOR_NAME, query = "SELECT i FROM Industry i WHERE i.name = :name"),
        @NamedQuery(name = Industry.FIND_BY_NAME, query = "SELECT i FROM Industry i WHERE i.name LIKE :name"),
        @NamedQuery(name = Industry.FIND_BY_PROVIDER, query = "SELECT i FROM Industry i WHERE :provider MEMBER OF i.providers")
})
public class Industry implements Serializable {

    public static final String FIND_FOR_NAME = "Industry.findForName";
    public static final String FIND_BY_NAME = "Industry.findByName";
    public static final String FIND_BY_PROVIDER = "Industry.findByProvider";

    private Long industryId;
    private String name;
    private String description;

    private Set<Provider> providers = new HashSet<>();

    /* constructors */

    public Industry() {}

    public Industry(String name) {
        this.name = name;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "industry_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getIndustryId() {
        return industryId;
    }

    public void setIndustryId(Long industryId) {
        this.industryId = industryId;
    }

    @NotNull
    @Size(min=2, max=45)
    @Column(name = "industry_name", nullable = false, unique = true, length = 45)
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

    /* many-to-many relationship */

    @ManyToMany
    @JoinTable(name = "provider_industry",
        joinColumns = @JoinColumn(name = "industry_id"),
        inverseJoinColumns = @JoinColumn(name = "provider_id")
    )
    // @OrderBy("providerName ASC") - Hibernate + MySQL error
    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
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
        if (obj == null || !(obj instanceof Industry))
            return false;
        if (obj == this)
            return true;

        Industry rhs = (Industry) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(getName(), rhs.getName()).
                isEquals();
    }
}
