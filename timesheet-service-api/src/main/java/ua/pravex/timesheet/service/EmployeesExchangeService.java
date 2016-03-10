package ua.pravex.timesheet.service;

import ua.pravex.timesheet.model.Employee;

import javax.servlet.ServletContext;

public interface EmployeesExchangeService {
    void setServletContext(ServletContext servletContext);

    public Employee getEmployeeByCode(String emplCode);

}
