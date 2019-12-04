package com.rideal.api.ridealBackend.utils.strtotime;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

class DateFormatMatcher implements Matcher {

    private final DateFormat dateFormat;

    public DateFormatMatcher(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Date tryConvert(String input, String refDateStr) {
        try {
            return dateFormat.parse(input);
        } catch (ParseException ex) {
            return null;
        }
    }
}
