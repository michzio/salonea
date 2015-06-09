package pl.salonea.utils.zip_codes;


import pl.salonea.qualifiers.Country;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZipCodeCheckerFactory {

    private static final Logger logger = Logger.getLogger(ZipCodeCheckerFactory.class.getName());

    @Inject
    CountryZipCodeChecker countryZipCodeChecker;

    /**
     * Producing suitable ZipCodeChecker implementation
     * based on InjectionPoint condition, i.e. based on
     * ZipCodeValidator provided country, in order to
     * make ZipCodeValidator validate zip code format
     * in country specific manner
     * @param injectionPoint
     * @return
     */
    @Produces @Country
    public ZipCodeChecker createZipCodeChecker(InjectionPoint injectionPoint) {

        // retrieving country name from injection point to return country specific checker instance
        String countryName = null;

        Set<Annotation> qualifiers = injectionPoint.getQualifiers();
        for(Annotation qualifier : qualifiers) {
            if(qualifier instanceof Country) {
                Country countryQualifier = (Country) qualifier;
                countryName = countryQualifier.value();
            }
        }

        //Country countryQualifier = injectionPoint.getAnnotated().getAnnotation(Country.class);
        //String countryName = countryQualifier.value();

        logger.log(Level.INFO, "Creating CountryZipCodeChecker for: " + countryName);

        countryZipCodeChecker.setCountryName(countryName);

        return countryZipCodeChecker;
    }

}