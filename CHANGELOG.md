# Changelog

Alle wesentlichen Aenderungen an diesem Projekt werden in dieser Datei dokumentiert.
Das Format lehnt sich an [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Hinzugefügt
- CRUD-Endpunkte fuer Ausgaben (`/api/expenses`)
- Aggregations-Endpoint `/api/expenses/summary` (Gesamt, pro Kategorie, Anzahl)
- Eingabevalidierung mit Jakarta Validation
- Globale Fehlerbehandlung mit JSON-Antwort
- DTO-Schicht (ExpenseRequest/Response) zur Entkopplung von der JPA-Entity
- OpenAPI/Swagger UI (`/swagger-ui.html`)
- Actuator Health/Info/Metrics (`/actuator/*`)
- Kategorie- und Zeitraum-Filter auf der Liste
- Pagination (`page`/`size`) mit `PageResponse`-Envelope
- Sortierung nach Datum (`sort=ASC|DESC`)
- Statistik-Endpunkte (`/api/stats/monthly`, `/api/stats/current-month`)
- CORS-Konfiguration fuer lokales Frontend (Port 3000)
- Dockerfile (Multi-Stage) + docker-compose
- GitHub Actions CI-Workflow
- Tests: Unit (Mockito), MockMvc, Integration (`@SpringBootTest`), Actuator, Statistik
