package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
class ReportRepository {

    private final MongoTemplate mongoTemplate;
    private final RedLogMongoProperties redLogMongoConfigProperties;

    public ReportRepository(final MongoTemplate mongoTemplate,
                            final RedLogMongoProperties redLogMongoConfigProperties) {
        this.mongoTemplate = mongoTemplate;
        this.redLogMongoConfigProperties = redLogMongoConfigProperties;
    }

    List<Report> getReportCompareWithDate(final String applicationName, final LocalDate date) {

        final String collectionName = MongoNamingUtils.composeCollectionName(
                redLogMongoConfigProperties.getCollectionNamePrefix(),
                applicationName
        );

        final List<Report> latestReports = findByApplicationAndDate(collectionName, date);
        final List<Report> previousReports = findByApplicationAndDate(collectionName, date.minusDays(1));

        int i = 0;
        int j = 0;
        Report[] latestReportIt = latestReports.toArray(Report[]::new);
        Report[] previousReportIt = previousReports.toArray(Report[]::new);
        List<Report> result = new ArrayList<>();
        while (i < latestReportIt.length && j < previousReportIt.length) {
            Report r1 = latestReportIt[i];
            Report r2 = previousReportIt[j];
            if (r1.reportId().compareTo(r2.reportId()) < 0) {
                result.add(r1);
                i++;
            } else if (r1.reportId().compareTo(r2.reportId()) > 0) {
                result.add(
                        new Report(r2.reportId(), r2.lastUpdated(), r2.link(), r2.description(), List.of(), r2.items())
                );
                j++;
            } else {
                result.add(
                        new Report(r1.reportId(), r1.lastUpdated(), r1.link(), r1.description(), r1.items(), r2.items())
                );
                i++;
                j++;
            }
        }

        while (i < latestReportIt.length) {
            result.add(latestReportIt[i]);
            i++;
        }
        while (j < previousReportIt.length) {
            result.add(previousReportIt[j]);
            j++;
        }

        return result;
    }

    private List<Report> findByApplicationAndDate(String collectionName, LocalDate date) {
        Query query = Query.query(Criteria.where("date").is(date));
        return mongoTemplate.find(query, Report.class, collectionName);
    }


}
