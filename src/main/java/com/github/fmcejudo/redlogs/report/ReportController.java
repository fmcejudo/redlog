package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.engine.card.CardExecutionService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/${redlog.report.controller-path:report}")
class ReportController {

    private final ReportServiceProxy reportServiceProxy;
    private final CardExecutionService cardExecutionService;

    public ReportController(final ReportServiceProxy reportServiceProxy,
                            final CardExecutionService cardExecutionService) {
        this.reportServiceProxy = reportServiceProxy;
        this.cardExecutionService = cardExecutionService;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> getAdocReport(@PathVariable String applicationName,
                                                @RequestParam(value = "date", required = false) String date) {
        LocalDate reportDate = parseToDate(date);
        return ResponseEntity.ok(reportServiceProxy.content(applicationName, reportDate));
    }

    @GetMapping("/trigger/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName,
                                                @RequestParam(value = "date", required = false) String date) {

        LocalDate reportDate = parseToDate(date);
        cardExecutionService.execute(applicationName, reportDate);
        return ResponseEntity.ok("ok");
    }

    private LocalDate parseToDate(String date) {
        LocalDate reportDate = LocalDate.now();
        if (Strings.isNotBlank(date)) {
            reportDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        }
        return reportDate;
    }
}
