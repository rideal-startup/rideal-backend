package com.rideal.api.ridealBackend.utils.strtotime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * source adapted & extended from: http://stackoverflow.com/questions/1268174/phps-strtotime-in-java
 * 
 * example usage:
 * Date now = Str2Time.convert("now");
 * Date tomorrow = Str2Time.convert("tomorrow");
 * Date bla1 = Str2Time.convert("3 days");
 * Date bla2 = Str2Time.convert("-3 days");
 */
public final class Str2Time {

    public static long DAY_LEN_MS = 86400000;
    private static final List<Matcher> matchers;

    static {
        matchers = new LinkedList<>();
        matchers.add(new NowMatcher());
        matchers.add(new TomorrowMatcher());
        matchers.add(new YesterdayMatcher());
        matchers.add(new DaysMatcher());
        matchers.add(new WeeksMatcher());
        
        matchers.add(new MinutesMatcher());
        
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")));// format used by FACEBOOK
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z")));
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")));
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")));// e.g. Mon, 03 Dec 2012 20:00:00
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy MM dd")));
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy"))); // format used by TWITTER! e.g. "Mon Sep 24 03:35:21 +0000 2012"
        matchers.add(new DateFormatMatcher(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ"))); // format used by DISCOURSE! e.g. "2014-03-04T08:45:17.315-05:00"
         
        // NOTE: you can add more custom matchers as you need!!!
    }

    public static void registerMatcher(Matcher matcher) {
        matchers.add(0, matcher);
    }
    
    public static Date convert(String input) {
    	return convert(input, "");
    }

    public static Date forceMidnight(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);                           // set cal to date
        cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millis in second
        return cal.getTime();
    }

    public static Date forceResetHour(Date date) {
        Calendar cal = Calendar.getInstance();       // get calendar instance
        cal.setTime(date);                           // set cal to date
        cal.set(Calendar.MINUTE, 0);                 // set minute in hour
        cal.set(Calendar.SECOND, 0);                 // set second in minute
        cal.set(Calendar.MILLISECOND, 0);            // set millis in second
        return cal.getTime();
    }

    private static Date convert(String input, String refDateStr) {
        for (Matcher matcher : matchers) {
            Date date = matcher.tryConvert(input, refDateStr);

            if (date != null) {
                return Str2Time.forceMidnight(date);
            }
        }

        return null;
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getEndOfDay(Calendar calendar) {
        calendar.add(Calendar.SECOND, 86400);
        return calendar;
    }

    public static String timestampToString(Long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
        return dateFormat.format(date);
    }

    private Str2Time() {
        throw new UnsupportedOperationException("cannot instantiate");
    }
}
