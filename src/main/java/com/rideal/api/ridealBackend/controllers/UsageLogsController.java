package com.rideal.api.ridealBackend.controllers;

import com.google.gson.JsonObject;
import com.rideal.api.ridealBackend.models.Line;
import com.rideal.api.ridealBackend.models.UsageLogs;
import com.rideal.api.ridealBackend.models.User;
import com.rideal.api.ridealBackend.repositories.UsageLogsRepository;
import com.rideal.api.ridealBackend.services.UsageLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@BasePathAwareController
@RestController
@RequestMapping("/logs")
public class UsageLogsController {

    @Autowired
    private UsageLogsService usageLogsService;

    @PostMapping("/logs/ref")
    @ResponseBody
    public HttpStatus addLogs(JsonObject usageLogs) {
        String userRef;
        String lineRef;
        Long useTime = null;
        Long date = null;

        try {
            userRef = usageLogs.get("user").toString();
            lineRef = usageLogs.get("line").toString();
            useTime = usageLogs.get("useTime").getAsLong();
            date = usageLogs.get("date").getAsLong();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fields in POST request JSON body (user," +
                    " line, useTime, date)");
        }

        Optional<User> optionalUser = usageLogsService.getUserByRef(userRef);
        Optional<Line> optionalLine = usageLogsService.getLineByRef(lineRef);

        // UsageLogs usageLogs = new UsageLogs()
        return HttpStatus.OK;
    }

    @GetMapping("/findByUserId")
    @ResponseBody
    public ResponseEntity<UsageLogs> getLinesByUser(String usageLogsId) {
        Optional<UsageLogs> usageLogsOptional = usageLogsService.getUsageLogsByRef(usageLogsId);

        return usageLogsOptional.map(usageLogs -> new ResponseEntity<>(
                usageLogs, new HttpHeaders(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(
                null, new HttpHeaders(), HttpStatus.NOT_FOUND));

    }

    @GetMapping("/findLogsByDay")
    @ResponseBody
    public ResponseEntity<Map<Integer, Long>> getLogsByDay(@RequestParam Long day) {
        Optional<Map<Integer, Long>> usageLogs = usageLogsService.getLogsByDay(day);
        if (usageLogs.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usageLogs.get(), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/findLogsByDayFromUserWithUsername?username={username}&day={day}}")
    @ResponseBody
    public ResponseEntity<Map<Integer, Long>> getLogsByDayFromUser(@RequestParam String username, @RequestParam Long day) {
        Optional<Map<Integer, Long>> usageLogsList = usageLogsService.getLogsByDayFromUser(day, username);

        if (usageLogsList.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(usageLogsList.get(), new HttpHeaders(), HttpStatus.OK);
        }
    }

    @GetMapping("/insertByUserWithTimeInLine")
    @ResponseBody
    public ResponseEntity<UsageLogs> insertUserUsageLogs(@RequestParam String username, @RequestParam Long time, @RequestParam String lineId) {
        Optional<UsageLogs> usageLog = usageLogsService.insertUsageLog(username, time, lineId);
        if (usageLog.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
        }
    }
}
