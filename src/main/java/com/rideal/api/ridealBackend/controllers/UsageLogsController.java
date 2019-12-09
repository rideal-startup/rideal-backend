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

        try {
            userRef = usageLogs.get("user").toString();
            lineRef = usageLogs.get("line").toString();
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

    @GetMapping("/findByDayFromLineById")
    @ResponseBody
    public ResponseEntity<Map<Long, Long>> getLogsByDay(@RequestParam Long day) {
        Optional<Map<Long, Long>> usageLogs = usageLogsService.getLogsByDay(day);
        if (usageLogs.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(usageLogs.get(), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/findByDayFromUserByUsernameFromLineById")
    @ResponseBody
    public ResponseEntity<Map<Long, Long>> getLogsByDayFromUser(@RequestParam Long day, @RequestParam String username,
                                                                @RequestParam String line) {
        Optional<Map<Long, Long>> usageLogsList = usageLogsService.getLogsByDayFromUserInLine(day, username, line);

        if (usageLogsList.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(usageLogsList.get(), new HttpHeaders(), HttpStatus.OK);
        }
    }

    @PostMapping("/insertByUserWithTimeInLine")
    @ResponseBody
    public ResponseEntity<UsageLogs> insertUserUsageLogs(@RequestParam String username, @RequestParam Long time,
                                                         @RequestParam String lineId) {
        Optional<UsageLogs> usageLog = usageLogsService.insertUsageLog(username, time, lineId);
        if (usageLog.isEmpty()) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(usageLog.get(), new HttpHeaders(), HttpStatus.OK);
        }
    }
}
