package com.github.fmcejudo.redlogs.execution;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.fmcejudo.redlogs.execution.domain.Execution;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class ExecutionDTO  {

    private final String executionId;
    private final String application;
    private final Map<String, String> parameters;
    private final LocalDate reportDate;
    private final List<Link> links;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ExecutionDTO(String application, String executionId, Map<String, String> parameters, LocalDate reportDate) {
        this.application = application;
        this.executionId = executionId;
        this.parameters = parameters;
        this.reportDate = reportDate;
        this.links = List.of(
            new Link("json", "http://localhost:8080/report/execution/"+executionId+"/json"),
            new Link("doc", "http://localhost:8080/report/execution/"+executionId+"/doc")
        );
    }

    public static ExecutionDTO from(final Execution execution) {
        return new ExecutionDTO(
                execution.application(), execution.id(), execution.parameters(), execution.reportDate()
        );
    }

    public String getApplication() {
        return application;
    }

    public String getExecutionId() {
        return executionId;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public record Link(String rel, String href) {


    }
}
