package com.github.fmcejudo.redlogs.execution;

import com.github.fmcejudo.redlogs.report.ReportController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/execution")
class ExecutionController {

    private final ExecutionService executionService;

    @Value("${redlog.report.controller-path:report}")
    private String reportPath;

    public ExecutionController(final ExecutionService executionService) {
        this.executionService = executionService;
    }

    @GetMapping(value = "/list/{applicationName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExecutionDTO>> getExecutionList(@PathVariable final String applicationName,
                                                               @RequestParam final Map<String, String> params) {
        List<ExecutionDTO> executions = executionService.findExecutionWithParameters(applicationName, params)
                .stream().map(ExecutionDTO::from).map(this::addLinks).toList();
        return ResponseEntity.ok(executions);
    }

    private ExecutionDTO addLinks(final ExecutionDTO executionDTO) {
        Link jsonLink = linkTo(
                methodOn(ReportController.class).getJsonReport(executionDTO.getExecutionId())
        ).withRel("json");

        jsonLink  = jsonLink.withHref(jsonLink.getHref().replace("${redlog.report.controller-path}", reportPath));
        executionDTO.add((jsonLink));

        Link docLink = linkTo(
                methodOn(ReportController.class).getAdocReport(executionDTO.getExecutionId())
        ).withRel("doc");

        docLink = docLink.withHref(docLink.getHref().replace("${redlog.report.controller-path}", reportPath));
        executionDTO.add((docLink));

        return executionDTO;
    }
}

