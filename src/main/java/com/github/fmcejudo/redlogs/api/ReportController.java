package com.github.fmcejudo.redlogs.api;

import com.github.fmcejudo.redlogs.report.ReportService;
import com.github.fmcejudo.redlogs.report.ReportServiceFactory;
import com.github.fmcejudo.redlogs.service.RedLogScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportServiceFactory reportServiceFactory;

    private final RedLogScheduler redLogScheduler;

    public ReportController(final ReportServiceFactory reportServiceFactory, final RedLogScheduler redLogScheduler) {
        this.reportServiceFactory = reportServiceFactory;
        this.redLogScheduler = redLogScheduler;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> getHtmlReport(@PathVariable String applicationName) {
        ReportService<String> htmlReportService = reportServiceFactory.getService("HTML");
        return ResponseEntity.ok(htmlReportService.get(applicationName));
    }

    @GetMapping("/adoc/{applicationName}")
    public ResponseEntity<String> getAdocReport(@PathVariable String applicationName) {
        ReportService<String> htmlReportService = reportServiceFactory.getService("ADOC");
        return ResponseEntity.ok(htmlReportService.get(applicationName));
    }

    @GetMapping("/trigger/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName) {
        redLogScheduler.execute(applicationName);
        return ResponseEntity.ok("ok");
    }
}
