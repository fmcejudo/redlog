# Contributing to redlog

## Branch Model

This project maintains two active long-lived branches, one per supported Spring Boot generation:

| Branch | Spring Boot | Version line | Maven coordinates |
|---|---|---|---|
| `main` | 4.x | `1.x.x` | `io.github.fmcejudo.redlogs:red-log-api:1.x.x` |
| `maintenance/redlog_0x` | 3.x | `0.x.x` | `io.github.fmcejudo.redlogs:red-log-api:0.x.x` |

## Maven coordinates for consumers

**Spring Boot 4:**
```xml
<dependency>
    <groupId>io.github.fmcejudo.redlogs</groupId>
    <artifactId>red-log-api</artifactId>
    <version>1.x.x</version>
</dependency>
```

**Spring Boot 3:**
```xml
<dependency>
    <groupId>io.github.fmcejudo.redlogs</groupId>
    <artifactId>red-log-api</artifactId>
    <version>0.x.x</version>
</dependency>
```

## Contribution Workflows

### New features

1. Branch off `main`
2. Develop and test against Spring Boot 4
3. Open a PR targeting `main`

### Bug fixes applicable to both Spring Boot versions

1. Branch off `main`, fix there first
2. Open a PR targeting `main`
3. Once merged, cherry-pick the commit(s) to `maintenance/redlog_0x`:
   ```bash
   git checkout maintenance/redlog_0x
   git cherry-pick <commit-sha>
   git push origin maintenance/redlog_0x
   ```

### Spring Boot 3-only fixes

1. Branch off `maintenance/redlog_0x`
2. Open a PR targeting `maintenance/redlog_0x`

## Prerequisites

- Java 21 (see `.sdkmanrc`)
- Docker (required for integration tests via Testcontainers)

## Build commands

```bash
./mvnw -B package        # build + test all modules
./mvnw test              # tests only
./mvnw -pl red-log-core test   # test a single module
```
