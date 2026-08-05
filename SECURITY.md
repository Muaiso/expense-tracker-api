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
  und sollte in einer echten Deployment-Umgebung deaktiviert werden.
