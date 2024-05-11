package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.engine.card.CardExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
class ReportController {

    private final ReportServiceProxy reportServiceProxy;
    private final CardExecutionService cardExecutionService;

    public ReportController(final ReportServiceProxy reportServiceProxy,
                            final CardExecutionService cardExecutionService) {
        this.reportServiceProxy = reportServiceProxy;
        this.cardExecutionService = cardExecutionService;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> getAdocReport(@PathVariable String applicationName) {
        return ResponseEntity.ok(reportServiceProxy.content(applicationName));
    }

    @GetMapping("/trigger/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName) {
        cardExecutionService.execute(applicationName);
        return ResponseEntity.ok("ok");
    }
}
