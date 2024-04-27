package com.github.fmcejudo.redlogs.api;

import com.github.fmcejudo.redlogs.engine.card.CardExecutionService;
import com.github.fmcejudo.redlogs.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final CardExecutionService cardExecutionService;

    public ReportController(final ReportService reportService,
                            final CardExecutionService cardExecutionService) {
        this.reportService = reportService;
        this.cardExecutionService = cardExecutionService;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> getAdocReport(@PathVariable String applicationName) {
        return ResponseEntity.ok(reportService.get(applicationName));
    }

    @GetMapping("/trigger/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName) {
        cardExecutionService.execute(applicationName);
        return ResponseEntity.ok("ok");
    }
}
