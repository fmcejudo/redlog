package com.github.fmcejudo.redlogs.execution;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/${redlog.execution.controller-path:execution}")
class ExecutionController {

    private final ExecutionService executionService;

    @Value("${redlog.report.controller-path:report}")
    private String reportPath;

    public ExecutionController(final ExecutionService executionService) {
        this.executionService = executionService;
    }

    @GetMapping(value = "/list/{applicationName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExecutionDTO>> getExecutionList(@PathVariable final String applicationName,
                                                               @RequestParam final Map<String, String> params,
                                                               final ServerWebExchange exchange) {

        String urlBase = UrlLinkBuilder.from(exchange.getRequest()).build();
        List<ExecutionDTO> executions = executionService.findExecutionWithParameters(applicationName, params)
                .stream().map(execution -> ExecutionDTO.from(execution, urlBase)).toList();
        return ResponseEntity.ok(executions);
    }
}

