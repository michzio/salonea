package pl.salonea.jaxrs.bean_params;

import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by michzio on 05/11/2015.
 */
public class EmployeeBeanParam extends DateBetweenBeanParam { // incl. PaginationBeanParam

    private @QueryParam("description") String description;
    private @QueryParam("jobPosition") List<String> jobPositions;
    private @QueryParam("skillId") List<Integer> skillIds;
    private @QueryParam("educationId") List<Long> educationIds;
    private @QueryParam("serviceId") List<Integer> serviceIds;
    private @QueryParam("providerServiceId") List<ProviderServiceId> providerServiceIds; // {providerId}+{serviceId} composite PK
    private @QueryParam("servicePointId") List<ServicePointId> servicePointIds; // {providerId}+{servicePointNumber} composite PK
    private @QueryParam("workStationId") List<WorkStationId> workStationIds; // {providerId}+{servicePointNumber}+{workStationNumber} composite PK
    private @QueryParam("strictTerm") Boolean strictTerm;
    private @QueryParam("rated") Boolean rated;
    private @QueryParam("minAvgRating") Double minAvgRating;
    private @QueryParam("maxAvgRating") Double maxAvgRating;
    private @QueryParam("ratingClientId") List<Long> ratingClientIds;

}