package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Firm;

import java.util.List;

/**
 * Created by michzio on 17/07/2015.
 */
public interface FirmFacadeInterface extends AbstractFacadeInterface<Firm> {

    // concrete interface
    List<Firm> findByName(String name);
    Firm findByVATIN(String vatin);
    Firm findByCompanyNumber(String companyNumber);
    Boolean deleteWithVATIN(String vatin);
    Boolean deleteWithCompanyNumber(String companyNumber);
    List<Firm> findByName(String name, Integer start, Integer limit);
    List<Firm> findByAddress(String city, String state, String country, String street, String zipCode);
    List<Firm> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends FirmFacadeInterface {
    }

    @javax.ejb.Local
    interface Local extends FirmFacadeInterface {
    }
}
