package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 08/09/2015.
 */
public class NamesBeanParam extends PaginationBeanParam {

    private @QueryParam("firstName") String firstName;
    private @QueryParam("lastName") String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
