package com.github.fmcejudo.redlogs.card.writer;

import com.github.fmcejudo.redlogs.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.card.model.CardQueryResponseEntry;
import com.github.fmcejudo.redlogs.config.RedLogMongoProperties;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static com.github.fmcejudo.redlogs.util.MongoNamingUtils.composeCollectionName;

class CardResponseMongoWriter implements CardResponseWriter {

    private final MongoTemplate mongoTemplate;

    private final String reportCollectionName;

    public CardResponseMongoWriter(final MongoTemplate mongoTemplate,
                                   final RedLogMongoProperties redLogMongoConfigProperties) {

        this.mongoTemplate = mongoTemplate;
        String mongoPrefix = redLogMongoConfigProperties.getCollectionNamePrefix();
        this.reportCollectionName = composeCollectionName(mongoPrefix, "reports");
    }

    @Override
    public CardQueryResponse write(final CardQueryResponse cardQueryResponse) {
        var cardMongoRecord = new CardMongoRecord(
                cardQueryResponse.id(),
                cardQueryResponse.executionId(),
                cardQueryResponse.description(),
                cardQueryResponse.link(),
                cardQueryResponse.currentEntries()
        );
        mongoTemplate.insert(cardMongoRecord, reportCollectionName);
        return cardQueryResponse;
    }

    static final class CardMongoRecord {
        private String id;
        private String reportId;
        private String description;
        private String link;
        private String executionId;
        private List<CardQueryResponseEntry> items;

        public CardMongoRecord() {
        }

        CardMongoRecord(String reportId, String executionId, String description,
                        String link, List<CardQueryResponseEntry> items) {

            this.id = String.join(".", reportId, executionId);
            this.reportId = reportId;
            this.description = description;
            this.link = link;
            this.items = items;
            this.executionId = executionId;

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
