package ua.pravex.timesheet.persist.impl;

import ua.pravex.timesheet.persist.DBProceduresManager;
import ua.pravex.timesheet.persist.ReportRecordDao;
import ua.pravex.timesheet.model.Employee;
import ua.pravex.timesheet.persist.pools.Poolable;
import ua.pravex.timesheet.model.ReportRecord;
import ua.pravex.timesheet.model.WorkInstance;
import ua.pravex.timesheet.persist.pools.WorkFormPool;
import ua.pravex.timesheet.persist.pools.WorkInstancePool;
import ua.pravex.timesheet.persist.pools.WorkTitlePool;
import ua.pravex.timesheet.persist.pools.WorkTypePool;
import ua.pravex.timesheet.util.DatesProcessor;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RprtRcrdDaoImlpHOME implements ReportRecordDao {
    private ServletContext servletContext;
    private boolean getReportRecordEagerProcedureCreated = false;


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void updateRecords(Set<ReportRecord> reportRecordSet, String employeeAccount) {
        DatesProcessor datesProcessor = DatesProcessor.getInstance();
        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        Connection conn = null;

        Set<ReportRecord> deleteSet = new HashSet<>();
        Set<ReportRecord> insertOrUpdateSet = new HashSet<>();

        for (ReportRecord reportRecord : reportRecordSet) {
            if (reportRecord.getHoursNum() == 0) {
                deleteSet.add(reportRecord);
            } else {
                insertOrUpdateSet.add(reportRecord);
            }
        }

        try {
            conn = rootConnDataSource.getConnection();
            String employeeId = "";

            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT id " +
                    "FROM employee " +
                    "WHERE account = '" + employeeAccount + "'");
            if (resultSet.next()) {
                employeeId = resultSet.getString("id");
            }
            resultSet.close();
            stmt.close();

            stmt = conn.createStatement();
            for (ReportRecord reportRecord : deleteSet) {
                String MySQLFormatDate = datesProcessor.toMySQLFormatDate(reportRecord.getDateReported());
                stmt.addBatch("DELETE FROM report_record " +
                        "WHERE date_reported = '" + MySQLFormatDate +  "' " +
                        "AND work_instance_id = " + reportRecord.getWorkInstance().getId() + " " +
                        "AND author_employee_id = " + employeeId);
            }

            for (ReportRecord reportRecord : insertOrUpdateSet) {
                String MySQLFormatDate = datesProcessor.toMySQLFormatDate(reportRecord.getDateReported());
                stmt.addBatch("INSERT INTO report_record " +
                        "(date_reported, hours_num, work_instance_id, author_employee_id)" +
                        "VALUES ('" +
                        MySQLFormatDate + "', " +
                        reportRecord.getHoursNum() + ", " +
                        reportRecord.getWorkInstance().getId() + ", " +
                        employeeId + ") " +
                        "ON DUPLICATE KEY UPDATE " +
                        "hours_num = " + reportRecord.getHoursNum());
            }
            /*int[] rows = stmt.executeBatch( );*/
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

        //todo: delete o-hour records if exist, create/update n-hour records
    }

    @Override
    public Set<ReportRecord> getRecordSet(Employee employee, Date dateFrom, Date dateTo) {
        Set<ReportRecord> result = new HashSet<>();

        if (!getReportRecordEagerProcedureCreated) {
            DBProceduresManager dbProceduresManager =
                    (DBProceduresManager) servletContext.getAttribute("dbProceduresManager");

            String queryDrop = "DROP PROCEDURE IF EXISTS GET_REPORT_RECORD_EXPLODED";

            String createProcedure =
                    "create procedure GET_REPORT_RECORD_EXPLODED (\n" +
                            "IN employeeAccount varchar(64),\n" +
                            "IN dateFrom date,\n" +
                            "IN dateTo date)\n" +
                            "BEGIN\n" +
                            "SELECT\n" +
                            "t1.id AS report_record_id,\n" +
                            "t1.date_reported AS date_reported,\n" +
                            "t1.hours_num AS hours_num,\n" +
                            "t2.id AS work_instance_id,\n" +
                            "t2.details AS work_instance_details,\n" +
                            "t3.id AS work_title_id,\n" +
                            "t3.title AS work_title_title,\n" +
                            "t4.id AS work_form_id,\n" +
                            "t4.title AS work_form_title,\n" +
                            "t6.id AS work_type_id,\n" +
                            "t6.title AS work_type_title\n" +
                            "FROM\n" +
                            "(((((report_record t1\n" +
                            "JOIN work_instance t2 ON (t1.work_instance_id = t2.id))\n" +
                            "JOIN work_title t3 ON (t2.work_title_id = t3.id))\n" +
                            "JOIN work_form t4 ON (t2.work_form_id = t4.id))\n" +
                            "JOIN employee t5 ON (t1.author_employee_id = t5.id)))\n" +
                            "JOIN work_type t6 ON (t3.work_type_id = t6.id)\n" +
                            "WHERE t5.account = employeeAccount\n" +
                            "AND t1.date_reported >= dateFrom\n" +
                            "AND t1.date_reported <= dateTo;\n" +
                            "END";

            dbProceduresManager.createProcedure(queryDrop, createProcedure);

            getReportRecordEagerProcedureCreated = true;
        }

        DataSource rootConnDataSource = (DataSource) servletContext.getAttribute("rootConnDataSource");
        WorkTypePool workTypePool = (WorkTypePool) servletContext.getAttribute("workTypePool");
        WorkTitlePool workTitlePool = (WorkTitlePool) servletContext.getAttribute("workTitlePool");
        WorkFormPool workFormPool = (WorkFormPool) servletContext.getAttribute("workFormPool");
        WorkInstancePool workInstancePool = (WorkInstancePool) servletContext.getAttribute("workInstancePool");

        Connection conn = null;
        CallableStatement callableStatement = null;

        try {
            conn = rootConnDataSource.getConnection();
            callableStatement = conn.prepareCall("{call timesheet.GET_REPORT_RECORD_EXPLODED(?,?,?)}");
            callableStatement.setString(1, employee.getAccount());
            callableStatement.setDate(2, new java.sql.Date(dateFrom.getTime()));
            callableStatement.setDate(3, new java.sql.Date(dateTo.getTime()));
            ResultSet resultSet = callableStatement.executeQuery();
            while (resultSet.next()) {
                ReportRecord reportRecord = new ReportRecord();
                //reportRecord.setId(resultSet.getInt("report_record_id")); // don`t really need the id
                reportRecord.setDateReported(new Date(resultSet.getDate("date_reported").getTime()));
                reportRecord.setHoursNum(resultSet.getInt("hours_num"));

                //the order matters for the calls below
                Poolable workType = workTypePool
                        .add(resultSet.getInt("work_type_id"),
                                resultSet.getString("work_type_title"));
                Poolable workTitle = workTitlePool
                        .add(resultSet.getInt("work_title_id"),
                                resultSet.getString("work_title_title"),
                                workType);
                Poolable workForm = workFormPool
                        .add(resultSet.getInt("work_form_id"),
                                resultSet.getString("work_form_title"));
                Poolable workInstance = workInstancePool
                        .add(resultSet.getInt("work_instance_id"),
                                resultSet.getString("work_instance_details"),
                                workTitle,
                                workForm);

                reportRecord.setWorkInstance((WorkInstance) workInstance);
                result.add(reportRecord);

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
