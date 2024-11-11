package com.github.fmcejudo.redlogs.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import io.github.fmcejudo.redlogs.report.domain.Execution;
import io.github.fmcejudo.redlogs.report.domain.Report;
import io.github.fmcejudo.redlogs.report.domain.ReportSection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public interface ReportService {

  Report findReport(String executionId);
}

class MongoReportService implements ReportService {

  private final MongoTemplate mongoTemplate;

  private final String executionsCollectionName;

  private final String reportsCollectionName;

  public MongoReportService(final MongoTemplate mongoTemplate, final RedLogMongoProperties redLogMongoProperties) {
    this.mongoTemplate = mongoTemplate;
    String collectionNamePrefix = redLogMongoProperties.getCollectionNamePrefix();
    this.reportsCollectionName = MongoNamingUtils.composeCollectionName(collectionNamePrefix, "reports");
    this.executionsCollectionName = MongoNamingUtils.composeCollectionName(collectionNamePrefix, "executions");
  }

  @Override
  public Report findReport(String executionId) {
    Execution reportExecution = findExecution(executionId);
    return findReport(reportExecution);
  }

  private Execution findExecution(String executionId) {
    return mongoTemplate.findById(executionId, Execution.class, executionsCollectionName);
  }

  private Report findReport(final Execution reportExecution) {
    if (reportExecution == null) {
      return null;
    }
    String executionId = reportExecution.id();
    LocalDate reportDate = reportExecution.reportDate();
    Map<String, String> parameters = reportExecution.parameters();
    String applicationName = reportExecution.application();
    List<ReportSection> reportSection = findReportSection(executionId);
    return new Report(applicationName, reportDate, parameters, reportSection);
  }

  private List<ReportSection> findReportSection(final String executionId) {
    Query query = Query.query(Criteria.where("executionId").is(executionId));
    return mongoTemplate.find(query, ReportSection.class, reportsCollectionName);
  }

}