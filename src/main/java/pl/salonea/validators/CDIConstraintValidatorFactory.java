package pl.salonea.validators;


import pl.salonea.qualifiers.CDIValidation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import java.util.Set;

public class CDIConstraintValidatorFactory implements ConstraintValidatorFactory {

    @Override
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> type) {
        try{

            T t = getBeanInstance(type);
            if(t==null){
                t = type.newInstance();
            }
            return t;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private BeanManager getBeanManager() throws NamingException {
        String name = "java:comp/" + BeanManager.class.getSimpleName();
        InitialContext ic = new InitialContext();
        BeanManager beanManager = (BeanManager) ic.lookup(name);
        return beanManager;
    }

    public <T> T getBeanInstance(final Class<T> type) throws Exception{

        CDIValidation v = type.getAnnotation(CDIValidation.class);

        if(v!=null){
            BeanManager beanManager =  getBeanManager();
            final Set<Bean<?>> beans = beanManager.getBeans(type, v);
            beanManager.resolve(beans);
            if(!beans.isEmpty()){
                final Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
                final CreationalContext<T> creationalContext = beanManager.createCreationalContext(bean);
                return (T) beanManager.getReference(bean, type,creationalContext);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public void releaseInstance(ConstraintValidator<?, ?> instance) {

    }



}
