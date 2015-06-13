package pl.salonea.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="industry")
@Access(AccessType.PROPERTY)
public class Industry implements Serializable {

    private Long industryId;
    private String name;
    private String description;

    private Set<Provider> providers;

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
}
