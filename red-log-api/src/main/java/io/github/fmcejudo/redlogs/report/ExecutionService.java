package io.github.fmcejudo.redlogs.report;

import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.report.domain.Execution;

public interface ExecutionService {

    public abstract List<Execution> findExecutionWithParameters(final String appName,
                                                                final Map<String, String> parameters);
}

