package com.github.fmcejudo.redlogs.execution;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.fmcejudo.redlogs.report.domain.Execution;

class ExecutionDTO  {

    private final String executionId;
    private final String application;
    private final Map<String, String> parameters;
    private final LocalDate reportDate;
    private final List<Link> links;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ExecutionDTO(String application, String executionId, Map<String, String> parameters,
                        LocalDate reportDate, String urlBase) {
        this.application = application;
        this.executionId = executionId;
        this.parameters = parameters;
        this.reportDate = reportDate;
        this.links = List.of(
            new Link("json", urlBase + "/report/execution/"+executionId+"/json"),
            new Link("doc", urlBase + "/report/execution/"+executionId+"/doc")
        );
    }

    public static ExecutionDTO from(final Execution execution, String urlBase) {
        return new ExecutionDTO(
                execution.application(), execution.id(), execution.parameters(), execution.reportDate(), urlBase
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

    public List<Link> getLinks() {
        return links;
    }

    public record Link(String rel, String href) {
    }
}
