package net.ajcloud.wansviewplusw.support.http.bean;

/**
 * Created by mamengchao on 2018/09/03.
 * Function:
 */
public class CalendarDayBean {
    public static int TYPE_TITTLE = 0;
    public static int TYPE_DATA = 1;
    public static int TYPE_EMPTY = 2;
    private int year;
    private int month;
    private int day;
    private int type;
    private boolean selected;
    private boolean hasRecord;

    public CalendarDayBean(int year, int month, int day, int type, boolean hasRecord, boolean selected) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.type = type;
        this.selected = selected;
        this.hasRecord = hasRecord;
    }

    public static CalendarDayBean getTittle(int year, int month, int day) {
        return new CalendarDayBean(year, month, day, TYPE_TITTLE, false, false);
    }

    public static CalendarDayBean getData(int year, int month, int day, boolean hasRecord) {
        return new CalendarDayBean(year, month, day, TYPE_DATA, hasRecord, false);
    }

    public static CalendarDayBean getEmpty() {
        return new CalendarDayBean(0, 0, 0, TYPE_EMPTY, false, false);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isHasRecord() {
        return hasRecord;
    }

    public void setHasRecord(boolean hasRecord) {
        this.hasRecord = hasRecord;
    }
}
