package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.DBProceduresManager;
import ua.pravex.timesheet.persist.WorkFormDao;
import ua.pravex.timesheet.model.WorkForm;
import ua.pravex.timesheet.persist.pools.WorkFormPool;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WorkFormDaoImpl implements WorkFormDao {
    private ServletContext servletContext;
    private boolean getReportRecordEagerProcedureCreated = false;

    @Override
    public void setServletContext(ServletContext servletContext) {

        this.servletContext = servletContext;
    }

    @Override
    public List<WorkForm> getWorkForms(String workTypeId) {
        List<WorkForm> result = new ArrayList<>();

        if (!getReportRecordEagerProcedureCreated) {
            DBProceduresManager dbProceduresManager =
                    (DBProceduresManager) servletContext.getAttribute("dbProceduresManager");

            String queryDrop = "DROP PROCEDURE IF EXISTS GET_WORK_FORMS";

            String createProcedure =
                    "CREATE PROCEDURE `GET_WORK_FORMS` ( \n" +
                            "IN workTypeId smallint(5)) \n" +
                            "BEGIN\n" +
                            "select id, title \n" +
                            "from timesheet.work_form t1 \n" +
                            "join timesheet.work_form_by_type t2 \n" +
                            "on (t1.id = t2.work_form_id) \n" +
                            "where t2.work_type_id = workTypeId \n" +
                            "order by title; \n" +
                            "END";

            dbProceduresManager.createProcedure(queryDrop, createProcedure);

            getReportRecordEagerProcedureCreated = true;
        }

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        WorkFormPool workFormPool = (WorkFormPool)servletContext.getAttribute("workFormPool");

        Connection conn = null;
        CallableStatement callableStatement = null;

        try {
            conn = rootConnDataSource.getConnection();
            callableStatement = conn.prepareCall("{call timesheet.GET_WORK_FORMS(?)}");
            callableStatement.setString(1, workTypeId);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                WorkForm workForm = workFormPool
                        .add(resultSet.getInt("id"),
                                resultSet.getString("title"));

                result.add(workForm);

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
