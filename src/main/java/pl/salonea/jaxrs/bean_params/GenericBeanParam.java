package pl.salonea.jaxrs.bean_params;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Created by michzio on 05/09/2015.
 */
public class GenericBeanParam {

    private @HeaderParam("authToken") String authToken;
    private @Context UriInfo uriInfo;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public void setUriInfo(UriInfo uriInfo) { this.uriInfo = uriInfo; }

}
