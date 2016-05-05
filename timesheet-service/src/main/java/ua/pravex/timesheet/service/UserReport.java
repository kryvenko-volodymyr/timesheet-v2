package ua.pravex.timesheet.service;

import ua.pravex.timesheet.model.ReportRecord;
import ua.pravex.timesheet.model.WorkInstance;
import ua.pravex.timesheet.util.DatesProcessor;

import java.util.*;

public class UserReport {
    public final Date DATE_FROM;
    public final Date DATE_TO;

    private Map<WorkInstance, UserReportRecordsLine> userReportRows;

    public Set<ReportRecord> getReportRecords() {
        Set<ReportRecord> reportRecords = new HashSet<>();

        for (UserReportRecordsLine userReportRecordsLine : userReportRows.values()) {
            for (ReportRecord reportRecord : userReportRecordsLine.getUserReportRecordsLine().values()) {
                reportRecords.add(reportRecord);
            }
        }

        return reportRecords;
    }

    private class UserReportRecordsLine {
        private Map<Date, ReportRecord> userReportRecordsLine;

        public UserReportRecordsLine(WorkInstance workInstance) {
            userReportRecordsLine = new TreeMap<>();

            DatesProcessor datesProcessor = DatesProcessor.getInstance();
            for (Date date = DATE_FROM; !date.after(DATE_TO); date = datesProcessor.increment(date, 1)) {
                ReportRecord emptyReportRecord = new ReportRecord();
                emptyReportRecord.setDateReported(date);
                emptyReportRecord.setHoursNum(0);
                //emptyReportRecord.setId(0);
                emptyReportRecord.setWorkInstance(workInstance);
                userReportRecordsLine.put(date, emptyReportRecord);
            }
        }

        public void add(ReportRecord reportRecord) {
            Date date = reportRecord.getDateReported();
            userReportRecordsLine.put(date, reportRecord);
        }

        public Map<Date, ReportRecord> getUserReportRecordsLine() {
            return userReportRecordsLine;
        }
    }

    public UserReport(Date dateFrom, Date dateTo, Set<ReportRecord> recordsSet) {
        DATE_FROM = dateFrom;
        DATE_TO = dateTo;
        userReportRows = new HashMap<>();

        for (ReportRecord reportRecord : recordsSet) {
            this.add(reportRecord);
        }
    }

    private void add(ReportRecord reportRecord) {
        WorkInstance referredWorkInstance = reportRecord.getWorkInstance();

        if (!userReportRows.containsKey(referredWorkInstance)) {
            userReportRows.put(referredWorkInstance, new UserReportRecordsLine(referredWorkInstance));
        }

        UserReportRecordsLine recordsLine = userReportRows.get(referredWorkInstance);
        recordsLine.add(reportRecord);
    }

    public Map<WorkInstance, UserReportRecordsLine> getUserReportRows() {
        return userReportRows;
    }

    public Map<Date, ReportRecord> getUserReportRecordsLine(WorkInstance workInstance) {
        UserReportRecordsLine userReportRecordsLine = userReportRows.get(workInstance);
        return userReportRecordsLine.getUserReportRecordsLine();
    }

}