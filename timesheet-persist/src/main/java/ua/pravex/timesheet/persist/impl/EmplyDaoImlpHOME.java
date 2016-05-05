package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.EmployeeDao;
import ua.pravex.timesheet.model.Employee;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmplyDaoImlpHOME implements EmployeeDao {

    private ServletContext servletContext;

    //the below is a mock method for development purposes only
    public Employee getEmployeeByCode(String emplAccount) {
        Employee employee = new Employee();
        employee.setAccount(emplAccount);

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        Connection conn = null;

        //TODO the actual username must be fetched from the AD and updated to the MySQL
        try {
            conn = rootConnDataSource.getConnection();
            Statement statement = conn.createStatement();
            ResultSet resultSet
                    = statement.executeQuery("SELECT * FROM timesheet.employee where account = '" + emplAccount + "'");
            if (resultSet.next()){
                String employeeName = resultSet.getString("name");
                employee.setName(employeeName);
            } else {
                employee.setName("Користувач " + emplAccount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return employee;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}