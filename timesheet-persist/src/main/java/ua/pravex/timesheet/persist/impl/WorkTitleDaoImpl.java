package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.DBProceduresManager;
import ua.pravex.timesheet.persist.WorkTitleDao;
import ua.pravex.timesheet.model.WorkTitle;
import ua.pravex.timesheet.persist.pools.WorkTitlePool;
import ua.pravex.timesheet.persist.pools.WorkTypePool;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkTitleDaoImpl implements WorkTitleDao {
    private ServletContext servletContext;
    private boolean getReportRecordEagerProcedureCreated = false;

    @Override
    public void setServletContext(ServletContext servletContext) {

        this.servletContext = servletContext;
    }

    @Override
    public List<WorkTitle> getWorkTitles(String workTypeId) {
        List<WorkTitle> result = new ArrayList<>();

        if (!getReportRecordEagerProcedureCreated) {
            DBProceduresManager dbProceduresManager =
                    (DBProceduresManager) servletContext.getAttribute("dbProceduresManager");

            String queryDrop = "DROP PROCEDURE IF EXISTS GET_WORK_TITLES";

            String createProcedure =
                    "CREATE PROCEDURE `GET_WORK_TITLES` (" +
                            "IN workTypeId smallint(5))\n" +
                            "BEGIN\n" +
                            "select id, title, work_type_id \n" +
                            "from timesheet.work_title t1 \n" +
                            "where t1.work_type_id = workTypeId \n" +
                            "order by title;" +
                            "END";

            dbProceduresManager.createProcedure(queryDrop, createProcedure);

            getReportRecordEagerProcedureCreated = true;
        }

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        WorkTypePool workTypePool = (WorkTypePool)servletContext.getAttribute("workTypePool");
        WorkTitlePool workTitlePool = (WorkTitlePool)servletContext.getAttribute("workTitlePool");

        Connection conn = null;
        CallableStatement callableStatement = null;

        try {
            conn = rootConnDataSource.getConnection();
            callableStatement = conn.prepareCall("{call timesheet.GET_WORK_TITLES(?)}");
            callableStatement.setString(1, workTypeId);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                WorkTitle workTitle = workTitlePool
                        .add(resultSet.getInt("id"),
                                resultSet.getString("title"),
                                workTypePool.get(resultSet.getInt("work_type_id")));

                result.add(workTitle);

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
