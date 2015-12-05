package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;

import java.util.List;

/**
 * Created by michzio on 08/08/2015.
 */
public interface VirtualTourFacadeInterface extends AbstractFacadeInterface<VirtualTour> {

    // concrete interface
    VirtualTour createForServicePoint(ServicePointId servicePointId, VirtualTour virtualTour);
    VirtualTour updateWithServicePoint(ServicePointId servicePointId, VirtualTour virtualTour);
    List<VirtualTour> findAllEagerly();
    List<VirtualTour> findAllEagerly(Integer start, Integer limit);
    VirtualTour findByIdEagerly(Long tourId);

    List<VirtualTour> findByFileName(String fileName);
    List<VirtualTour> findByFileName(String fileName, Integer start, Integer limit);
    List<VirtualTour> findByDescription(String description);
    List<VirtualTour> findByDescription(String description, Integer start, Integer limit);
    List<VirtualTour> findByFileNameAndDescription(String fileName, String description);
    List<VirtualTour> findByFileNameAndDescription(String fileName, String description, Integer start, Integer limit);
    List<VirtualTour> findByKeyword(String keyword);
    List<VirtualTour> findByKeyword(String keyword, Integer start, Integer limit);
    List<VirtualTour> findByTagName(String tagName);
    List<VirtualTour> findByTagName(String tagName, Integer start, Integer limit);
    List<VirtualTour> findByAnyTagNames(List<String> tagNames);
    List<VirtualTour> findByAnyTagNames(List<String> tagNames, Integer start, Integer limit);
    List<VirtualTour> findByAllTags(List<Tag> tags);
    List<VirtualTour> findByAllTags(List<Tag> tags, Integer start, Integer limit);
    List<VirtualTour> findByAllTagNames(List<String> tagNames);
    List<VirtualTour> findByAllTagNames(List<String> tagNames, Integer start, Integer limit);
    List<VirtualTour> findByKeywordIncludingTags(String keyword);
    List<VirtualTour> findByKeywordIncludingTags(String keyword, Integer start, Integer limit);

    List<VirtualTour> findByServicePoint(ServicePoint servicePoint);
    List<VirtualTour> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<VirtualTour> findByServicePointEagerly(ServicePoint servicePoint);
    List<VirtualTour> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);
    List<VirtualTour> findByProvider(Provider provider);
    List<VirtualTour> findByProvider(Provider provider, Integer start, Integer limit);
    List<VirtualTour> findByProviderEagerly(Provider provider);
    List<VirtualTour> findByProviderEagerly(Provider provider, Integer start, Integer limit);
    List<VirtualTour> findByCorporation(Corporation corporation);
    List<VirtualTour> findByCorporation(Corporation corporation, Integer start, Integer limit);
    List<VirtualTour> findByCorporationEagerly(Corporation corporation);
    List<VirtualTour> findByCorporationEagerly(Corporation corporation, Integer start, Integer limit);
    List<VirtualTour> findByTag(Tag tag);
    List<VirtualTour> findByTag(Tag tag, Integer start, Integer limit);
    List<VirtualTour> findByTagEagerly(Tag tag);
    List<VirtualTour> findByTagEagerly(Tag tag, Integer start, Integer limit);

    Long countByServicePoint(ServicePoint servicePoint);
    Long countByProvider(Provider provider);
    Long countByCorporation(Corporation corporation);
    Long countByTag(Tag tag);

    Integer deleteByServicePoint(ServicePoint servicePoint);
    Integer deleteById(Long tourId);

    List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<VirtualTour> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<VirtualTour> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);

    List<VirtualTour> findByMultipleCriteriaEagerly(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<VirtualTour> findByMultipleCriteriaEagerly(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<VirtualTour> findByMultipleCriteriaEagerly(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<VirtualTour> findByMultipleCriteriaEagerly(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);
    List<VirtualTour> findByMultipleCriteriaEagerly(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags);
    List<VirtualTour> findByMultipleCriteriaEagerly(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations, List<Tag> tags, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends VirtualTourFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends VirtualTourFacadeInterface { }
}
