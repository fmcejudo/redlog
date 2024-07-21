package com.github.fmcejudo.redlogs.client.loki.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@FunctionalInterface
interface DataGenerator {

    public abstract Data generate();

    public static DataGenerator resultType(DataType dataType) {
        return () -> new Data(dataType.getType(), List.of());
    }

    public default DataGenerator addMatrixResult(final Map<String, String> label, final List<MatrixValue> values) {
        return () -> {
            Data data = this.generate();
            if (data.resultType().equals("streams")) {
                throw new IllegalStateException("it not possible to add a new matrix result to a streams type");
            }

            Result result = new MatrixResult(label, values);
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

            if (data.resultType().equals("matrix")) {
                throw new IllegalStateException("it not possible to add a new streams result to a matrix type");
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

    enum DataType {
        MATRIX("matrix"), STREAMS("streams");

        public String type;

        DataType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
