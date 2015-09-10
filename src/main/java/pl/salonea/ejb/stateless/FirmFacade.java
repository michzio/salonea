package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.FirmFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.embeddables.Address_;
import pl.salonea.entities.Firm;
import pl.salonea.entities.Firm_;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 17/07/2015.
 */
@Stateless
@LocalBean
public class FirmFacade extends AbstractFacade<Firm> implements FirmFacadeInterface.Remote, FirmFacadeInterface.Local  {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public FirmFacade() {
        super(Firm.class);
    }

    @Override
    public List<Firm> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Firm> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_NAME, Firm.class);
        query.setParameter("firm_name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Firm findByVATIN(String vatin) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_VATIN, Firm.class);
        query.setParameter("vatin", vatin);
        try {
            return query.getSingleResult();
        } catch(NoResultException|NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public Firm findByCompanyNumber(String companyNumber) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_COMPANY_NUMBER, Firm.class);
        query.setParameter("company_number", companyNumber);
        try {
            return query.getSingleResult();
        } catch(NoResultException|NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public Boolean deleteWithVATIN(String vatin) {

        Query query = getEntityManager().createNamedQuery(Firm.DELETE_WITH_VATIN);
        query.setParameter("vatin", vatin);
        if(query.executeUpdate() == 1)
            return true;

        return false;
    }

    @Override
    public Boolean deleteWithCompanyNumber(String companyNumber) {

        Query query = getEntityManager().createNamedQuery(Firm.DELETE_WITH_COMPANY_NUMBER);
        query.setParameter("company_number", companyNumber);
        if(query.executeUpdate() == 1)
            return true;

        return false;
    }

    @Override
    public List<Firm> findByAddress(String city, String state, String country, String street, String zipCode) {
        return findByAddress(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<Firm> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_ADDRESS, Firm.class);
        if(city == null) city = "";
        query.setParameter("city", "%" + city + "%");
        if(state == null) state = "";
        query.setParameter("state", "%" + state + "%");
        if(country == null) country = "";
        query.setParameter("country", "%" + country + "%");
        if(street == null) street = "";
        query.setParameter("street", "%" + street + "%");
        if(zipCode == null) zipCode = "";
        query.setParameter("zip_code", "%" + zipCode + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Firm> findByMultipleCriteria(String name, String vatin, String companyNumber, String statisticNumber, String phoneNumber, String skypeName, Address address) {
        return findByMultipleCriteria(name, vatin, companyNumber, statisticNumber, phoneNumber, skypeName, address, null, null);
    }

    @Override
    public List<Firm> findByMultipleCriteria(String name, String vatin, String companyNumber, String statisticNumber, String phoneNumber, String skypeName, Address address, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Firm> criteriaQuery = criteriaBuilder.createQuery(Firm.class);
        // FROM
        Root<Firm> firm = criteriaQuery.from(Firm.class);
        // SELECT
        criteriaQuery.select(firm);

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(name != null) {
            predicates.add( criteriaBuilder.like(firm.get(Firm_.name), "%" + name + "%") );
        }

        if(vatin != null) {
            predicates.add( criteriaBuilder.like(firm.get(Firm_.vatin), "%" + vatin + "%") );
        }

        if(companyNumber != null) {
            predicates.add( criteriaBuilder.like(firm.get(Firm_.companyNumber), "%" + companyNumber + "%") );
        }

        if(statisticNumber != null) {
            predicates.add( criteriaBuilder.like(firm.get(Firm_.statisticNumber), "%" + statisticNumber + "%") );
        }

        if(phoneNumber != null) {
            predicates.add( criteriaBuilder.like(firm.get(Firm_.phoneNumber), "%" + phoneNumber + "%") );
        }

        if(skypeName != null) {
            predicates.add( criteriaBuilder.like(firm.get(Firm_.skypeName), "%" + skypeName + "%") );
        }

        if(address != null) {

            Path<Address> addressPath = firm.get(Firm_.address);

            if(address.getCity() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.city), "%" + address.getCity() + "%") );

            if(address.getState() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.state), "%" + address.getState() + "%") );

            if(address.getCountry() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.country), "%" + address.getCountry() + "%") );

            if(address.getStreet() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.street), "%" + address.getStreet() + "%") );

            if(address.getZipCode() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.zipCode), "%" + address.getZipCode() + "%") );

            if(address.getFlatNumber() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.flatNumber), "%" + address.getFlatNumber() + "%") );

            if(address.getHouseNumber() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.houseNumber), "%" + address.getHouseNumber() + "%") );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Firm> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }
}