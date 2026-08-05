# Sicherheitsrichtlinie

## Unterstützte Versionen
Nur die aktuellste `0.x`-Version wird gepflegt.

## Melden von Sicherheitslücken
Bitte keine oeffentlichen Issues fuer Sicherheitsluecken.
Kontakt ueber ein privates GitHub-Issue oder Direktnachricht an den Maintainer.

## Hinweise
- Die Anwendung nutzt eine In-Memory-H2-Datenbank und ist fuer den
  lokalen Gebrauch / Lernzwecke gedacht, nicht als Produktivsystem.
- Die H2-Console ist standardmaessig aktiv (`spring.h2.console.enabled=true`)
  und sollte in einer echten Deployment-Umgebung deaktiviert werden
  (z.B. ueber `spring.h2.console.enabled=false` in `application-prod.properties`).
- CORS ist auf `http://localhost:3000` beschraenkt; fuer andere Frontends
  die erlaubten Origins in `CorsConfig` anpassen.
- Actuator-Endpunkte (`/actuator/*`) sind im Default nur mit `health`,
  `info` und `metrics` exponiert. Keine sensiblen Endpunkte freigeben.
