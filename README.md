#YourInternetBF

Authors:
- Eric Goerens
- Michel Heimes
- Jonas Anetzberger
- Hasret Timocin


## Userstories
### REST API
- [ ] Der Player kann sich einloggen und kann Robots kaufen.
- [ ] Der Player stellt den Modus von einem Robot ein.
- [ ] Kaufe einen Roboter in der nächsten Runde.
- [ ] Gebe Zustand eines Roboter aus.
- [ ] Gebe Zustand aller Robots aus.

### Domain-layer
- [ ] Der Modus "Go Home".
- [ ] Der Modus "Serendipity".
- [ ] Gebe eine Übersicht aller bekannten Planeten mit den Positionen der eigenen Robots aus.
- [ ] Gebe den Zustand eines Roboter
- [ ] Gebe den Zustand aller Roboter
- [ ] "Kaufe einen Roboter in der nächsten Runde" - Befehl

---

### Minimalanforderungen an den Player
#### Playermodis
- [ ] Der Player kann Robots kaufen und jeden von ihnen eigenständig im “Serendipity”-Modus (läuft rum und versucht, neue Planeten zu finden) umherlaufen lassen.
- [ ] Alternativ kann man einem Robot befehlen, eigenständig “nach Hause” zu gehen (gehe zu einer Space-Station)

#### Steuerungs-REST-API
Es gibt ein Steuerungs-REST-API, das die folgenden Kommandos an den eigenen Player erlaubt:
- [ ] Kaufe Robot in der nächsten Runde
- [ ] Gebe Zustand eines Robots aus
- [ ] Gebe Zustand aller Robots aus
- [ ] Versetze einen bestimmten Robot in den Modus “Serendipity”
- [ ] Versetze einen bestimmten Robot in den Modus “Go Home”
- [ ] Gebe eine Übersicht aller bekannten Planeten mit den Positionen der eigenen Robots aus
