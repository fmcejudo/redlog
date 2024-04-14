package com.github.fmcejudo.redlogs.client.loki;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface LokiHttpClient {

    @GetExchange(url = "/loki/api/v1/query_range")
    public abstract LokiResponse queryRange(@RequestParam(name = "query") String query,
                                            @RequestParam(name = "limit") int limit,
                                            @RequestParam(name = "start") long start,
                                            @RequestParam(name = "end") long end,
                                            @RequestParam(name = "step", defaultValue = "5m") String step);

    @GetExchange(url = "/loki/api/v1/query")
    public abstract LokiResponse query(@RequestParam(name = "query") String query,
                                       @RequestParam(name = "limit") int limit,
                                       @RequestParam(name = "time") long time);

}
