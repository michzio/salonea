package pl.salonea.interceptors;


import pl.salonea.entities.Provider;

import javax.annotation.PostConstruct;
import javax.interceptor.AroundConstruct;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Level;
import java.util.logging.Logger;

@Interceptor
@ServicePointInterceptable
public class ServicePointInterceptor {

    private static final Logger logger = java.util.logging.Logger.getLogger(ServicePointInterceptor.class.getName());

    @AroundInvoke
    private Object init(InvocationContext ic) throws Exception {

        logger.log(Level.INFO, "@AroundInvoke interceptor");

        return ic.proceed();
    }
}
