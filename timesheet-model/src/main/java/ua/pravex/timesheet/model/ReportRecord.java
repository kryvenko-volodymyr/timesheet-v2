package ua.pravex.timesheet.model;

import java.util.Date;

public class ReportRecord {

    private int hoursNum;
    private WorkInstance workInstance;
    private Date dateReported;

    public int getHoursNum() {
        return hoursNum;
    }

    public void setHoursNum(int hoursNum) {
        this.hoursNum = hoursNum;
    }

    public WorkInstance getWorkInstance() {
        return workInstance;
    }

    public void setWorkInstance(WorkInstance workInstance) {
        this.workInstance = workInstance;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportRecord)) return false;

        ReportRecord that = (ReportRecord) o;

        if (hoursNum != that.hoursNum) return false;
        return workInstance.equals(that.workInstance) && dateReported.equals(that.dateReported);

    }

    @Override
    public int hashCode() {
        int result = hoursNum;
        result = 31 * result + workInstance.hashCode();
        result = 31 * result + dateReported.hashCode();
        return result;
    }
}