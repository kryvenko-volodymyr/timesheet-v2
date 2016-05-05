package ua.pravex.timesheet.persist;

import javax.servlet.ServletContext;

public interface DBProceduresManager {
     void createProcedure(String queryDrop, String createProcedure);

    void setServletContext(ServletContext servletContext);
}