package pl.salonea.utils.zip_codes;

import pl.salonea.qualifiers.Country;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CountryZipCodeChecker implements ZipCodeChecker {

    private static final Logger logger = Logger.getLogger(CountryZipCodeChecker.class.getName());

    private Map<String, String> countryCodeMap;

    private String countryName;
    private Pattern formatPattern;

    public CountryZipCodeChecker() {}

    /* interface method - testing zip code against country specific regex pattern */

    @Override
    public boolean isFormatValid(String zipCode) {

        if(formatPattern != null) {

            Matcher matcher = formatPattern.matcher(zipCode);
            if(matcher.find())
                return true;

        }

        return false;
    }

    /* Getters and setters */
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
        loadFormatPattern(countryName);
    }

    public Pattern getFormatPattern() {
        return formatPattern;
    }

    public void setFormatPattern(Pattern formatPattern) {
        this.formatPattern = formatPattern;
    }

    /* helper methods - configure zip code pattern based on country name */

    private void loadFormatPattern(String countryName) {

        // loading country code -> regex pattern mapping
        Properties properties = loadZipCodePatternsProperties();

        // retrieving regex pattern from properties
        String countryCode = buildCountryCodeMap().get(countryName);
        String regex = properties.getProperty("zipcode.regex." + countryCode);

        setFormatPattern(Pattern.compile(regex));
    }

    private Map<String,String> buildCountryCodeMap() {

        if(countryCodeMap == null) {
            countryCodeMap = new HashMap<>();

            for (String iso : Locale.getISOCountries()) {
                Locale l = new Locale("", iso);
                countryCodeMap.put(l.getDisplayCountry(), iso);
            }
        }
        return countryCodeMap;

    }

    private Properties loadZipCodePatternsProperties() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("zip_code_patterns.properties");

        Properties properties = new Properties();
        try {
            properties.load(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.INFO, "Could not load zip_code_patterns.properties file.");
        }

        return properties;
    }

}
