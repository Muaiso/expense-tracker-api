# Mitwirken

Danke fuer dein Interesse! Dieses Projekt ist eine Lern-Referenz, Beitraege sind willkommen.

## Voraussetzungen
- Java 25 (oder hoeher, via Gradle Toolchain)
- Gradle Wrapper (kein separater Gradle-Install noetig)

## Lokales Setup
```bash
./gradlew build      # baut und testet
./gradlew bootRun    # startet die API auf :8080
```

## Richtlinien
- Commits auf Englisch empfohlen, Beschreibungen praezise.
- Neue Logik braucht einen Test (Mindestens Unit- oder MockMvc-Test).
- `./gradlew build` muss lokal gruen sein, bevor ein PR erstellt wird.
- Code-Style ueber `.editorconfig` (4 Spaces Java, LF-Zeilenenden).
