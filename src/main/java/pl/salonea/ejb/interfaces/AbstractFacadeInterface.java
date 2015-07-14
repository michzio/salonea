package pl.salonea.ejb.interfaces;

import java.util.List;

/**
 * Created by michzio on 14/07/2015.
 */
public interface AbstractFacadeInterface<T> {

        // abstract interface
        T create(T client);
        T update(T client);
        void remove(T client);
        T find(Object obj);
        List<T> findAll();
        List<T> findRange(int[] range);
        Long count();
}
