package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.DBProceduresManager;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class DBProceduresManagerImpl implements DBProceduresManager {
    private ServletContext servletContext;

    @Override
    public void createProcedure(String queryDrop, String createProcedure) {
        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        Connection conn = null;

        try {
            conn = rootConnDataSource.getConnection();

            Statement stmtDrop = conn.createStatement();
            stmtDrop.execute(queryDrop);

            Statement createStmt = conn.createStatement();
            createStmt.executeUpdate(createProcedure);

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
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
