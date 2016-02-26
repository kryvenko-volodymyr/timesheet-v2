import javax.servlet.ServletContext;

public interface EmployeesExchangeService {
    void setServletContext(ServletContext servletContext);

    public Employee getEmployeeByCode(String emplCode);

}
