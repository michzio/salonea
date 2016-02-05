package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.ServiceCategoryFacade;
import pl.salonea.entities.ServiceCategory;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 04/02/2016.
 */
public class ServiceCategoryBeanParam extends PaginationBeanParam {

    private @QueryParam("name") List<String> names;
    private @QueryParam("description") List<String> descriptions;
    private @QueryParam("keyword") List<String> keywords;

    private @QueryParam("superCategoryId") List<Integer> superCategoryIds;

    @Inject
    private ServiceCategoryFacade serviceCategoryFacade;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<Integer> getSuperCategoryIds() {
        return superCategoryIds;
    }

    public void setSuperCategoryIds(List<Integer> superCategoryIds) {
        this.superCategoryIds = superCategoryIds;
    }

    public List<ServiceCategory> getSuperCategories() throws NotFoundException {
        if(getSuperCategoryIds() != null && getSuperCategoryIds().size() > 0) {
            final List<ServiceCategory> superCategories = serviceCategoryFacade.find( new ArrayList<>(getSuperCategoryIds()) );
            if(superCategories.size() != getSuperCategoryIds().size()) throw new NotFoundException("Could not find super categories for all provided ids.");
            return superCategories;
        }
        return null;
    }
}
