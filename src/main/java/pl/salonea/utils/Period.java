package pl.salonea.utils;

import java.util.Date;

/**
 * Created by michzio on 10/08/2015.
 */
public class Period {

    private Date startTime;
    private Date endTime;

    public Period(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
