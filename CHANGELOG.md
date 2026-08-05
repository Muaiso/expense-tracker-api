# Changelog

Alle wesentlichen Aenderungen an diesem Projekt werden in dieser Datei dokumentiert.
Das Format lehnt sich an [Keep a Changelog](https://keepachangelog.com/).

## [Unreleased]

### Hinzugefügt
- CRUD-Endpunkte fuer Ausgaben (`/api/expenses`)
- Aggregations-Endpoint `/api/expenses/summary` (Gesamt, pro Kategorie, Anzahl)
- Eingabevalidierung mit Jakarta Validation
- Globale Fehlerbehandlung mit JSON-Antwort
- JUnit/Mockito/MockMvc-Tests (10)
- H2-In-Memory-Datenbank mit H2-Console
