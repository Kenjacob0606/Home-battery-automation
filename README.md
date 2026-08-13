# Reactive Home Automation System (ENG2 Assessment)

A reactive, event-driven home automation platform built around a battery-management scenario: the system pulls Octopus Agile import/export electricity rates, decides when to charge or discharge a home battery based on price, and drives that decision out to IoT actuators. The system is specified with a custom DSL and implemented as a set of Kafka-connected Micronaut microservices.

## Overview

The scenario models a smart home battery that reacts to variable energy prices:

1. **AgileRatesFetcher** pulls the latest Octopus Agile rates on a schedule.
2. **ImportRateExtractor** / **ExportRateExtractor** derive the current import and export rate from that data.
3. **BatteryDecision** consumes both rates and decides whether the battery should charge, discharge, or hold.
4. **BatteryActuatorController** applies that decision to the physical (simulated) battery actuator.

This reactive pipeline is formally specified as a component/topic model in a custom textual DSL (**RCL** – Reactive Component Language), then implemented as independent microservices that communicate over Kafka topics.

## Repository Structure

```
.
├── language/           # Xtext-based RCL DSL: grammar, editor, validation (EVL), code generator
├── model/               # RCL model instance describing the home automation component graph
├── microservices/
│   ├── iot/              # Manages rooms, sensors, and actuators (REST API + DB)
│   ├── rates/             # Fetches and serves Octopus Agile import/export rates
│   ├── reactive/           # Reactive components: rate extraction, battery decision, actuator control
│   └── end2end-tests/      # Integration tests exercising the full running system
└── microservices/compose.yml  # Docker Compose stack: MariaDB, Kafka (3-node), and the services above
```

### The RCL language (`language/`)

An Eclipse/Xtext project defining the Reactive Component Language used to model this kind of system declaratively:

- `uk.ac.york.cs.eng2.rcl` – core grammar/metamodel
- `uk.ac.york.cs.eng2.rcl.editor` / `.edit` – editing support
- `uk.ac.york.cs.eng2.rcl.evl` – Epsilon Validation Language constraints
- `uk.ac.york.cs.eng2.rcl.generator` / `.generator.dt` – code/artifact generation
- `uk.ac.york.cs.eng2.rcl.viewpoint` – Sirius diagram viewpoint

### The model (`model/`)

`homeControl.rcl` defines the concrete component graph for this scenario: components (`AgileRatesFetcher`, `ImportRateExtractor`, `ExportRateExtractor`, `BatteryDecision`, `BatteryActuatorController`), their triggers (time-based and topic-based), and the Kafka topics connecting them (`agile_rates`, `current_import_rate`, `current_export_rate`, `battery_decision`).

### The microservices (`microservices/`)

Built with [Micronaut](https://micronaut.io/) (Java 17), each service is independently buildable and containerizable via the Micronaut Gradle plugin:

- **`iot`** – REST API for rooms, sensors, actuators, and sensor readings, backed by MariaDB.
- **`rates`** – A local fake/mock rates API. It stands in for the real upstream data source when that's unavailable (rate-limited, offline, etc.).
- **`reactive`** – Implements the reactive components from the RCL model (rate extraction, battery decision logic, actuator control), consuming/producing Kafka events and calling the `iot` service.
- **`end2end-tests`** – Black-box integration tests that exercise the full composed stack.

## Rates Data Source

By default, the `reactive` service fetches live Octopus Agile import/export rates from [agilerates.uk](https://agilerates.uk) (a third-party API publishing Octopus's calculated Agile rates per UK region), configured in `microservices/reactive/src/main/resources/application.properties`:

```properties
micronaut.http.services.rates.url=https://agilerates.uk
micronaut.http.services.rates.path=/api/agile_rates_region_M.json
```

If that upstream API is unavailable, comment out the two lines above and uncomment the alternative block in the same file to point at the local `rates` microservice (the fake/mock rates API included in this repo) instead:

```properties
#micronaut.http.services.rates.url=http://localhost:8082
#micronaut.http.services.rates.url=http://rates:8082
#micronaut.http.services.rates.path=/
```

## Prerequisites

- JDK 17+
- Docker (Engine or Desktop) with Compose support
- An internet connection for Gradle/Docker image downloads on first run

> **Docker Engine 29+ note:** if you see `Could not find a valid Docker environment` when running tests, see the [Testcontainers/Micronaut issue](https://github.com/micronaut-projects/micronaut-test-resources/issues/941). Workaround — from Bash:
> ```shell
> echo api.version=1.44 > $HOME/.docker-java.properties
> ```
> From PowerShell:
> ```shell
> "api.version=1.44" | set-content $HOME/.docker-java.properties -Encoding Ascii
> ```
> If Docker was already failing, reset the cached failure with the `stopTestResourcesService` Gradle task.

## Building and Running

Each microservice can be built and containerized individually from its own directory:

```shell
cd microservices/<service>
./gradlew build dockerBuild
```

To build every service, bring up the full stack (MariaDB, Kafka, `iot`, `reactive`), and run the end-to-end test suite in one go:

```shell
cd microservices
./run-tests.sh
```

To just start the stack without running tests:

```shell
cd microservices
docker compose up --wait
```

Once running:
- `iot` is available at `http://localhost:8080`
- `reactive` is available at `http://localhost:8081`

## Testing

- Unit/component tests live under each service's `src/test`.
- End-to-end tests (`microservices/end2end-tests`) spin up the composed stack and verify full workflows, such as the battery charge/discharge decision pipeline (`BatteryWorkFlowTest`).

Run a single service's tests with:

```shell
cd microservices/<service>
./gradlew test
```

## Continuous Integration

A GitHub Actions workflow (`.github/workflows/gradle.yml`) runs the Gradle build for Part 1 of the assessment. Some steps are commented out until the microservices and end-to-end tests are sufficiently complete — uncomment them as the implementation progresses.

## Tech Stack

- **Language/Modeling:** Xtext, Epsilon (EVL), Sirius
- **Services:** Java 17, Micronaut 4.x, Gradle (with Shadow and GraalVM Native plugins)
- **Messaging:** Apache Kafka (3-broker KRaft cluster)
- **Persistence:** MariaDB
- **Testing:** JUnit 5, Testcontainers
- **Orchestration:** Docker Compose
