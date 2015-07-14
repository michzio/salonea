package pl.salonea.ejb.interfaces;

import pl.salonea.ejb.stateless.NaturalPersonFacade;
import pl.salonea.entities.NaturalPerson;


/**
 * Created by michzio on 14/07/2015.
 */
public interface NaturalPersonFacadeInterface extends AbstractFacadeInterface<NaturalPerson> {

    // concrete interface


    @javax.ejb.Remote
    interface Remote extends NaturalPersonFacadeInterface { }

    @javax.ejb.Local
    interface Local extends NaturalPersonFacadeInterface { }


}
