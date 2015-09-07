package pl.salonea.mapped_superclasses;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

/**
 * Created by michzio on 17/07/2015.
 */
@MappedSuperclass
public abstract class UUIDEntity {

    // UUID = universally unique identifier 128-bit value used by equals()/hashCode()
    // parsed to VARCHAR(36) value which takes 36*8 = 288 bits
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "uuid", columnDefinition = "CHAR(36) default NULL")
    public String getUuid() {
        return uuid;
    }

    private void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(getUuid())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UUIDEntity))
            return false;
        if (obj == this)
            return true;

        UUIDEntity rhs = (UUIDEntity) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(getUuid(), rhs.getUuid()).
                isEquals();
    }
}
