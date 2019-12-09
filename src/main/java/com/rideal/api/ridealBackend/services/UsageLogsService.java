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
import java.util.stream.Collectors;

@Service
public class UsageLogsService {

    @Autowired
    private UsageLogsRepository usageLogsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LineRepository lineRepository;

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

    private Optional<List<UsageLogs>> getUsageLogsByUserWithUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            List<UsageLogs> usageLogsList = usageLogsRepository.findAll();
            List<UsageLogs> usageLogs = usageLogsList.stream().filter(usageLogsItem ->
                    usageLogsItem.getUser().getUsername().equals(username)).collect(Collectors.toList());
            if (usageLogs.size() >= 1) return Optional.of(usageLogs);
        }
        return Optional.empty();
    }

    public Optional<User> getUserByRef(String userRef) {
        Optional<User> userOptional;
        if (userRef.split("/users/").length <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid format on user param. Try /users/id");
        }
        userOptional = userRepository.findById(userRef.split("/users/")[1]);
        return userOptional;
    }

    public Optional<Line> getLineByRef(String lineRef) {
        Optional<Line> lineOptional;
        if (lineRef.split("/lines/").length <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid format in line param. Try /lines/id");
        }
        lineOptional = lineRepository.findById(lineRef.split("/lines/")[1]);
        return lineOptional;
    }

    public Optional<Map<Long, Long>> getLogsByDay(Long day) {
        Map<Long, Long> dayLogs = new HashMap<>();
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

    public Optional<Map<Long, Long>> getLogsByDayFromUserInLine(Long timestamp, String username, String lineId) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Optional<List<UsageLogs>> optionalUsageLogs = this.getUsageLogsByUserWithUsername(username);

            if (optionalUsageLogs.isEmpty()) return Optional.empty();

            List<UsageLogs> usageLogs = optionalUsageLogs.get();
            Map<Long, Long> logs = new HashMap<>();
            usageLogs.forEach(usageLog -> {
                Date startDayDate = Str2Time.forceMidnight(new Date(timestamp));
                usageLog.getUseTime().forEach((k,v) -> {
                    if (startDayDate.getTime() <= k && k < (startDayDate.getTime() + Str2Time.DAY_LEN_MS)) {
                        logs.put(k, v);
                    }
                });
            });
            if (logs.size() >= 1) return Optional.of(logs);
        }
        return Optional.empty();
    }

    public List<UsageLogs> getAllUsageLogs() {
        return usageLogsRepository.findAll();
    }

    public Optional<UsageLogs> insertUsageLog(String username, Long timestamp, String lineId) {
        Date startDayDate = Str2Time.forceMidnight(new Date(timestamp));

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) return Optional.empty();

        Optional<Line> optionalLine = lineRepository.findById(lineId);
        if (optionalLine.isEmpty()) return Optional.empty();

        Optional<UsageLogs> usageLogsOptional = usageLogsRepository.findByUser(optionalUser.get());
        UsageLogs usageLog;
        if (usageLogsOptional.isEmpty()) {
            User user = optionalUser.get();
            Line line = optionalLine.get();
            usageLog = new UsageLogs(user.getId(), user, line, new HashMap<>(), 0L);
        } else {
            usageLog = usageLogsOptional.get();
        }
        final UsageLogs log = usageLog;

        if (log.getUseTime().size() <= 0) {
            log.getUseTime().put(Str2Time.forceResetHour(new Date(timestamp)).getTime(), 13L);
        } else {
            log.getUseTime().forEach((k, v) -> {
                if (Str2Time.forceResetHour(new Date(timestamp)).getTime() == k) {
                    log.getUseTime().put(Str2Time.forceResetHour(new Date(timestamp)).getTime(), v + 13L);
                }
            });
            if (!this.isTimestampKeyInHistory(
                    Str2Time.forceResetHour(new Date(timestamp)).getTime(), log.getUseTime())) {
                log.getUseTime().put(Str2Time.forceResetHour(new Date(timestamp)).getTime(), 13L);
            }
        }
        usageLogsRepository.save(log);
        return Optional.of(log);
    }

    private Integer getHourFromTimestamp(Long timestamp, Long startDateTimestamp) {
        double hourLong = (timestamp - startDateTimestamp)/3600000F;
        int hourInt = (int) hourLong;
        double extraHours = (hourLong - hourInt)/0.60;
        return (int) (hourInt + extraHours);
    }

    private boolean isTimestampKeyInHistory(Long key, Map<Long, Long> history) {
        for (Map.Entry<Long, Long> entry : history.entrySet()) {
            if (key.equals(entry.getKey())) {
                return true;
            }
        }
        return false;
    }
}
