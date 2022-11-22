package io.vokumas.jitpayassignment.back.model.repository;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

public class RawJsonAggregationOperation implements AggregationOperation {

    private String rawJson;

    public RawJsonAggregationOperation(String rawJson) {
        this.rawJson = rawJson;
    }

    @Override
    public org.bson.Document toDocument(AggregationOperationContext aggregationOperationContext) {
        return aggregationOperationContext.getMappedObject(org.bson.Document.parse(rawJson));
    }
}
