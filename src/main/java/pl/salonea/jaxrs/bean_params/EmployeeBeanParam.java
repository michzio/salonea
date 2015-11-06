package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.utils.Period;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
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

    @Inject
    private SkillFacade skillFacade;

    @Inject
    private EducationFacade educationFacade;

    @Inject
    private ServiceFacade serviceFacade;

    @Inject
    private ProviderServiceFacade providerServiceFacade;

    @Inject
    private ServicePointFacade servicePointFacade;

    @Inject
    private WorkStationFacade workStationFacade;

    @Inject
    private ClientFacade clientFacade;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getJobPositions() {
        return jobPositions;
    }

    public void setJobPositions(List<String> jobPositions) {
        this.jobPositions = jobPositions;
    }

    public List<Integer> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Integer> skillIds) {
        this.skillIds = skillIds;
    }

    public List<Skill> getSkills() throws NotFoundException {
        if(getSkillIds() != null && getSkillIds().size() > 0) {
            final List<Skill> skills = skillFacade.find(new ArrayList<>(getSkillIds()));
            if(skills.size() != getSkillIds().size()) throw new NotFoundException("Could not find skills for all provided ids.");
            return skills;
        }
        return null;
    }

    public List<Long> getEducationIds() {
        return educationIds;
    }

    public void setEducationIds(List<Long> educationIds) {
        this.educationIds = educationIds;
    }

    public List<Education> getEducations() throws NotFoundException {
        if(getEducationIds() != null && getEducationIds().size() > 0) {
            final List<Education> educations = educationFacade.find(new ArrayList<>(getEducationIds()));
            if(educations.size() != getEducationIds().size()) throw new NotFoundException("Could not find educations for all provided ids.");
            return educations;
        }
        return null;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Service> getServices() throws NotFoundException {
        if(getServiceIds() != null && getServiceIds().size() > 0) {
            final List<Service> services = serviceFacade.find(new ArrayList<>(getServiceIds()));
            if(services.size() != getServiceIds().size()) throw new NotFoundException("Could not find services for all provided ids.");
            return services;
        }
        return null;
    }

    public List<ProviderServiceId> getProviderServiceIds() {
        return providerServiceIds;
    }

    public void setProviderServiceIds(List<ProviderServiceId> providerServiceIds) {
        this.providerServiceIds = providerServiceIds;
    }

    public List<ProviderService> getProviderServices() throws NotFoundException {
        if(getProviderServiceIds() != null && getProviderServiceIds().size() > 0) {
            final List<ProviderService> providerServices = providerServiceFacade.find( new ArrayList<>(getProviderServiceIds()) );
            if(providerServices.size() != getProviderServiceIds().size()) throw new NotFoundException("Could not find provider services for all provided ids.");
            return providerServices;
        }
        return null;
    }

    public List<ServicePointId> getServicePointIds() {
        return servicePointIds;
    }

    public void setServicePointIds(List<ServicePointId> servicePointIds) {
        this.servicePointIds = servicePointIds;
    }

    public List<ServicePoint> getServicePoints() throws NotFoundException {
        if(getServicePointIds() != null && getServicePointIds().size() > 0) {
            final List<ServicePoint> servicePoints = servicePointFacade.find( new ArrayList<>(getServicePointIds()) );
            if(servicePoints.size() != getServicePointIds().size()) throw new NotFoundException("Could not find service points for all provided ids.");
            return servicePoints;
        }
        return null;
    }

    public List<WorkStationId> getWorkStationIds() {
        return workStationIds;
    }

    public void setWorkStationIds(List<WorkStationId> workStationIds) {
        this.workStationIds = workStationIds;
    }

    public List<WorkStation> getWorkStations() throws NotFoundException {
        if(getWorkStationIds() != null && getWorkStationIds().size() > 0) {
            final List<WorkStation> workStations = workStationFacade.find( new ArrayList<>(getWorkStationIds()) );
            if(workStations.size() != getWorkStationIds().size()) throw new NotFoundException("Could not find work stations for all provided ids.");
            return workStations;
        }
        return null;
    }

    public Period getPeriod() {
        if(getStartDate() == null && getEndDate() == null)
            return null;

        return new Period(getStartDate(), getEndDate());
    }

    public Boolean getStrictTerm() {
        return strictTerm;
    }

    public void setStrictTerm(Boolean strictTerm) {
        this.strictTerm = strictTerm;
    }

    public Boolean getRated() {
        return rated;
    }

    public void setRated(Boolean rated) {
        this.rated = rated;
    }

    public Double getMinAvgRating() {
        return minAvgRating;
    }

    public void setMinAvgRating(Double minAvgRating) {
        this.minAvgRating = minAvgRating;
    }

    public Double getMaxAvgRating() {
        return maxAvgRating;
    }

    public void setMaxAvgRating(Double maxAvgRating) {
        this.maxAvgRating = maxAvgRating;
    }

    public List<Long> getRatingClientIds() {
        return ratingClientIds;
    }

    public void setRatingClientIds(List<Long> ratingClientIds) {
        this.ratingClientIds = ratingClientIds;
    }

    public List<Client> getRatingClients() throws NotFoundException {
        if(getRatingClientIds() != null && getRatingClientIds().size() > 0) {
            final List<Client> ratingClients = clientFacade.find(new ArrayList<>(getRatingClientIds()));
            if(ratingClients.size() != getRatingClientIds().size()) throw new NotFoundException("Could not find rating clients for all provided ids.");
            return ratingClients;
        }
        return null;
    }
}