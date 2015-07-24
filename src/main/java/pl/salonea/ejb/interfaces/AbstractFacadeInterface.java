package pl.salonea.ejb.interfaces;

import java.util.List;

/**
 * Created by michzio on 14/07/2015.
 */
public interface AbstractFacadeInterface<T> {

        // abstract interface
        T create(T entity);
        T update(T entity);
        void refresh(T entity); // works only with attached entity, if entity is detached need to find it again
        void remove(T entity);
        T find(Object obj);
        List<T> findAll();
        List<T> findRange(int[] range);
        Long count();
}
