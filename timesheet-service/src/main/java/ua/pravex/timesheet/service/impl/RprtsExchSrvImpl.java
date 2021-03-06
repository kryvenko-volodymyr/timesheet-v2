package ua.pravex.timesheet.service.impl;

import ua.pravex.timesheet.persist.ReportRecordDao;
import ua.pravex.timesheet.model.Employee;
import ua.pravex.timesheet.service.UserReport;
import ua.pravex.timesheet.model.ReportRecord;
import ua.pravex.timesheet.service.ReportsExchangeService;
import ua.pravex.timesheet.util.DatesProcessor;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.Set;

public class RprtsExchSrvImpl implements ReportsExchangeService {

    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public UserReport getUserReport(Employee employee) {
        UserReport result = getUserReport(employee, DEFAULT_NUM_DAYS_REPORTED);
        return result;
    }

    @Override
    public UserReport getUserReport(Employee employee, int daysReported) {

        DatesProcessor datesProcessor = DatesProcessor.getInstance();

        Date dateToday = datesProcessor.getTodayRounded();
        Date dateFrom = datesProcessor.increment(dateToday, -DEFAULT_NUM_DAYS_REPORTED);

        UserReport result = getUserReport(employee, dateFrom, dateToday);
        return result;
    }

    @Override
    public UserReport getUserReport(Employee employee, Date dateFrom, Date dateTo) {

        ReportRecordDao reportRecordDao = (ReportRecordDao)servletContext.getAttribute("reportRecordDao");
        Set<ReportRecord> recordSet = reportRecordDao.getRecordSet(employee, dateFrom, dateTo);

        return new UserReport(dateFrom, dateTo, recordSet);
    }

    @Override
    public void saveReport(UserReport userReport, String employeeAccount) {
        ReportRecordDao reportRecordDao = (ReportRecordDao)servletContext.getAttribute("reportRecordDao");
        Set<ReportRecord> reportRecordSet = userReport.getReportRecords();
        reportRecordDao.updateRecords(reportRecordSet, employeeAccount);

        //TODO: maybe some confirmation here?
    }

}