package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;

import java.util.List;

/**
 * Created by michzio on 08/08/2015.
 */
public interface VirtualTourFacadeInterface extends AbstractFacadeInterface<VirtualTour> {

    // concrete interface
    List<VirtualTour> findByFileName(String fileName);
    List<VirtualTour> findByFileName(String fileName, Integer start, Integer offset);
    List<VirtualTour> findByDescription(String description);
    List<VirtualTour> findByDescription(String description, Integer start, Integer offset);
    List<VirtualTour> findByFileNameAndDescription(String fileName, String description);
    List<VirtualTour> findByFileNameAndDescription(String fileName, String description, Integer start, Integer offset);
    List<VirtualTour> findByKeyword(String keyword);
    List<VirtualTour> findByKeyword(String keyword, Integer start, Integer offset);
    List<VirtualTour> findByTagName(String tagName);
    List<VirtualTour> findByTagName(String tagName, Integer start, Integer offset);
    List<VirtualTour> findByAnyTagNames(List<String> tagNames);
    List<VirtualTour> findByAnyTagNames(List<String> tagNames, Integer start, Integer offset);
    List<VirtualTour> findByAllTags(List<Tag> tags);
    List<VirtualTour> findByAllTags(List<Tag> tags, Integer start, Integer offset);
    List<VirtualTour> findByAllTagNames(List<String> tagNames);
    List<VirtualTour> findByAllTagNames(List<String> tagNames, Integer start, Integer offset);
    List<VirtualTour> findByKeywordIncludingTags(String keyword);
    List<VirtualTour> findByKeywordIncludingTags(String keyword, Integer start, Integer offset);
    List<VirtualTour> findByServicePoint(ServicePoint servicePoint);
    List<VirtualTour> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset);
    List<VirtualTour> findByProvider(Provider provider);
    List<VirtualTour> findByProvider(Provider provider, Integer start, Integer offset);
    List<VirtualTour> findByCorporation(Corporation corporation);
    List<VirtualTour> findByCorporation(Corporation corporation, Integer start, Integer offset);
    List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations);
    List<VirtualTour> findByMultipleCriteria(List<String> keywords, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations);
    List<VirtualTour> findByMultipleCriteria(List<String> fileNames, List<String> descriptions, List<String> tagNames, List<ServicePoint> servicePoints, List<Provider> providers, List<Corporation> corporations);

    @javax.ejb.Local
    interface Local extends VirtualTourFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends VirtualTourFacadeInterface { }
}
