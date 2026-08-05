# Expense Tracker API

Schlanke **Spring-Boot-REST-API** zum Verwalten und Auswerten von Ausgaben.
Erstellt als Beispielprojekt für Backend-Grundlagen (Layered Architecture, JPA, Tests).

## Features

- Vollständiges CRUD über `POST / GET / PUT / DELETE /api/expenses`
- Aggregation: `GET /api/expenses/summary` liefert Gesamtsumme, Anzahl und Summe pro Kategorie
- Validierung der Eingaben (`@NotNull`, `@NotBlank`, `@DecimalMin`) → `400` bei ungültigem Payload
- Globale Fehlerbehandlung → sauberes `404`-JSON statt Stacktrace
- In-memory **H2**-Datenbank (kein Setup nötig) + H2-Console unter `/h2-console`
- Geldbeträge als `BigDecimal` (kein Fließkomma-Rundungsfehler)

## Tech-Stack

| Bereich      | Technologie                     |
|--------------|---------------------------------|
| Sprache      | Java 25 (Gradle Toolchain)      |
| Framework    | Spring Boot 3.5 (Web, Data JPA) |
| Persistenz   | JPA / Hibernate, H2 (in-memory) |
| Validierung  | Jakarta Validation              |
| Tests        | JUnit 5, Mockito, MockMvc       |
| Build        | Gradle 9.4 (Wrapper enthalten)  |

## Architektur (Layered)

```
controller/   ExpenseController       -> HTTP, Mapping, Status-Codes
service/      ExpenseService          -> Geschäftslogik, Aggregation
repository/   ExpenseRepository       -> JPA-Datenzugriff
model/        Expense (Entity)        ->持久ierte Domäne
dto/          ExpenseSummary (record) -> nur-lesbares Aggregat
```

## Projekt bauen & starten

```bash
./gradlew build          # kompiliert + führt alle Tests aus
./gradlew bootRun        # startet die API auf http://localhost:8080
```

Nach dem Start sind die Endpunkte erreichbar:

| Methode | Pfad                    | Beschreibung                |
|---------|-------------------------|-----------------------------|
| GET     | `/api/expenses`         | alle Ausgaben               |
| GET     | `/api/expenses/{id}`    | eine Ausgabe (404 falls weg)|
| POST    | `/api/expenses`         | neue Ausgabe anlegen (201)  |
| PUT     | `/api/expenses/{id}`    | Ausgabe aktualisieren       |
| DELETE  | `/api/expenses/{id}`    | Ausgabe löschen (204)       |
| GET     | `/api/expenses/summary` | Aggregation (siehe oben)    |

H2-Console: `http://localhost:8080/h2-console` (JDBC-URL `jdbc:h2:mem:expensesdb`).

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

Abgedeckt: Service-Logik (Mockito), Controller-Verhalten inkl. Validierung & 404 (MockMvc) — aktuell 10 Tests.

## Lizenz

MIT — siehe [LICENSE](LICENSE).
