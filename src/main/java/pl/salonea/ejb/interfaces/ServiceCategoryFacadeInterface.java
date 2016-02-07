package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Service;
import pl.salonea.entities.ServiceCategory;

import java.util.List;

/**
 * Created by michzio on 31/07/2015.
 */
public interface ServiceCategoryFacadeInterface extends AbstractFacadeInterface<ServiceCategory> {

    // concrete interface
    ServiceCategory update(ServiceCategory serviceCategory, Boolean retainTransientFields);
    List<ServiceCategory> findAllEagerly();
    List<ServiceCategory> findAllEagerly(Integer start, Integer limit);
    ServiceCategory findByIdEagerly(Integer categoryId);

    List<ServiceCategory> findByName(String name);
    List<ServiceCategory> findByName(String name, Integer start, Integer limit);
    List<ServiceCategory> findByDescription(String description);
    List<ServiceCategory> findByDescription(String description, Integer start, Integer limit);
    List<ServiceCategory> findByKeyword(String keyword);
    List<ServiceCategory> findByKeyword(String keyword, Integer start, Integer limit);

    List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory);
    List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory, Integer start, Integer limit);
    List<ServiceCategory> findBySuperCategoryEagerly(ServiceCategory superCategory);
    List<ServiceCategory> findBySuperCategoryEagerly(ServiceCategory superCategory, Integer start, Integer limit);
    List<ServiceCategory> findBySuperCategoryAndName(ServiceCategory superCategory, String name);
    List<ServiceCategory> findBySuperCategoryAndName(ServiceCategory superCategory, String name, Integer start, Integer limit);
    List<ServiceCategory> findBySuperCategoryAndDescription(ServiceCategory superCategory, String description);
    List<ServiceCategory> findBySuperCategoryAndDescription(ServiceCategory superCategory, String description, Integer start, Integer limit);
    List<ServiceCategory> findBySuperCategoryAndKeyword(ServiceCategory superCategory, String keyword);
    List<ServiceCategory> findBySuperCategoryAndKeyword(ServiceCategory superCategory, String keyword, Integer start, Integer limit);

    Long countBySuperCategory(ServiceCategory superCategory);

    Integer deleteByName(String name);
    Integer deleteBySuperCategory(ServiceCategory superCategory);

    List<ServiceCategory> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories);
    List<ServiceCategory> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories, Integer start, Integer limit);
    List<ServiceCategory> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> superCategories);
    List<ServiceCategory> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> superCategories, Integer start, Integer limit);

    List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories);
    List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> superCategories, Integer start, Integer limit);
    List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> superCategories);
    List<ServiceCategory> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> superCategories, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends ServiceCategoryFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServiceCategoryFacadeInterface { }
}
