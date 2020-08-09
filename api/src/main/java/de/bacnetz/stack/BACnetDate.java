package de.bacnetz.stack;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import de.bacnetz.common.utils.Utils;

public class BACnetDate {

    /**
     * e.g. 2012. The year in this class is NOT stored in the BACnet notation (year
     * = 1900 + bacnetYear) but as a normal year.
     */
    int year;

    int month;

    int dayOfMonth;

    /** 1 = Monday, ..., 7 == Sunday */
    int dayOfWeek;

    /**
     * ctor
     */
    public BACnetDate() {
        final Date now = new Date();
        fromDate(now);
    }

    /**
     * ctor
     * 
     * @param date
     */
    public BACnetDate(final Date date) {
        fromDate(date);
    }

    /**
     * ctor
     * 
     * @param year       - normal year such as 2012. NOT bacnet encoded value (1900
     *                   + year)
     * @param month
     * @param dayOfMonth
     */
    public BACnetDate(final int year, final int month, final int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

        this.dayOfWeek = toLocalDate().getDayOfWeek().getValue();
    }

    public void fromDate(final Date date) {

        final LocalDate localDate = Utils.dateToLocalDate(date);

        this.year = localDate.getYear();
        this.month = localDate.getMonthValue();
        this.dayOfMonth = localDate.getDayOfMonth();
        this.dayOfWeek = localDate.getDayOfWeek().getValue();
    }

    public void fromLocalDateTime(final LocalDateTime localDateTime) {

        this.year = localDateTime.getYear();
        this.month = localDateTime.getMonthValue();
        this.dayOfMonth = localDateTime.getDayOfMonth();
        this.dayOfWeek = localDateTime.getDayOfWeek().getValue();
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public Date toDate() {
        final LocalDate localDate = toLocalDate();
        return Utils.localDateToDate(localDate);
    }

    @Override
    public String toString() {
        return toLocalDate().toString();
    }

    public int getYear() {
        return year;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(final int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(final int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(final int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

}
