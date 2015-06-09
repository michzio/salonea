package pl.salonea.entities.idclass;

import java.io.Serializable;

public class ServicePointId implements Serializable {

    private Long provider;
    private Integer servicePointNumber;

    /* constructors */

    public ServicePointId() {}

    public ServicePointId(Long providerId, Integer servicePointNumber) {
        this.provider = providerId;
        this.servicePointNumber = servicePointNumber;
    }

    /* getters and setters */

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long providerId) {
        this.provider = providerId;
    }

    public Integer getServicePointNumber() {
        return servicePointNumber;
    }

    public void setServicePointNumber(Integer servicePointNumber) {
        this.servicePointNumber = servicePointNumber;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServicePointId that = (ServicePointId) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        return !(servicePointNumber != null ? !servicePointNumber.equals(that.servicePointNumber) : that.servicePointNumber != null);

    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (servicePointNumber != null ? servicePointNumber.hashCode() : 0);
        return result;
    }
}
