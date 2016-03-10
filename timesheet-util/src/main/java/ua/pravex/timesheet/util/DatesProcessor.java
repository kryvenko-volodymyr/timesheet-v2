package ua.pravex.timesheet.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatesProcessor {
    private static DatesProcessor ourInstance = new DatesProcessor();

    public static DatesProcessor getInstance() {
        return ourInstance;
    }

    private Calendar calendar;
    private DateFormat utcFormat;
    private DateFormat mySQLFormat;

    private DatesProcessor() {
        calendar = Calendar.getInstance();
        utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mySQLFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public Date increment (Date date, int days) {
        //Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public Date getTodayRounded() {
        calendar = Calendar.getInstance(); //TODO: get today date without new instance of calendar

        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);


        return calendar.getTime();
    }

    public Date getLocalDate (String dateString) {
        Date date = null;

        try {
            date = utcFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public String toMySQLFormatDate(Date dateReported) {
        return mySQLFormat.format(dateReported);
    }
}
