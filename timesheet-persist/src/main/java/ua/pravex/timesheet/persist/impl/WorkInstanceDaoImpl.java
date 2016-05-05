package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.DBProceduresManager;
import ua.pravex.timesheet.persist.WorkInstanceDao;
import ua.pravex.timesheet.model.WorkInstance;
import ua.pravex.timesheet.persist.pools.WorkFormPool;
import ua.pravex.timesheet.persist.pools.WorkInstancePool;
import ua.pravex.timesheet.persist.pools.WorkTitlePool;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WorkInstanceDaoImpl implements WorkInstanceDao {
    private ServletContext servletContext;
    private boolean getWorkInstanceProcedureCreated = false;
    private boolean createWorkInstanceProcedureCreated = false;

    @Override
    public void setServletContext(ServletContext servletContext) {

        this.servletContext = servletContext;
    }

    @Override
    public List<WorkInstance> getWorkInstances(String workTitleId, String workFormId) {
        List<WorkInstance> result = new ArrayList<>();

        if (!getWorkInstanceProcedureCreated) {
            DBProceduresManager dbProceduresManager =
                    (DBProceduresManager) servletContext.getAttribute("dbProceduresManager");

            String queryDrop = "DROP PROCEDURE IF EXISTS GET_WORK_INSTANCES";

            String createProcedure =
                    "CREATE PROCEDURE `GET_WORK_INSTANCES` ( \n" +
                            "IN workTitleId smallint(5), \n" +
                            "IN workFormId smallint(5)) \n" +
                            "BEGIN \n" +
                            "SELECT \n" +
                            "    id, work_title_id, work_form_id, details\n" +
                            "FROM\n" +
                            "    timesheet.work_instance t1\n" +
                            "WHERE\n" +
                            "    (t1.work_title_id = workTitleId\n" +
                            "        AND t1.work_form_id = workFormId)\n" +
                            "ORDER BY details; \n" +
                            "END";

            dbProceduresManager.createProcedure(queryDrop, createProcedure);

            getWorkInstanceProcedureCreated = true;
        }

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        WorkTitlePool workTitlePool = (WorkTitlePool)servletContext.getAttribute("workTitlePool");
        WorkFormPool workFormPool = (WorkFormPool)servletContext.getAttribute("workFormPool");
        WorkInstancePool workInstancePool = (WorkInstancePool)servletContext.getAttribute("workInstancePool");

        Connection conn = null;
        CallableStatement callableStatement = null;

        try {
            conn = rootConnDataSource.getConnection();
            callableStatement = conn.prepareCall("{call timesheet.GET_WORK_INSTANCES(?,?)}");
            callableStatement.setString(1, workTitleId);
            callableStatement.setString(2, workFormId);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                WorkInstance workInstance = workInstancePool
                        .add(resultSet.getInt("id"),
                                resultSet.getString("details"),
                                workTitlePool.get(resultSet.getInt("work_title_id")),
                                workFormPool.get(resultSet.getInt("work_form_id")));

                result.add(workInstance);

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

    @Override
    public WorkInstance createWorkInstance(String workTitleId, String workFormId, String employeeAccount, String workInstanceDetails) {

        if (!createWorkInstanceProcedureCreated) {
            DBProceduresManager dbProceduresManager =
                    (DBProceduresManager) servletContext.getAttribute("dbProceduresManager");

            String queryDrop = "DROP PROCEDURE IF EXISTS CREATE_AND_GET_WORK_INSTANCE";

            String createProcedure =
                    "CREATE PROCEDURE `CREATE_AND_GET_WORK_INSTANCE` ( \n" +
                            "IN workTitleId smallint(5), \n" +
                            "IN workFormId smallint(5), \n" +
                            "IN employeeAccount varchar(64), \n" +
                            "IN workInstanceDetails varchar(1023)) \n" +
                            "BEGIN \n" +
                            "INSERT INTO timesheet.work_instance \n" +
                            "    (work_title_id, work_form_id, author_employee_id, date_created, details)\n" +
                            "VALUES \n" +
                            "    (workTitleId, workFormId, (\n" +
                            "    SELECT id \n" +
                            "    FROM employee t2 \n" +
                            "    WHERE t2.account = employeeAccount \n" +
                            "    ), CURDATE(), workInstanceDetails);\n" +
                            "SELECT \n" +
                            "    *\n" +
                            "FROM\n" +
                            "    timesheet.work_instance\n" +
                            "WHERE\n" +
                            "    id = LAST_INSERT_ID(); \n" +
                            "END";

            dbProceduresManager.createProcedure(queryDrop, createProcedure);

            createWorkInstanceProcedureCreated = true;
        }

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        WorkTitlePool workTitlePool = (WorkTitlePool)servletContext.getAttribute("workTitlePool");
        WorkFormPool workFormPool = (WorkFormPool)servletContext.getAttribute("workFormPool");
        WorkInstancePool workInstancePool = (WorkInstancePool)servletContext.getAttribute("workInstancePool");

        Connection conn = null;
        CallableStatement callableStatement = null;
        WorkInstance workInstance = null;

        try {
            conn = rootConnDataSource.getConnection();
            callableStatement = conn.prepareCall("{call timesheet.CREATE_AND_GET_WORK_INSTANCE(?,?,?,?)}");
            callableStatement.setString(1, workTitleId);
            callableStatement.setString(2, workFormId);
            callableStatement.setString(3, employeeAccount);
            callableStatement.setString(4, workInstanceDetails);
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                workInstance = workInstancePool
                        .add(resultSet.getInt("id"),
                                resultSet.getString("details"),
                                workTitlePool.get(resultSet.getInt("work_title_id")),
                                workFormPool.get(resultSet.getInt("work_form_id")));
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

        return workInstance;
    }
}
