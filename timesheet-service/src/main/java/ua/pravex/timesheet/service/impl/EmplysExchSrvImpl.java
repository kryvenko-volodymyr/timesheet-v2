package Service.Impl;

import Dao.EmployeeDao;
import Domain.Employee;
import Service.EmployeesExchangeService;

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
