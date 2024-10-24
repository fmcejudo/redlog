package com.github.fmcejudo.redlogs.report;

import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.report.formatter.asciidoctor.AsciiDoctorFormat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@DataMongoTest
@EnableAutoConfiguration
@EnableConfigurationProperties(value = RedLogMongoProperties.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ReportReaderService.class,
        MongoReportService.class
})
@Testcontainers(disabledWithoutDocker = true)
@TestPropertySource(properties = {
        "redlog.mongo.collection-name-prefix=test"
})
class ReportServiceProxyTest {

    private static final String COLLECTION_NAME = "testExecutions";

    @Autowired
    ReportReaderService reportServiceProxy;

    @Autowired
    MongoTemplate mongoTemplate;

    @MockBean
    AsciiDoctorFormat asciiDoctorFormat;


    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:5"));

    @DynamicPropertySource
    public static void updateApplicationContext(final DynamicPropertyRegistry registry) {
        Startables.deepStart(mongoDBContainer).join();
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }


}