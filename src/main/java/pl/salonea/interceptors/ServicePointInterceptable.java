package pl.salonea.interceptors;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@InterceptorBinding
@Target({METHOD, TYPE, CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServicePointInterceptable {
}
