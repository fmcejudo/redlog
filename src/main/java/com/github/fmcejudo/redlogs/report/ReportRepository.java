package com.github.fmcejudo.redlogs.report;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ReportRepository extends MongoRepository<Report, String> {

    List<Report> findByApplicationName(String applicationName);
}
