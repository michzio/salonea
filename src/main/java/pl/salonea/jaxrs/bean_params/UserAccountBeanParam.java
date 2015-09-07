package pl.salonea.jaxrs.bean_params;

import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 06/09/2015.
 */
public class UserAccountBeanParam extends PaginationBeanParam {

    private @QueryParam("login") String login;
    private @QueryParam("email") String email;
    private @QueryParam("activated") Boolean activated;
    private @QueryParam("createdAfter") RESTDateTime createdAfterDate;
    private @QueryParam("createdBefore") RESTDateTime createdBeforeDate;
    private @QueryParam("lastLoggedAfter") RESTDateTime lastLoggedAfterDate;
    private @QueryParam("lastLoggedBefore") RESTDateTime lastLoggedBeforeDate;
    private @QueryParam("lastFailedLoginAfter") RESTDateTime lastFailedLoginAfterDate;
    private @QueryParam("lastFailedLoginBefore") RESTDateTime lastFailedLoginBeforeDate;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public RESTDateTime getCreatedAfterDate() {
        return createdAfterDate;
    }

    public void setCreatedAfterDate(RESTDateTime createdAfterDate) {
        this.createdAfterDate = createdAfterDate;
    }

    public RESTDateTime getCreatedBeforeDate() {
        return createdBeforeDate;
    }

    public void setCreatedBeforeDate(RESTDateTime createdBeforeDate) {
        this.createdBeforeDate = createdBeforeDate;
    }

    public RESTDateTime getLastLoggedAfterDate() {
        return lastLoggedAfterDate;
    }

    public void setLastLoggedAfterDate(RESTDateTime lastLoggedAfterDate) {
        this.lastLoggedAfterDate = lastLoggedAfterDate;
    }

    public RESTDateTime getLastLoggedBeforeDate() {
        return lastLoggedBeforeDate;
    }

    public void setLastLoggedBeforeDate(RESTDateTime lastLoggedBeforeDate) {
        this.lastLoggedBeforeDate = lastLoggedBeforeDate;
    }

    public RESTDateTime getLastFailedLoginAfterDate() {
        return lastFailedLoginAfterDate;
    }

    public void setLastFailedLoginAfterDate(RESTDateTime lastFailedLoginAfterDate) {
        this.lastFailedLoginAfterDate = lastFailedLoginAfterDate;
    }

    public RESTDateTime getLastFailedLoginBeforeDate() {
        return lastFailedLoginBeforeDate;
    }

    public void setLastFailedLoginBeforeDate(RESTDateTime lastFailedLoginBeforeDate) {
        this.lastFailedLoginBeforeDate = lastFailedLoginBeforeDate;
    }
}