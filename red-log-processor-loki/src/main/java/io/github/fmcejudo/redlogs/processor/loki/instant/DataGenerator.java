package io.github.fmcejudo.redlogs.processor.loki.instant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@FunctionalInterface
interface DataGenerator {

    Data generate();

    static DataGenerator withResultType(final ResultType resultType) {
        return () -> new Data(resultType.getType(), List.of());
    }

    public default DataGenerator addVectorResult(final Map<String, String> label, final List<VectorValue> values) {
        return () -> {
            Data data = this.generate();
            if (data.resultType().equals("stream")) {
                throw new IllegalStateException("it not possible to add a new vector result to a stream type");
            }

            Result result = new VectorResult(label, values);
            if (data.result() != null) {
                List<Result> results = new ArrayList<>(data.result());
                results.add(result);
                return new Data(data.resultType(), Collections.unmodifiableList(results));
            }
            return new Data(data.resultType(), List.of(result));
        };
    }

    public default DataGenerator addStreamsResult(final Map<String, String> label, final List<StreamsValue> values) {
        return () -> {
            Data data = this.generate();

            if (data.resultType().equals("vector")) {
                throw new IllegalStateException("it not possible to add a new stream result to a vector type");
            }
            Result result = new StreamsResult(label, values);
            if (data.result() != null) {
                List<Result> results = new ArrayList<>(data.result());
                results.add(result);
                return new Data(data.resultType(), Collections.unmodifiableList(results));
            }
            return new Data(data.resultType(), List.of(result));
        };
    }


    public enum ResultType {
        STREAM("stream"), VECTOR("vector");

        private String type;

        ResultType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
