package pl.salonea.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "service_category")
@Access(AccessType.PROPERTY)
public class ServiceCategory {

    private Integer categoryId;
    private String categoryName;
    private String description;

    /* many-to-one relationship */
    private ServiceCategory superCategory;

    /* one-to-many relationship */
    private Set<ServiceCategory> subCategories;
    private Set<Service> services;

    /* constructors */

    public ServiceCategory() { }

    public ServiceCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public ServiceCategory(ServiceCategory superCategory, String categoryName) {
        this.superCategory = superCategory;
        this.categoryName = categoryName;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "category_id", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @NotNull
    @Size(min = 2, max = 45)
    @Column(name = "category_name", nullable = false, unique = true, length = 45)
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* many-to-one relationship */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "super_category_id", referencedColumnName = "category_id", columnDefinition = "INT UNSIGNED DEFAULT NULL")
    public ServiceCategory getSuperCategory() {
        return superCategory;
    }

    public void setSuperCategory(ServiceCategory superCategory) {
        this.superCategory = superCategory;
    }

    /* one-to-many relationship */

    @OneToMany(mappedBy = "superCategory", fetch = FetchType.LAZY)
    public Set<ServiceCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<ServiceCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @OneToMany(mappedBy = "serviceCategory", fetch = FetchType.LAZY)
    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }
}
