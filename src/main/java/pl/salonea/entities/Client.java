package pl.salonea.entities;

import pl.salonea.constraints.NaturalPersonOrFirm;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="client")
@Access(AccessType.PROPERTY)
@NaturalPersonOrFirm
public class Client implements Serializable{

    private Long clientId;
    private String description;

    // one-to-one relationships with:
    private NaturalPerson naturalPerson;
    private Firm firm;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT default NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne(mappedBy = "client")
    public NaturalPerson getNaturalPerson() {
        return naturalPerson;
    }

    public void setNaturalPerson(NaturalPerson naturalPerson) {
        this.naturalPerson = naturalPerson;
    }

    @OneToOne(mappedBy = "client")
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }
}