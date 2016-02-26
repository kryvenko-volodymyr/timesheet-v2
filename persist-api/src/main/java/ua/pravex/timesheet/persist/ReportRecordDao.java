package ua.pravex.timesheet.persist;

import ua.pravex.timesheet.model.Employee;
import ua.pravex.timesheet.model.ReportRecord;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.Set;

public interface ReportRecordDao {
    Set<ReportRecord> getRecordSet(Employee employee, Date dateFrom, Date dateTo);

    void setServletContext(ServletContext servletContext);

    void updateRecords(Set<ReportRecord> reportRecordSet, String employeeAccount);
}
