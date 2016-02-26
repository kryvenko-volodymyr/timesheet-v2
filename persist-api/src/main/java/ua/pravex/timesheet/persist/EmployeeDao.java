package ua.pravex.timesheet.persist;

import ua.pravex.timesheet.model.Employee;
import javax.servlet.ServletContext;


public interface EmployeeDao {
    public Employee getEmployeeByCode (String emplCode);

    void setServletContext(ServletContext servletContext);
}
