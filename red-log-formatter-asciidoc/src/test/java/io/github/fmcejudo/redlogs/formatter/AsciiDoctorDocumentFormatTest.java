package io.github.fmcejudo.redlogs.formatter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import io.github.fmcejudo.redlogs.report.formatter.DocumentFormat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class AsciiDoctorDocumentFormatTest {

  @Test
  void shouldCreateAPDF() throws Exception {
    //Given
    Report report = new Report("TEST", LocalDate.now(), Map.of("country", "Spain"), List.of(
        new ReportSection("10", "first-section", "First Section", "my link", List.of()),
        new ReportSection("10", "second-section", "Second Section", "my link", List.of())
    ));

    //When
    DocumentFormat documentFormat = new AsciiDoctorDocumentFormat();
    String pdfDocument = documentFormat.get(report);

    //Then
    Assertions.assertThat(pdfDocument).contains("First Section").contains("Second Section");
  }

}