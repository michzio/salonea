package pl.salonea.jaxrs.utils;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by michzio on 07/10/2015.
 */
@XmlRootElement(name = "request")
public class RequestWrapper {

    private String message;

    public RequestWrapper() { }

    public RequestWrapper(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
