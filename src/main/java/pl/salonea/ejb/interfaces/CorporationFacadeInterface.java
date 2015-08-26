package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Corporation;

import javax.ejb.Remote;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 22/07/2015.
 */
public interface CorporationFacadeInterface extends AbstractFacadeInterface<Corporation> {

    // concrete interface
    List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode);
    List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<Corporation> findByName(String name);
    List<Corporation> findByName(String name, Integer start, Integer limit);
    List<Corporation> findOpenAfter(Date date);
    List<Corporation> findOpenAfter(Date date, Integer start, Integer limit);
    List<Corporation> findOpenBefore(Date date);
    List<Corporation> findOpenBefore(Date date, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends CorporationFacadeInterface { }

    @javax.ejb.Local
    interface Local extends CorporationFacadeInterface { }
}
