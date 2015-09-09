package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.QueryParam;

/**
 * Created by michzio on 08/09/2015.
 */
public class AgeBetweenBeanParam extends PaginationBeanParam {

    private @QueryParam("youngestAge") Integer youngestAge;
    private @QueryParam("oldestAge") Integer oldestAge;

    public Integer getYoungestAge() {
        return youngestAge;
    }

    public void setYoungestAge(Integer youngestAge) {
        this.youngestAge = youngestAge;
    }

    public Integer getOldestAge() {
        return oldestAge;
    }

    public void setOldestAge(Integer oldestAge) {
        this.oldestAge = oldestAge;
    }
}
