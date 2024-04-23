package com.github.fmcejudo.redlogs.writer;

import java.util.Map;

public record ReportItem(Map<String, String> labels, long count) {
}
