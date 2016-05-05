package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.DBProceduresManager;
import ua.pravex.timesheet.persist.WorkTypeDao;
import ua.pravex.timesheet.model.WorkType;

import ua.pravex.timesheet.persist.pools.WorkTypePool;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


public class WorkTypeDaoImpl implements WorkTypeDao {

    private ServletContext servletContext;
    private boolean getReportRecordEagerProcedureCreated = false;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public List<WorkType> getWorkTypes() {
        List<WorkType> result = new ArrayList<>();

        if (!getReportRecordEagerProcedureCreated) {
            DBProceduresManager dbProceduresManager =
                    (DBProceduresManager)servletContext.getAttribute("dbProceduresManager");

            String queryDrop = "DROP PROCEDURE IF EXISTS GET_WORK_TYPES";

            String createProcedure =
                    "CREATE PROCEDURE `GET_WORK_TYPES` ()\n" +
                            "BEGIN\n" +
                            "select id, title from work_type;\n" +
                            "END";

            dbProceduresManager.createProcedure(queryDrop, createProcedure);

            getReportRecordEagerProcedureCreated = true;
        }

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        WorkTypePool workTypePool = (WorkTypePool) servletContext.getAttribute("workTypePool");

        Connection conn = null;
        CallableStatement callableStatement = null;

        try {
            conn = rootConnDataSource.getConnection();
            callableStatement = conn.prepareCall("{call timesheet.GET_WORK_TYPES}");
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                WorkType workType = workTypePool
                        .add(resultSet.getInt("id"),
                                resultSet.getString("title"));
                result.add(workType);
            }
            callableStatement.clearParameters();
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
        return result;
    }
}
