package com.rideal.api.ridealBackend.services;

import com.rideal.api.ridealBackend.models.Line;
import com.rideal.api.ridealBackend.models.UsageLogs;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.LineRepository;
import com.rideal.api.ridealBackend.repositories.UsageLogsRepository;
import com.rideal.api.ridealBackend.repositories.UserRepository;
import com.rideal.api.ridealBackend.utils.strtotime.MyUtils;
import com.rideal.api.ridealBackend.utils.strtotime.Str2Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Null;
import java.util.*;

@Service
public class UsageLogsService {

    @Autowired
    UsageLogsRepository usageLogsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    LineRepository lineRepository;

    public Optional<UsageLogs> getUsageLogsByRef(String userRef) {
        List<UsageLogs> usageLogsList = usageLogsRepository.findAll();
        Optional<UsageLogs> usageLogs = Optional.empty();

        for (UsageLogs usageLog:
             usageLogsList) {
            if (usageLog.getUser().getId().equals(userRef)) {
                usageLogs = Optional.of(usageLog);
                return usageLogs;
            }
        }
        return usageLogs;
    }

    public Optional<UsageLogs> getUsageLogsByUserWithUsername(String username) {
        List<UsageLogs> usageLogsList = usageLogsRepository.findAll();
        Optional<UsageLogs> usageLogs = Optional.empty();

        for (UsageLogs usageLog:
                usageLogsList) {
            if (usageLog.getUser().getUsername().equals(username)) {
                usageLogs = Optional.of(usageLog);
                return usageLogs;
            }
        }
        return usageLogs;
    }

    public Optional<User> getUserByRef(String userRef) {
        Optional<User> userOptional = Optional.empty();
        if (userRef.split("/users/").length <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid format on user param. Try /users/id");
        }
        userOptional = userRepository.findById(userRef.split("/users/")[1]);
        return userOptional;
    }

    public Optional<Line> getLineByRef(String lineRef) {
        Optional<Line> lineOptional = Optional.empty();
        if (lineRef.split("/lines/").length <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid format in line param. Try /lines/id");
        }
        lineOptional = lineRepository.findById(lineRef.split("/lines/")[1]);
        return lineOptional;
    }

    public Optional<Map<Integer, Long>> getLogsByDay(Long day) {
        Map<Integer, Long> dayLogs = new HashMap<>();
        List<UsageLogs> usageLogs = usageLogsRepository.findAll();
        Calendar startQueryDay = Str2Time.dateToCalendar(Str2Time.convert(Str2Time.timestampToString(day)));
        Calendar endQueryDay = Str2Time.getEndOfDay(startQueryDay);

        if (usageLogs.isEmpty()) return Optional.empty();

        for (UsageLogs log: usageLogs) {
            log.getUseTime().forEach((k, v) -> {
                Date tmpDate = new Date(k);
                if (tmpDate.compareTo(startQueryDay.getTime()) >= 0 && tmpDate.compareTo(endQueryDay.getTime()) < 0) {
                    // tmpDate is inside search margin
                    dayLogs.put(k, v);
                }
            });
        }
        return Optional.of(dayLogs);
    }

    public Optional<Map<Integer, Long>> getLogsByDayFromUser(Long day, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Map<Integer, Long> dayLogs = new HashMap<>();
        if (optionalUser.isPresent()) {
            Optional<UsageLogs> usageLogs = usageLogsRepository.findByUser(optionalUser.get());
            Calendar startQueryDay = Str2Time.dateToCalendar(Str2Time.convert("2019-12-03T18:24:25.109145+01:00"));
            Calendar endQueryDay = Str2Time.getEndOfDay(startQueryDay);

            if (usageLogs.isEmpty()) return Optional.empty();
            UsageLogs log = usageLogs.get();
            log.getUseTime().forEach((k, v) -> {
                Date tmpDate = new Date(k);
                if (tmpDate.compareTo(startQueryDay.getTime()) >= 0 && tmpDate.compareTo(endQueryDay.getTime()) < 0) {
                    // tmpDate is inside search margin
                    dayLogs.put(k, v);
                }
            });
            return Optional.of(dayLogs);
        } else {
            return Optional.empty();
        }
    }

    public List<UsageLogs> getAllUsageLogs() {
        return usageLogsRepository.findAll();
    }

    public Optional<UsageLogs> insertUsageLog(String username, Long timestamp, String lineId) {
        Date startDayDate = Str2Time.convert(Str2Time.timestampToString(timestamp));

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) return Optional.empty();

        Optional<Line> optionalLine = lineRepository.findById(lineId);
        if (optionalLine.isEmpty()) return Optional.empty();

        Optional<UsageLogs> usageLogsOptional = usageLogsRepository.findByUser(optionalUser.get());
        UsageLogs usageLog = null;
        if (usageLogsOptional.isEmpty()) {
            User user = optionalUser.get();
            Line line = optionalLine.get();
            usageLog = new UsageLogs(user.getId(), user, line, new HashMap<Integer, Long>(), 0L);
        } else {
            usageLog = usageLogsOptional.get();
        }
        final UsageLogs log = usageLog;

        if (log.getUseTime().size() <= 0) {
            log.getUseTime().put(this.getHourFromTimestamp(timestamp, startDayDate.getTime()), 13L);
        } else {
            log.getUseTime().forEach((k, v) -> {
                Map<Integer, Long> useTime = log.getUseTime();
                useTime.put(this.getHourFromTimestamp(timestamp, startDayDate.getTime()), 13L);
            });
        }
        usageLogsRepository.save(log);
        return Optional.of(log);
    }

    private Integer getHourFromTimestamp(Long timestamp, Long startDatTimestamp) {
        long hourLong = (timestamp - startDatTimestamp)/36000;
        int hourInt = Math.toIntExact(hourLong);
        int extraHours = Math.toIntExact((hourLong - hourInt)/60);
        return hourInt + extraHours;
    }
}
