package pl.salonea.ejb.interfaces;

import pl.salonea.entities.ServiceCategory;

import java.util.List;

/**
 * Created by michzio on 31/07/2015.
 */
public interface ServiceCategoryFacadeInterface extends AbstractFacadeInterface<ServiceCategory> {

    // concrete interface
    List<ServiceCategory> findByName(String name);
    List<ServiceCategory> findByName(String name, Integer start, Integer offset);
    List<ServiceCategory> findByDescription(String description);
    List<ServiceCategory> findByDescription(String description, Integer start, Integer offset);
    List<ServiceCategory> findByKeyword(String keyword);
    List<ServiceCategory> findByKeyword(String keyword, Integer start, Integer offset);
    List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory);
    List<ServiceCategory> findBySuperCategory(ServiceCategory superCategory, Integer start, Integer offset);
    List<ServiceCategory> findByKeywordInCategory(ServiceCategory superCategory, String keyword);
    List<ServiceCategory> findByKeywordInCategory(ServiceCategory superCategory, String keyword, Integer start, Integer offset);
    Integer deleteByName(String name);
    Integer deleteBySuperCategory(ServiceCategory superCategory);

    @javax.ejb.Local
    interface Local extends ServiceCategoryFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServiceCategoryFacadeInterface { }
}
