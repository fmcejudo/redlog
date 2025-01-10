package com.github.fmcejudo.redlogs.execution;

import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.common.link.UrlLinkBuilder;
import io.github.fmcejudo.redlogs.report.ExecutionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@RequestMapping("/${redlog.execution.controller-path:execution}")
class ReactiveExecutionController {

  private final ExecutionService executionService;

  @Value("${redlog.report.controller-path:report}")
  private String reportPath;

  public ReactiveExecutionController(final ExecutionService executionService) {
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

