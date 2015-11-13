package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"industryId", "name", "description", "links"})

@Entity
@Table(name="industry")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Industry.FIND_ALL_EAGERLY, query = "SELECT DISTINCT i FROM Industry i LEFT JOIN FETCH i.providers"),
        @NamedQuery(name = Industry.FIND_BY_ID_EAGERLY, query = "SELECT i FROM Industry i LEFT JOIN FETCH i.providers WHERE i.industryId = :industryId"),
        @NamedQuery(name = Industry.FIND_FOR_NAME, query = "SELECT i FROM Industry i WHERE i.name = :name"),
        @NamedQuery(name = Industry.FIND_BY_NAME, query = "SELECT i FROM Industry i WHERE i.name LIKE :name"),
        @NamedQuery(name = Industry.FIND_BY_DESCRIPTION, query = "SELECT i FROM Industry i WHERE i.description LIKE :description"),
        @NamedQuery(name = Industry.FIND_BY_KEYWORD, query = "SELECT i FROM Industry i WHERE i.name LIKE :keyword OR i.description LIKE :keyword"),
        @NamedQuery(name = Industry.FIND_BY_PROVIDER, query = "SELECT i FROM Industry i WHERE :provider MEMBER OF i.providers"),
        @NamedQuery(name = Industry.FIND_BY_PROVIDER_EAGERLY, query = "SELECT i FROM Industry i INNER JOIN FETCH i.providers p  WHERE p = :provider")
})
public class Industry implements Serializable {

    public static final String FIND_ALL_EAGERLY = "Industry.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Industry.findByIdEagerly";
    public static final String FIND_FOR_NAME = "Industry.findForName";
    public static final String FIND_BY_NAME = "Industry.findByName";
    public static final String FIND_BY_DESCRIPTION = "Industry.findByDescription";
    public static final String FIND_BY_KEYWORD = "Industry.findByKeyword";
    public static final String FIND_BY_PROVIDER = "Industry.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "Industry.findByProviderEagerly";

    private Long industryId;
    private String name;
    private String description;

    private Set<Provider> providers = new HashSet<>();

    // HATEOAS support for RESTFul web service in JAX-RS
    private List<Link> links = new ArrayList<>();

    /* constructors */

    public Industry() {}

    public Industry(String name) {
        this.name = name;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "industry_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED" */)
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
    @XmlTransient
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

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @Transient
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
