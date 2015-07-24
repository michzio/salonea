package pl.salonea.ejb.interfaces;

import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;

import java.util.List;

/**
 * Created by michzio on 24/07/2015.
 */
public interface IndustryFacadeInterface extends AbstractFacadeInterface<Industry> {

    // concrete interface
    Industry findForName(String name);
    List<Industry> findByName(String name);
    List<Industry> findByName(String name, Integer start, Integer offset);
    List<Industry> findByProvider(Provider provider);
    List<Industry> findByProvider(Provider provider, Integer start, Integer offset);

    @javax.ejb.Remote
    interface Remote extends IndustryFacadeInterface { }

    @javax.ejb.Local
    interface Local extends IndustryFacadeInterface { }
}
