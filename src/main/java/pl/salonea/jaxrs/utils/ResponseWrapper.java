package pl.salonea.jaxrs.utils;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by michzio on 03/09/2015.
 */
@XmlRootElement(name = "response")
public class ResponseWrapper {

    private String message;
    private Integer statusCode;
    private String description;
    //List<Link> links;


    public ResponseWrapper() {
    }

    public ResponseWrapper(String message, Integer statusCode, String description) {
        this.message = message;
        this.statusCode = statusCode;
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
