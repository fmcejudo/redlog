package com.github.fmcejudo.redlogs.engine.card.writer;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;
import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponseEntry;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

class CardResponseMongoWriter implements CardResponseWriter {


    private final MongoTemplate mongoTemplate;

    public CardResponseMongoWriter(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CardQueryResponse write(final CardQueryResponse cardQueryResponse) {

        final String collectionName = cardQueryResponse.applicationName();
        var cardMongoRecord = new CardMongoRecord(
                cardQueryResponse.id(),
                cardQueryResponse.description(),
                cardQueryResponse.link(),
                cardQueryResponse.date(),
                cardQueryResponse.currentEntries()
        );

        Query query = query(where("id").is(cardMongoRecord.id()));
        CardMongoRecord cardRecord = mongoTemplate.findOne(query, CardMongoRecord.class, collectionName);
        if (cardRecord != null) {
            mongoTemplate.remove(query, collectionName);
            mongoTemplate.save(cardMongoRecord, collectionName);
        } else {
            mongoTemplate.insert(cardMongoRecord, collectionName);
        }
        return cardQueryResponse;
    }


    record CardMongoRecord(String id, String reportId, String description, String link,
                           LocalDate date, List<CardQueryResponseEntry> items) {

        CardMongoRecord(String reportId, String description, String link,
                        LocalDate date, List<CardQueryResponseEntry> items) {

            this(String.join(".", reportId, date.format(ISO_DATE)), reportId, description, link, date, items);
        }
    }
}
