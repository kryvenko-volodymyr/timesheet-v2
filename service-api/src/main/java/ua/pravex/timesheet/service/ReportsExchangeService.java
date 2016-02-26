package ua.pravex.timesheet.service;

import ua.pravex.timesheet.model.Employee;

import javax.servlet.ServletContext;
import java.util.Date;

public interface ReportsExchangeService {
    final int DEFAULT_NUM_DAYS_REPORTED = 20;

    public void setServletContext(ServletContext servletContext);

    public UserReport getUserReport(Employee employee);

    public UserReport getUserReport(Employee employee, int daysReported);

    public UserReport getUserReport(Employee employee, Date dateFrom, Date dateTo);

    void saveReport(UserReport userReport, String employeeAccount);
}