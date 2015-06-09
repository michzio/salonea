package pl.salonea.qualifiers;

import javax.enterprise.util.AnnotationLiteral;

/**
 * Created by michzio on 06/06/15.
 */
public class CountryQualifierLiteral extends AnnotationLiteral<Country> implements Country {

    private final String countryName;

    public CountryQualifierLiteral(final String countryName) {
            this.countryName = countryName;
    }

    @Override
    public String value() {
        return countryName;
    }
}
