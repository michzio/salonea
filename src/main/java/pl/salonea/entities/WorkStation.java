package pl.salonea.entities;

import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.WorkStationType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "work_station")
@Access(AccessType.PROPERTY)
@IdClass(WorkStationId.class)
public class WorkStation implements Serializable{

    private Integer workStationNumber; // PK
    private ServicePoint servicePoint; // composite PK, composite FK

    private WorkStationType workStationType;

    /* one-to-many relationship */
    private Set<TermEmployeeWorkOn> termsEmployeesWorkOn;

    /* constructors */

    public  WorkStation() { }

    public WorkStation(ServicePoint servicePoint, Integer workStationNumber) {
        this.servicePoint = servicePoint;
        this.workStationNumber = workStationNumber;
    }

    public WorkStation(ServicePoint servicePoint,Integer workStationNumber, WorkStationType workStationType) {
        this.servicePoint = servicePoint;
        this.workStationNumber = workStationNumber;
        this.workStationType = workStationType;
    }

    /* PK getters and setters */

    @Id
    @Basic(optional = false)
    @Column(name = "work_station_no", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getWorkStationNumber() {
        return workStationNumber;
    }

    public void setWorkStationNumber(Integer workStationNumber) {
        this.workStationNumber = workStationNumber;
    }

    @Id
    @NotNull
    @JoinColumns(value = {
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED"),
            @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED"),
    })
    @ManyToOne(fetch = FetchType.EAGER)
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    /* other getters and setters */

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "work_station_type", nullable = false, columnDefinition = "ENUM('ROOM', 'CHAIR', 'OFFICE', 'GARAGE', 'OTHER') DEFAULT 'OTHER'")
    public WorkStationType getWorkStationType() {
        return workStationType;
    }

    public void setWorkStationType(WorkStationType workStationType) {
        this.workStationType = workStationType;
    }

    /* one-to-many relationship */

    @OneToMany(mappedBy = "workStation", fetch = FetchType.LAZY)
    public Set<TermEmployeeWorkOn> getTermsEmployeesWorkOn() {
        return termsEmployeesWorkOn;
    }

    public void setTermsEmployeesWorkOn(Set<TermEmployeeWorkOn> termsEmployeesWorkOn) {
        this.termsEmployeesWorkOn = termsEmployeesWorkOn;
    }
}
