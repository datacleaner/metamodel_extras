package org.eobjects.metamodel.sas;

import org.joda.time.DateTime;
import org.joda.time.Period;

public class DateTimeConverter {

    private static final int MINYEAR= 1, MAXYEAR=9999, MINDAYS=-999999999, MAXDAYS=999999999;

    public DateTime datetimeToJava(int dateSeconds) {
        DateTime dt = new DateTime(1960, 1, 1, 0, 0).plusSeconds(dateSeconds);
        return dt;
    }

    public DateTime date9ToJava(int date9) {

        /*
         * This bounding [MINDAYS, MAXDAYS] isn't actually part of the spec --
         * it's a restriction that is inherited from the python implementation
         * of the sas7bdat format (and the use of Python datetimes in that
         * code), and it's here to make sure that the results of the two packages
         * are mutually comparable.
         */
        if(date9 < MINDAYS || date9 > MAXDAYS) {
            throw new DateConversionException(
                    "date9=%d must fall within the range [%d, %d]",
                    date9, MINDAYS, MAXDAYS);
        }

        DateTime dt = new DateTime(1960, 1, 1, 0, 0).plusDays(date9);

        /*
         * Same as above -- this is a Python-ism, which seems reasonable
         * (until we start collecting clinical study data in the year 10,000)
         * and is here to make sure that the two code bases are as functionally
         * equivalent as possible (which helps for testing).
         */
        if(dt.getYear() < MINYEAR || dt.getYear() > MAXYEAR) {
            throw new DateConversionException(
                    "year=%d must fall within the range [%d, %d]",
                    dt.getYear(), MINYEAR, MAXYEAR);
        }

        return dt;
    }

    public Period time5ToJavaPeriod(int time5) {
        /*
         * Carry over Python-determined time and timedelta bounds, see comment above.
         */
        if(time5 < MINDAYS || time5 > MAXDAYS) {
            throw new DateConversionException(
                    "time5=%d must fall within the range [%d, %d]",
                    time5, MINDAYS, MAXDAYS);
        }

        /*
         * This is confusing to me -- I don't know why, if we're only
         * measuring seconds-within-a-day for a TIME-formatted value,
         * we are storing _the total number of seconds since Jan 1, 1960_
         * BUT that appears to be what's going on here.  <SIGH>
         */
        DateTime dt = new DateTime(1960, 1, 1, 0, 0).plusSeconds(time5);

        /*
         * Carry over Python-determined time and timedelta bounds, see comment above.
         */
        if(dt.getYear() < MINYEAR || dt.getYear() > MAXYEAR) {
            throw new DateConversionException(
                    "year=%d must fall within the range [%d, %d]",
                    dt.getYear(), MINYEAR, MAXYEAR);
        }

        /*
         * for TIME values, we only return the hours/minutes/seconds
         * as a separate Period value.
         */
        int hours = dt.getHourOfDay();
        int minutes = dt.getMinuteOfHour();
        int seconds = dt.getSecondOfMinute();
        int millis = dt.getMillisOfSecond();

        return new Period(hours, minutes, seconds, millis);
    }
}
