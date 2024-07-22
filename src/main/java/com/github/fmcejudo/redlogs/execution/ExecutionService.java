package com.github.fmcejudo.redlogs.execution;

import com.github.fmcejudo.redlogs.execution.domain.Execution;

import java.util.List;
import java.util.Map;

public interface ExecutionService {

    public abstract List<Execution> findExecutionWithParameters(final String appName,
                                                                final Map<String, String> parameters);
}

