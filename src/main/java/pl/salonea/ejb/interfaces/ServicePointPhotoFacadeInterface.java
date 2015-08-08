package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;

import java.util.List;

/**
 * Created by michzio on 05/08/2015.
 */
public interface ServicePointPhotoFacadeInterface extends AbstractFacadeInterface<ServicePointPhoto> {

    // concrete interface
    List<ServicePointPhoto> findByFileName(String fileName);
    List<ServicePointPhoto> findByFileName(String fileName, Integer start, Integer offset);
    List<ServicePointPhoto> findByDescription(String description);
    List<ServicePointPhoto> findByDescription(String description, Integer start, Integer offset);
    List<ServicePointPhoto> findByFileNameAndDescription(String fileName, String description);
    List<ServicePointPhoto> findByFileNameAndDescription(String fileName, String description, Integer start, Integer offset);
    List<ServicePointPhoto> findByKeyword(String keyword);
    List<ServicePointPhoto> findByKeyword(String keyword, Integer start, Integer offset);
    List<ServicePointPhoto> findByTagName(String tagName);
    List<ServicePointPhoto> findByTagName(String tagName, Integer start, Integer offset);
    List<ServicePointPhoto> findByAnyTagNames(List<String> tagNames);
    List<ServicePointPhoto> findByAnyTagNames(List<String> tagNames, Integer start, Integer offset);
    List<ServicePointPhoto> findByAllTags(List<Tag> tags);
    List<ServicePointPhoto> findByAllTags(List<Tag> tags, Integer start, Integer offset);
    List<ServicePointPhoto> findByAllTagNames(List<String> tagNames);
    List<ServicePointPhoto> findByAllTagNames(List<String> tagNames, Integer start, Integer offset);
    List<ServicePointPhoto> findByKeywordIncludingTags(String keyword);
    List<ServicePointPhoto> findByKeywordIncludingTags(String keyword, Integer start, Integer offset);
    List<ServicePointPhoto> findByServicePoint(ServicePoint servicePoint);
    List<ServicePointPhoto> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset);
    List<ServicePointPhoto> findByProvider(Provider provider);
    List<ServicePointPhoto> findByProvider(Provider provider, Integer start, Integer offset);
    List<ServicePointPhoto> findByCorporation(Corporation corporation);
    List<ServicePointPhoto> findByCorporation(Corporation corporation, Integer start, Integer offset);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations);

    @javax.ejb.Local
    interface Local extends ServicePointPhotoFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServicePointPhotoFacadeInterface { }
}
