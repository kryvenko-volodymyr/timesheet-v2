package ua.pravex.timesheet.service.impl;

import ua.pravex.timesheet.persist.EmployeeDao;
import ua.pravex.timesheet.model.Employee;
import ua.pravex.timesheet.service.EmployeesExchangeService;

import javax.servlet.*;

public class EmplysExchSrvImpl implements EmployeesExchangeService {
    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }


    public Employee getEmployeeByCode(String emplCode) {
        EmployeeDao employeeDao = (EmployeeDao) servletContext.getAttribute("employeeDao");
        return employeeDao.getEmployeeByCode(emplCode);
    }
}
