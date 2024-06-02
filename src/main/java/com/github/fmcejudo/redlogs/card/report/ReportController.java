package com.github.fmcejudo.redlogs.card.report;

import com.github.fmcejudo.redlogs.card.CardContext;
import com.github.fmcejudo.redlogs.card.engine.CardExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
                                                @RequestParam(required = false) final Map<String, String> parameters) {
        CardContext cardContext = CardContext.from(applicationName, parameters);
        return ResponseEntity.ok(reportServiceProxy.content(cardContext));
    }

    @GetMapping("/trigger/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName,
                                                @RequestParam(required = false) Map<String, String> params) {

        CardContext cardContext = CardContext.from(applicationName,params);
        cardExecutionService.execute(cardContext);
        return ResponseEntity.ok("ok");
    }



}
