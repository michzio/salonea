package pl.salonea.jaxrs.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by michzio on 03/09/2015.
 */
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ErrorResponseWrapper {

    private String errorMessage;
    private Integer errorCode;
    private String errorDescription;

    public ErrorResponseWrapper() {
    }

    public ErrorResponseWrapper(String errorMessage, Integer errorCode, String errorDescription) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    @XmlElement(name="message")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @XmlElement(name="code")
    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @XmlElement(name="description")
    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
