package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.report.domain.Report;
import com.github.fmcejudo.redlogs.report.domain.ReportSection;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {

    List<Report> findReports(ReportContext reportContext);
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
    public List<Report> findReports(ReportContext reportContext) {
        List<ReportExecution> reportExecutions = findReportExecutions(reportContext);
        return reportExecutions.stream().map(this::findReportFromExecution).toList();
    }

    private List<ReportExecution> findReportExecutions(ReportContext reportContext) {
        Query query = Query.query(
                Criteria.where("applicationName").is(reportContext.applicationName())
                        .and("reportDate").is(reportContext.reportDate())
                        .and("parameters").is(reportContext.parameters())
        );
        return mongoTemplate.find(query, ReportExecution.class, executionsCollectionName);
    }

    private Report findReportFromExecution(final ReportExecution reportExecution) {
        String executionId = reportExecution.id();
        LocalDate reportDate = reportExecution.reportDate();
        Map<String, String> parameters = reportExecution.parameters();
        String applicationName = reportExecution.applicationName();
        List<ReportSection> reportSection = findReportSection(executionId);
        return new Report(applicationName, reportDate, parameters, reportSection);
    }

    private List<ReportSection> findReportSection(final String executionId) {
        Query query = Query.query(Criteria.where("executionId").is(executionId));
        return mongoTemplate.find(query, ReportSection.class, reportsCollectionName);
    }

    record ReportExecution(String id, String applicationName, Map<String, String> parameters, LocalDate reportDate) {

    }


}