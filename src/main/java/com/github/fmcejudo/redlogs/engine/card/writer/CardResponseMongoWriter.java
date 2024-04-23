package com.github.fmcejudo.redlogs.engine.card.writer;

import com.github.fmcejudo.redlogs.engine.card.model.CardQueryResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class CardResponseMongoWriter implements CardResponseWriter {

    private static final String COLLECTION = "card_report";

    private final MongoTemplate mongoTemplate;

    public CardResponseMongoWriter(final MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CardQueryResponse write(final CardQueryResponse cardQueryResponse) {
        Query query = query(where("id").is(cardQueryResponse.id()));
        CardQueryResponse previousQueryResponse = mongoTemplate.findOne(query, CardQueryResponse.class, COLLECTION);
        if (previousQueryResponse != null) {

            CardQueryResponse updatedResponse =
                    cardQueryResponse.addPreviousEntries(previousQueryResponse.currentEntries());
            return mongoTemplate.save(updatedResponse, COLLECTION);
        }
        return mongoTemplate.insert(cardQueryResponse, COLLECTION);
    }
}
