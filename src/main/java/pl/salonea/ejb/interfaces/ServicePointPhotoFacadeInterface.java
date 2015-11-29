package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;

import java.util.List;

/**
 * Created by michzio on 05/08/2015.
 */
public interface ServicePointPhotoFacadeInterface extends AbstractFacadeInterface<ServicePointPhoto> {

    // concrete interface
    ServicePointPhoto createForServicePoint(ServicePointId servicePointId, ServicePointPhoto photo);
    ServicePointPhoto updateWithServicePoint(ServicePointId servicePointId, ServicePointPhoto photo);
    List<ServicePointPhoto> findAllEagerly();
    List<ServicePointPhoto> findAllEagerly(Integer start, Integer limit);
    ServicePointPhoto findByIdEagerly(Long photoId);

    List<ServicePointPhoto> findByFileName(String fileName);
    List<ServicePointPhoto> findByFileName(String fileName, Integer start, Integer limit);
    List<ServicePointPhoto> findByDescription(String description);
    List<ServicePointPhoto> findByDescription(String description, Integer start, Integer limit);
    List<ServicePointPhoto> findByFileNameAndDescription(String fileName, String description);
    List<ServicePointPhoto> findByFileNameAndDescription(String fileName, String description, Integer start, Integer limit);
    List<ServicePointPhoto> findByKeyword(String keyword);
    List<ServicePointPhoto> findByKeyword(String keyword, Integer start, Integer limit);
    List<ServicePointPhoto> findByTagName(String tagName);
    List<ServicePointPhoto> findByTagName(String tagName, Integer start, Integer limit);
    List<ServicePointPhoto> findByAnyTagNames(List<String> tagNames);
    List<ServicePointPhoto> findByAnyTagNames(List<String> tagNames, Integer start, Integer limit);
    List<ServicePointPhoto> findByAllTags(List<Tag> tags);
    List<ServicePointPhoto> findByAllTags(List<Tag> tags, Integer start, Integer limit);
    List<ServicePointPhoto> findByAllTagNames(List<String> tagNames);
    List<ServicePointPhoto> findByAllTagNames(List<String> tagNames, Integer start, Integer limit);
    List<ServicePointPhoto> findByKeywordIncludingTags(String keyword);
    List<ServicePointPhoto> findByKeywordIncludingTags(String keyword, Integer start, Integer limit);

    List<ServicePointPhoto> findByServicePoint(ServicePoint servicePoint);
    List<ServicePointPhoto> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<ServicePointPhoto> findByServicePointEagerly(ServicePoint servicePoint);
    List<ServicePointPhoto> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);
    List<ServicePointPhoto> findByProvider(Provider provider);
    List<ServicePointPhoto> findByProvider(Provider provider, Integer start, Integer limit);
    List<ServicePointPhoto> findByProviderEagerly(Provider provider);
    List<ServicePointPhoto> findByProviderEagerly(Provider provider, Integer start, Integer limit);
    List<ServicePointPhoto> findByCorporation(Corporation corporation);
    List<ServicePointPhoto> findByCorporation(Corporation corporation, Integer start, Integer limit);
    List<ServicePointPhoto> findByCorporationEagerly(Corporation corporation);
    List<ServicePointPhoto> findByCorporationEagerly(Corporation corporation, Integer start, Integer limit);
    List<ServicePointPhoto> findByTag(Tag tag);
    List<ServicePointPhoto> findByTag(Tag tag, Integer start, Integer limit);
    List<ServicePointPhoto> findByTagEagerly(Tag tag);
    List<ServicePointPhoto> findByTagEagerly(Tag tag, Integer start, Integer limit);

    Long countByServicePoint(ServicePoint servicePoint);
    Long countByProvider(Provider provider);
    Long countByCorporation(Corporation corporation);
    Long countByTag(Tag tag);

    Integer deleteByServicePoint(ServicePoint servicePoint);
    Integer deleteById(Long photoId);

    List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<ServicePointPhoto> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);

    List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<ServicePointPhoto> findByMultipleCriteriaEagerly(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends ServicePointPhotoFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServicePointPhotoFacadeInterface { }
}
