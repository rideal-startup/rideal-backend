package com.rideal.api.ridealBackend.utils.strtotime;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

class TomorrowMatcher implements Matcher {

    private final Pattern tomorrow = Pattern.compile("\\W*tomorrow\\W*");

    public Date tryConvert(String input, String refDateStr) {
        if (tomorrow.matcher(input).matches()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, +1);
            return calendar.getTime();
        } else {
            return null;
        }
    }
}
