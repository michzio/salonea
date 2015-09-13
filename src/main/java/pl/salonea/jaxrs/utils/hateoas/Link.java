package pl.salonea.jaxrs.utils.hateoas;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * Created by michzio on 03/09/2015.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Link {

    private String uri;
    private String rel;

    // TODO better to implement custom XmlAdapter to marshal object to xml and turn off unmarshalling
    //      than to expose this public default no-args constructor
    public Link() { }

    private Link(String uri, String rel) {
        this.uri = uri;
        this.rel = rel;
    }

    /** builder factory methods **/

    public static Builder fromUri(URI uri) {
        Builder builder = new Builder();
        builder.uri(uri.toString());
        return builder;
    }

    public static Builder fromUri(String uri) {
        Builder builder = new Builder();
        builder.uri(uri);
        return builder;
    }

    public static Builder fromRel(String rel) {
        Builder builder = new Builder();
        builder.rel(rel);
        return builder;
    }

    /** getters and setters **/

    @XmlElement(name = "uri")
    public String getUri() {
        return uri;
    }

    private void setUri(String uri) {
        this.uri = uri;
    }

    @XmlElement(name = "rel")
    public String getRel() {
        return rel;
    }

    private void setRel(String rel) {
        this.rel = rel;
    }

    /**
     * Builder pattern class that enables to build
     * Link object in convenient way.
     */

    public static class Builder {

        private String uri;
        private String rel;

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder rel(String rel) {
            this.rel = rel;
            return this;
        }

        public Link build(){
            return new Link(uri, rel);
        }
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getUri())
                .append(getRel())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Link))
            return false;
        if (obj == this)
            return true;

        Link rhs = (Link) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj))
                .append(getUri(), rhs.getUri())
                .append(getRel(), rhs.getRel())
                .isEquals();
    }

}
