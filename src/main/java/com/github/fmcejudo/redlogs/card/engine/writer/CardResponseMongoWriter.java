package com.github.fmcejudo.redlogs.card.engine.writer;

import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.engine.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import com.github.fmcejudo.redlogs.util.MongoNamingUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

class CardResponseMongoWriter implements CardResponseWriter {

    private final MongoTemplate mongoTemplate;

    private final RedLogMongoProperties redLogMongoConfigProperties;

    public CardResponseMongoWriter(final MongoTemplate mongoTemplate, RedLogMongoProperties redLogMongoConfigProperties) {
        this.mongoTemplate = mongoTemplate;
        this.redLogMongoConfigProperties = redLogMongoConfigProperties;
    }

    @Override
    public CardQueryResponse write(final CardQueryResponse cardQueryResponse) {
        String collectionName = MongoNamingUtils.composeCollectionName(
                redLogMongoConfigProperties.getCollectionNamePrefix(),
                cardQueryResponse.applicationName()
        );
        var cardMongoRecord = new CardMongoRecord(
                cardQueryResponse.id(),
                cardQueryResponse.executionId(),
                cardQueryResponse.description(),
                cardQueryResponse.link(),
                cardQueryResponse.date(),
                cardQueryResponse.currentEntries()
        );

        Query query = query(where("id").is(cardMongoRecord.getId()));
        CardMongoRecord cardRecord = mongoTemplate.findOne(query, CardMongoRecord.class, collectionName);
        if (cardRecord != null) {
            mongoTemplate.remove(query, collectionName);
            mongoTemplate.save(cardMongoRecord, collectionName);
        } else {
            mongoTemplate.insert(cardMongoRecord, collectionName);
        }
        return cardQueryResponse;
    }


    static final class CardMongoRecord {
        private String id;
        private String reportId;
        private String description;
        private String link;
        private LocalDate date;
        private String executionId;
        private List<CardQueryResponseEntry> items;

        public CardMongoRecord() {
        }

        CardMongoRecord(String reportId, String executionId, String description, String link,
                        LocalDate date, List<CardQueryResponseEntry> items) {

            this.id = String.join(".", reportId, date.format(ISO_DATE));
            this.reportId = reportId;
            this.description = description;
            this.link = link;
            this.date = date;
            this.items = items;
            this.executionId = executionId;

        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<CardQueryResponseEntry> getItems() {
            return items;
        }

        public void setItems(List<CardQueryResponseEntry> items) {
            this.items = items;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public String getExecutionId() {
            return executionId;
        }

        public void setExecutionId(String executionId) {
            this.executionId = executionId;
        }
    }
}
