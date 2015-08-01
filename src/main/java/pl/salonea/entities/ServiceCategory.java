package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.CategoryPhrase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_category")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = ServiceCategory.FIND_BY_NAME, query = "SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.categoryName) LIKE LOWER(:name)"),
        @NamedQuery(name = ServiceCategory.FIND_BY_DESCRIPTION, query = "SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.description) LIKE LOWER(:description)"),
        @NamedQuery(name = ServiceCategory.FIND_BY_KEYWORD, query = "SELECT sc FROM ServiceCategory sc WHERE LOWER(sc.categoryName) LIKE LOWER(:keyword) OR LOWER(sc.description) LIKE LOWER(:keyword)"),
        @NamedQuery(name = ServiceCategory.FIND_BY_SUPER_CATEGORY, query = "SELECT sc FROM ServiceCategory sc WHERE sc.superCategory = :super_category"),
        @NamedQuery(name = ServiceCategory.FIND_BY_KEYWORD_IN_CATEGORY, query = "SELECT sc FROM ServiceCategory sc WHERE (LOWER(sc.categoryName) LIKE LOWER(:keyword) OR LOWER(sc.description) LIKE LOWER(:keyword)) AND sc.superCategory = :super_category"),
        @NamedQuery(name = ServiceCategory.DELETE_BY_NAME, query = "DELETE FROM ServiceCategory sc WHERE sc.categoryName = :name"),
        @NamedQuery(name = ServiceCategory.DELETE_BY_SUPER_CATEGORY, query = "DELETE FROM ServiceCategory sc WHERE sc.superCategory = :super_category")
})
public class ServiceCategory {

    public static final String FIND_BY_NAME = "ServiceCategory.findByName";
    public static final String FIND_BY_DESCRIPTION = "ServiceCategory.findByDescription";
    public static final String FIND_BY_KEYWORD = "ServiceCategory.findByKeyword";
    public static final String FIND_BY_SUPER_CATEGORY = "ServiceCategory.findBySuperCategory";
    public static final String FIND_BY_KEYWORD_IN_CATEGORY = "ServiceCategory.findByKeywordInCategory";
    public static final String DELETE_BY_NAME = "ServiceCategory.deleteByName";
    public static final String DELETE_BY_SUPER_CATEGORY = "ServiceCategory.deleteBySuperCategory";

    private Integer categoryId;
    private String categoryName;
    private String description;

    /* many-to-one relationship */
    private ServiceCategory superCategory;

    /* one-to-many relationship */
    private Set<ServiceCategory> subCategories = new HashSet<>();
    private Set<Service> services = new HashSet<>();

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

    @CategoryPhrase @NotNull
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

    @OneToMany(mappedBy = "superCategory", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getCategoryName())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ServiceCategory))
            return false;
        if (obj == this)
            return true;

        ServiceCategory rhs = (ServiceCategory) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getCategoryName(), rhs.getCategoryName())
                .isEquals();
    }
}