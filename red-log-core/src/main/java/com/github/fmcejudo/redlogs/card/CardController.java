package com.github.fmcejudo.redlogs.card;

import java.util.Map;

import com.github.fmcejudo.redlogs.card.exception.CardExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${redlog.card.controller-path:card-runner}")
public class CardController {

    private final CardRunner cardRunner;

    public CardController(final CardRunner cardRunner) {
        this.cardRunner = cardRunner;
    }

    @GetMapping("/{applicationName}")
    public ResponseEntity<String> triggerReport(@PathVariable String applicationName,
                                                @RequestParam(required = false) Map<String, String> params) {

        try {
            CardContext cardContext = CardContext.from(applicationName, params);
            cardRunner.run(cardContext);
            return ResponseEntity.ok("ok");
        } catch (CardExecutionException cardExecutionException) {
            return ResponseEntity.badRequest().body(cardExecutionException.getMessage());
        }
    }
}
