# Redlog HTTP Endpoints

All endpoints are registered by `red-log-core` and are active by default (`redlog.enabled=true`).

---

## 1. Trigger Card Execution

**`GET /card-runner/{applicationName}`**

**Class:** `WebCardController` / `ReactiveCardController`
(`red-log-core/src/main/java/com/github/fmcejudo/redlogs/card/`)

Loads the card configuration for the given application, runs all queries against the configured backend (Loki, MongoDB, etc.), persists the results, and returns a link to the generated report.

```bash
curl "http://localhost:8080/card-runner/my-app?date=2026-07-01&env=prod"
```

```json
{
  "applicationName": "my-app",
  "executionId": "a1b2c3d4-...",
  "uri": "http://localhost:8080/report/execution/a1b2c3d4-.../doc",
  "params": { "date": "2026-07-01", "env": "prod" }
}
```

> Query parameters are optional and forwarded as the execution context (e.g. date ranges, environment tags).

---

## 2. List Executions

**`GET /execution/list/{applicationName}`**

**Class:** `WebExecutionController` / `ReactiveExecutionController`
(`red-log-core/src/main/java/com/github/fmcejudo/redlogs/execution/`)

Returns all stored executions for the given application, filtered by the provided query parameters. Each result includes hypermedia links to its JSON and document reports.

```bash
curl "http://localhost:8080/execution/list/my-app?date=2026-07-01"
```

```json
[
  {
    "application": "my-app",
    "executionId": "a1b2c3d4-...",
    "parameters": { "date": "2026-07-01" },
    "reportDate": "2026-07-01",
    "links": [
      { "rel": "json", "href": "http://localhost:8080/report/execution/a1b2c3d4-.../json" },
      { "rel": "doc",  "href": "http://localhost:8080/report/execution/a1b2c3d4-.../doc"  }
    ]
  }
]
```

---

## 3. Formatted Document Report

**`GET /report/execution/{executionId}/doc`**

**Class:** `ReportController`
(`red-log-core/src/main/java/com/github/fmcejudo/redlogs/report/ReportController.java`)

Returns the stored report rendered by the configured `DocumentFormat` bean (e.g. AsciiDoc/HTML when `red-log-formatter-asciidoc` is on the classpath).

> Only registered when a `DocumentFormat` bean is present in the application context.

```bash
curl "http://localhost:8080/report/execution/a1b2c3d4-.../doc"
```

---

## 4. Raw JSON Report

**`GET /report/execution/{executionId}/json`**

**Class:** `ReportController`
(`red-log-core/src/main/java/com/github/fmcejudo/redlogs/report/ReportController.java`)

Returns the raw `Report` domain object as JSON, useful for programmatic consumers.

> Only registered when a `DocumentFormat` bean is present in the application context.

```bash
curl "http://localhost:8080/report/execution/a1b2c3d4-.../json"
```

```json
{
  "applicationName": "my-app",
  "reportDate": "2026-07-01",
  "params": { "date": "2026-07-01" },
  "sections": [
    {
      "id": "card-id.a1b2c3d4-...",
      "reportId": "card-id",
      "description": "Error rate check",
      "items": [{ "labels": { "level": "ERROR" }, "count": 42 }],
      "createdAt": "2026-07-01T12:00:00"
    }
  ]
}
```

---

## Configuration

Base paths can be overridden in `application.properties`:

| Property                           | Default      | Affects        |
|------------------------------------|--------------|----------------|
| `redlog.card.controller-path`      | `card-runner`| Endpoint 1     |
| `redlog.execution.controller-path` | `execution`  | Endpoint 2     |
| `redlog.report.controller-path`    | `report`     | Endpoints 3, 4 |
| `redlog.enabled`                   | `true`       | All endpoints  |
