package io.github.fmcejudo.redlogs.loki.processor;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.util.UriUtils;

final class LokiLinkBuilder {

  private final String lokiExploreUrl;

  private String query;

  private LocalDateTime from;

  private LocalDateTime to;

  private final String dataSource;

  private LokiLinkBuilder(final String lokiUrl, final String dataSource) {
    this.lokiExploreUrl = String.join("/", lokiUrl, "explore");
    this.dataSource = dataSource;
  }

  public static LokiLinkBuilder builder(final String lokiUrl, final String dataSource) {
    return new LokiLinkBuilder(lokiUrl, dataSource);
  }

  public LokiLinkBuilder from(LocalDateTime from) {
    this.from = from;
    return this;
  }

  public LokiLinkBuilder query(String query) {
    this.query = query;
    return this;
  }

  public LokiLinkBuilder to(LocalDateTime to) {
    this.to = to;
    return this;
  }

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public String build() {
    PanesParam panesParam =
        new PanesParam("tm7", new LeftPart(dataSource, List.of(QueryPart.create("A", query, dataSource)), new RangePart(from, to)));
    String panesSection = adjustEncoding(panesParam.toString());

    String queryParams = """
        schemaVersion=1&panes=%s&orgId=1""".formatted(panesSection);

    return lokiExploreUrl + "?" + queryParams;
  }

  private String adjustEncoding(String queryParam) {
    return queryParam
        .replace("\n", "%0A")
        .replace("\r", "%0D")
        .replace("=", "%3D")
        .replace("|", "%7C")
        .replace("&", "%26")
        .replace("\\", "%5C")
        .replace("\"","%22")
        .replace(" ", "%20")
        .replace("{", "%7B")
        .replace("}", "%7D")
        .replace("[", "%5B")
        .replace("]", "%5D")
        .replace("(", "%28")
        .replace(")", "%29")
        .replace("*", "%2A")
        .replace("?", "%3F")
        .replace("`", "%60")
        .replace("'", "%27")
        .replace("+", "%2B")
        .replace("<", "%3C")
        .replace(">", "%3E");
  }

  record PanesParam(String key, LeftPart leftPart) {

    @Override
    public String toString() {
      try {
        return MAPPER.writeValueAsString(Map.of(key, MAPPER.readTree(leftPart.toString())));
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialize PanesParam", e);
      }
    }
  }

  record LeftPart(String datasource, List<QueryPart> queries, RangePart range) {

    @Override
    public String toString() {
      try {
        ArrayNode queriesNode = MAPPER.createArrayNode();
        for (QueryPart q : queries) {
          queriesNode.add(MAPPER.readTree(q.toString()));
        }
        ObjectNode node = MAPPER.createObjectNode();
        node.put("datasource", datasource);
        node.set("queries", queriesNode);
        node.set("range", MAPPER.readTree(range.toString()));
        node.put("compact", false);
        return MAPPER.writeValueAsString(node);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialize LeftPart", e);
      }
    }
  }

  record QueryPart(String refId, String expr, String datasource) {

    static QueryPart create(String refId, String expr, String datasource) {
      return new QueryPart(refId, expr, datasource);
    }

    @Override
    public String toString() {
      try {
        ObjectNode datasourceNode = MAPPER.createObjectNode();
        datasourceNode.put("type", "loki");
        datasourceNode.put("uid", datasource);

        ObjectNode node = MAPPER.createObjectNode();
        node.put("refId", refId);
        node.put("expr", expr);
        node.put("queryType", "range");
        node.set("datasource", datasourceNode);
        node.put("editorMode", "code");
        node.put("direction", "backward");
        return MAPPER.writeValueAsString(node);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialize QueryPart", e);
      }
    }
  }

  record RangePart(LocalDateTime from, LocalDateTime to) {

    @Override
    public String toString() {
      try {
        ObjectNode node = MAPPER.createObjectNode();
        if (from == null || to == null) {
          node.put("from", "now-1d");
          node.put("to", "now");
        } else {
          node.put("from", from.toInstant(UTC).toEpochMilli());
          node.put("to", to.toInstant(UTC).toEpochMilli());
        }
        return MAPPER.writeValueAsString(node);
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Failed to serialize RangePart", e);
      }
    }
  }
}
