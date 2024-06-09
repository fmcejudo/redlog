package com.github.fmcejudo.redlogs.card;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/${redlog.report.controller-path:card-runner}")
public class CardController {

    private final CardRunner cardRunner;

    public CardController(final CardRunner cardRunner) {
        this.cardRunner = cardRunner;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName,
                                                @RequestParam(required = false) Map<String, String> params) {

        CardContext cardContext = CardContext.from(applicationName,params);
        cardRunner.run(cardContext);
        return ResponseEntity.ok("ok");
    }
}
