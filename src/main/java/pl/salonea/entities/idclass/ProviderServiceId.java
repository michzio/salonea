package pl.salonea.entities.idclass;

import java.io.Serializable;

public class ProviderServiceId implements Serializable {

    private Long provider;
    private Integer service;

    /* constructors */

    public ProviderServiceId() { }

    public ProviderServiceId(Long providerId, Integer serviceId) {
        this.provider = providerId;
        this.service = serviceId;
    }

    /* getters and setters */

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long providerId) {
        this.provider = providerId;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer serviceId) {
        this.service = serviceId;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProviderServiceId that = (ProviderServiceId) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        return !(service != null ? !service.equals(that.service) : that.service != null);

    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        return result;
    }

    /**
     * Allowed serialized provider service id formats:
     * [1,2]  [1+2]  [1-2]
     * (1,2)  (1+2)  (1-2)
     * 1,2    1+2    1-2
     */
    public static ProviderServiceId valueOf(String providerServiceIdString) {

        if( !providerServiceIdString.matches("[(\\[]?\\d+[,+ -]{1}\\d+[\\])]?") )
            throw new IllegalArgumentException("Serialized provider service id doesn't match specified regex pattern.");

        // trim leading and trailing brackets
        providerServiceIdString = providerServiceIdString.replaceAll("[(\\[)\\]]", "");
        // split identifiers by several possible delimiters
        String[] tokens = providerServiceIdString.split("[,+ -]", 2);
        if( tokens.length != 2 )
            throw new IllegalArgumentException("Serialized provider service id should consist of two delimited tokens ex. 1+2");

        Long providerId = Long.valueOf(tokens[0]);
        Integer serviceId = Integer.valueOf(tokens[1]);

        // construct provider service id
        return new ProviderServiceId(providerId, serviceId);
    }

}

