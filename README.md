# Expense Tracker API

Schlanke **Spring-Boot-REST-API** zum Verwalten und Auswerten von Ausgaben.
Erstellt als Beispielprojekt fuer Backend-Grundlagen (Layered Architecture,
JPA, Tests, Docs, Container).

## Features

- Vollstaendiges CRUD ueber `POST / GET / PUT / DELETE /api/expenses`
- **Filter**: nach Kategorie (`?category=`) und Zeitraum (`?from=&to=`)
- **Pagination**: `?page=&size=` mit `PageResponse`-Envelope
- **Sortierung**: `?sort=ASC|DESC` (nach Datum)
- **Aggregation**: `GET /api/expenses/summary` (Gesamt, pro Kategorie, Anzahl)
- **Statistik**: `GET /api/stats/monthly` (Ausgaben pro Kalendermonat)
- **Validierung** der Eingaben (`@NotNull`, `@NotBlank`, `@DecimalMin`) -> `400`
- **Globale Fehlerbehandlung** -> sauberes `404`/Fehler-JSON
- **OpenAPI/Swagger UI** unter `/swagger-ui.html`
- **Actuator** Health/Info/Metrics unter `/actuator/*`
- **CORS** fuer ein lokales Frontend (Port 3000)
- **H2** In-Memory-DB + H2-Console (`/h2-console`)
- Geldbetraege als `BigDecimal` (kein Fließkomma-Rundungsfehler)

## Tech-Stack

| Bereich      | Technologie                          |
|--------------|--------------------------------------|
| Sprache      | Java 25 (Gradle Toolchain)           |
| Framework    | Spring Boot 3.5 (Web, Data JPA)      |
| Persistenz   | JPA / Hibernate, H2 (in-memory)      |
| Validierung  | Jakarta Validation                   |
| Docs         | springdoc-openapi (Swagger UI)       |
| Monitoring   | Spring Boot Actuator                 |
| Tests        | JUnit 5, Mockito, MockMvc, TestRestTemplate |
| Build        | Gradle 9.4 (Wrapper enthalten)       |
| Container    | Docker / docker-compose              |

## Architektur (Layered)

```
controller/      *Controller        -> HTTP, Mapping, Status-Codes, DTOs
service/         *Service           -> Geschaeftslogik, Aggregation
repository/      ExpenseRepository  -> JPA-Datenzugriff (Pageable)
model/           Expense (Entity)   -> persistierte Domäne
dto/             Request/Response, PageResponse, MonthlyStat (records)
config/          CorsConfig         -> CORS-Konfiguration
```

## Projekt bauen & starten

```bash
./gradlew build          # kompiliert + fuehrt alle Tests aus
./gradlew bootRun        # startet die API auf http://localhost:8080
```

Nach dem Start sind die Endpunkte erreichbar:

| Methode | Pfad                          | Beschreibung                          |
|---------|-------------------------------|---------------------------------------|
| GET     | `/api/expenses`               | Liste (Filter/Pagination/Sort)        |
| GET     | `/api/expenses/{id}`          | eine Ausgabe (404 falls weg)          |
| POST    | `/api/expenses`               | neue Ausgabe anlegen (201)            |
| PUT     | `/api/expenses/{id}`          | Ausgabe aktualisieren                 |
| DELETE  | `/api/expenses/{id}`          | Ausgabe loeschen (204)                |
| GET     | `/api/expenses/summary`       | Aggregation (Gesamt, pro Kategorie)  |
| GET     | `/api/stats/monthly`          | Ausgaben pro Kalendermonat            |
| GET     | `/api/stats/current-month`    | Summe des laufenden Monats            |

H2-Console: `http://localhost:8080/h2-console` (JDBC-URL `jdbc:h2:mem:expensesdb`).
Swagger UI: `http://localhost:8080/swagger-ui.html`.

## Beispiel-Request

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{"description":"Lunch","amount":10.50,"category":"Food","date":"2026-08-05"}'
```

## Tests

```bash
./gradlew test
```

Abgedeckt: Service-Logik (Mockito), Controller-Verhalten inkl. Validierung
& 404 (MockMvc), vollstaendiger CRUD/Summary-Durchlauf ueber HTTP
(`@SpringBootTest` + `TestRestTemplate`), Actuator-Health, Statistik-Aggregation.

## Container

```bash
docker compose up --build      # baut Image und startet Container auf :8080
```

## Lizenz

MIT — siehe [LICENSE](LICENSE).
