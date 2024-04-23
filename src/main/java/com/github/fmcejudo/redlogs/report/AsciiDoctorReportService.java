package com.github.fmcejudo.redlogs.report;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.Placement;
import org.asciidoctor.SafeMode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Component
@Qualifier("asciidoctorReportService")
class AsciiDoctorReportService implements ReportService<String> {

    private static final String ALERTHUB_PRO_URL =
            "https://sscc.central.inditex.grp/alerthui/web/alerthub/alert-definitions/summary/pro/";

    private final ReportRepository reportRepository;

    public AsciiDoctorReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public String get(final String applicationName) {
        List<Report> reports = reportRepository.findByApplicationName(applicationName);
        return createAsciiReport(applicationName, reports);
    }

    private String createAsciiReport(final String applicationName, final List<Report> reports) {
        if (reports.isEmpty()) {
            return "NO REPORT TO SHOW";
        }
        StringBuilder documentBuilder = new StringBuilder("= ")
                .append(applicationName).append(" - ")
                .append(reports.getFirst().lastUpdated().format(ISO_LOCAL_DATE_TIME))
                .append("\n:icons: font").append("\n\n");
        for (Report report : reports) {
            documentBuilder.append(createReportItemHeader(report));
            List<ReportItem> previousItems = Optional.ofNullable(report.previousItems()).orElse(List.of());
            report.items().forEach(ri -> documentBuilder.append(createReportDetails(ri, previousItems)));
        }
        String string = documentBuilder.toString();
        try (var asciidoctor = Asciidoctor.Factory.create()) {
            return asciidoctor.convert(string, Options.builder().backend("html5")
                    .standalone(true)
                    .safe(SafeMode.UNSAFE)
                    .docType("book")
                    .attributes(Attributes.builder()
                            .icons(Attributes.FONT_ICONS)
                            .experimental(true)
                            .tableOfContents(true)
                            .tableOfContents(Placement.LEFT)
                            .sectNumLevels(3)
                            .sectionNumbers(true)
                            .hardbreaks(true)
                            .setAnchors(true).build()).build());
        }
    }

    private String createReportItemHeader(final Report report) {
        return """
                == %s
                link:%s[%s]
                
                """.formatted(report.description(), report.link(), report.description());
    }

    private String createReportDetails(final ReportItem reportItem, final List<ReportItem> previousItems) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> labels = reportItem.labels();
        boolean hasPreviousItem = previousItems.contains(reportItem);
        if (!hasPreviousItem) {
            builder.append("icon:fire[1x,role=red] **New**\n\n")
                    .append("WARNING: ");
        }
        boolean isFirstLine = true;
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            if (isFirstLine) {
                builder.append("- ");
                isFirstLine = false;
            } else {
                builder.append("  ");
            }
            if (entry.getKey().equals("alert")) {
                builder.append("**").append(entry.getKey()).append("**").append(": ")
                        .append("+\n").append("link:").append(ALERTHUB_PRO_URL)
                        .append(entry.getValue()).append("[").append(entry.getValue()).append("]").append(" +\n");
            } else {
                builder.append("**").append(entry.getKey()).append("**").append(": ")
                        .append(entry.getValue()).append(" +\n");
            }
        }
        builder.append("  **count**: ").append(reportItem.count()).append("\n\n");
        return builder.toString();
    }

}
