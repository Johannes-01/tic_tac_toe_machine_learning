# Aufgabe 2: Bestärkendes Lernen (Reinforcement Learning)

## Tic-Tac-Toe mit Reinforcement Learning

### Ziel der Aufgabe
Implementierung eines Tic-Tac-Toe-Spielers, der durch **Reinforcement Learning** lernt, optimal zu spielen.

---

## 1. Technische Anforderungen

### 1.1 Zu implementierende Schnittstellen
- **`ISpieler`** - Grundlegende Spielerschnittstelle
- **`ILernenderSpieler`** - Erweiterte Schnittstelle für lernende Spieler

### 1.2 Wichtige Methoden

#### Konstruktor
```java
public ReinforcementSpieler(String name, /* optionale Parameter */)
```
- Einziger zwingender Parameter: Name des Spielers
- Weitere Parameter optional möglich
- Spieler muss am Ende trainiert sein
- **Best Practice**: Spieler vorher trainieren und neuronales Netz abspeichern

#### neuesSpiel(farbe, bedenkzeit)
```java
public void neuesSpiel(Farbe farbe, int bedenkzeit)
```
- Spieler muss sich die Farbe merken
- Internes Spielfeld leeren
- Parameter `bedenkzeit` (in Sekunden) wird momentan noch nicht genutzt

#### berechneZug(vorherigerZug, zeitKreis, zeitKreuz)
```java
public Zug berechneZug(Zug vorherigerZug, long zeitKreis, long zeitKreuz)
```
- `vorherigerZug`: Letzter Zug des Gegners (null beim ersten Zug)
- Gegnerischen Zug im eigenen Spielfeld vermerken
- Eigenen Zug berechnen und zurückgeben
- Zeit-Parameter (in Millisekunden) enthalten momentan noch keine Werte

### 1.3 Spielablauf

1. **Spieler erzeugen** - Konstruktor aufrufen
2. **Spiel starten** - `neuesSpiel(farbe, bedenkzeit)` wird aufgerufen
3. **Züge berechnen** - `berechneZug(...)` wird wiederholt aufgerufen
4. **Spielende** - Wenn keine Züge mehr möglich oder Gewinner feststeht

### 1.4 Bereitgestellte Komponenten

- **`Wettkampf.java`** - Testumgebung zum Trainieren und Testen
- **`tic_tac_toe.jar`** - Bibliothek mit TicTacToe-Klasse und Schnittstellen
- **Zufallsspieler** - Beispielimplementierung als Gegner nutzbar
- **`ReinforcementSpielerLeer`** - Template (nicht funktionierend)

### 1.5 Wichtige Hinweise

- ⏱️ Zeitlimitierung muss noch NICHT berücksichtigt werden
- 🧠 Einsatz neuronaler Netze ist sinnvoll/erwünscht
- 💾 Trainiertes Modell sollte speicherbar sein
- 🎮 Spieler ist selbst für Spielfeld-Verwaltung verantwortlich

---

## 2. Abgabeanforderungen

### 2.1 Ausarbeitung (ca. 5-10 Seiten)

#### a) Problembeschreibung
- Beschreibung des Tic-Tac-Toe Problems
- **Grobe Abschätzung**: Wie viele unterschiedliche Spielsituationen sind möglich?

#### b) Theorieteil
- Allgemeine Einführung in Reinforcement Learning
- Fokussierung auf das Tic-Tac-Toe Problem
- **Neuronale Netze im RL-Kontext**:
  - Einsatz von neuronalen Netzen mit RL
  - Warum/Wann ist der Einsatz sinnvoll?

#### c) Lösungsbeschreibung
- Verständliche Beschreibung für Kommilitonen
- **Grafischer Überblick** (z.B. Klassendiagramm bei OOP)
- Erläuterung der Bestandteile des Codes, die für den Lernprozess relevant sind
- **Nachweis des Lernens**: 
  - Beispiel zeigen, dass das Programm tatsächlich lernt
  - Z.B. Anzahl der Siege gegen Gegner vor/nach Training

#### d) Fazit
- Überblick über die Lösung
- Festgestellte Probleme
- Mögliche Lösungen und Erweiterungen

**Wichtig**: Quellenangaben nicht vergessen! Auch wenn Quellen RL für Tic-Tac-Toe beschreiben, müssen sie angegeben werden.

### 2.2 Eclipse-Projekt
- Vollständiger Quelltext
- Kurze Anleitung zur Nutzung des Projekts

---

## 3. Implementierungsplan

### Phase 1: Grundstruktur (Vorbereitung)
- [ ] Projekt-Setup überprüfen
- [ ] Abhängigkeiten analysieren (`tic_tac_toe.jar`)
- [ ] Schnittstellen `ISpieler` und `ILernenderSpieler` verstehen
- [ ] Grundgerüst der `ReinforcementSpieler`-Klasse erstellen

### Phase 2: Spiellogik (Basis)
- [ ] Internes Spielfeld implementieren
- [ ] Spielzustand-Repräsentation definieren
- [ ] Methoden `neuesSpiel()` und `berechneZug()` implementieren (Basis)
- [ ] Validierung von Zügen implementieren
- [ ] Test gegen Zufallsspieler (ohne Training)

### Phase 3: Reinforcement Learning (Kern)
- [ ] **RL-Algorithmus wählen** (z.B. Q-Learning, SARSA, Deep Q-Learning)
- [ ] **State-Repräsentation** definieren (Spielfeld → Eingabe)
- [ ] **Action-Space** definieren (9 mögliche Züge)
- [ ] **Reward-Funktion** implementieren:
  - Sieg: +1.0
  - Niederlage: -1.0
  - Unentschieden: 0.0
  - Optional: Zwischenbelohnungen
- [ ] **Exploration vs. Exploitation** (ε-greedy Strategie)

### Phase 4: Neuronales Netz (optional aber empfohlen)
- [ ] Neuronales Netz-Architektur definieren
  - Eingabe: 9 Felder (Spielfeld-Zustand)
  - Ausgabe: Q-Werte für 9 Aktionen
- [ ] NN-Framework wählen (z.B. Deeplearning4j für Java)
- [ ] Netzwerk-Training implementieren
- [ ] Experience Replay (optional)

### Phase 5: Training
- [ ] Trainings-Loop implementieren
- [ ] Self-Play: Spieler gegen sich selbst
- [ ] Training gegen verschiedene Gegner:
  - Zufallsspieler
  - Perfekter Spieler (optional)
- [ ] Hyperparameter-Tuning:
  - Learning Rate
  - Discount Factor (γ)
  - Exploration Rate (ε)
- [ ] Trainingsfortschritt überwachen (Statistiken)

### Phase 6: Persistenz
- [ ] Modell speichern (neuronales Netz / Q-Tabelle)
- [ ] Modell laden im Konstruktor
- [ ] Trainingsmodus vs. Spielmodus unterscheiden

### Phase 7: Evaluierung
- [ ] Performance-Metriken implementieren:
  - Siegrate vor Training
  - Siegrate nach Training
  - Durchschnittliche Züge pro Spiel
- [ ] Wettkämpfe durchführen (1000+ Spiele)
- [ ] Ergebnisse dokumentieren

### Phase 8: Dokumentation
- [ ] Ausarbeitung schreiben:
  - Problembeschreibung
  - Theorie-Teil
  - Lösungsbeschreibung
  - Klassendiagramm erstellen
  - Lernnachweis mit Statistiken
  - Probleme & Erweiterungen
- [ ] Code kommentieren
- [ ] README mit Nutzungsanleitung erstellen

---

## 4. Geschätzte Spielsituationen

Zur Abschätzung der möglichen Spielsituationen:

- **Obere Grenze** (alle möglichen Belegungen): 3^9 = 19.683
  - Jedes der 9 Felder kann leer, X oder O sein
  
- **Realistische Anzahl** (gültige Spielzustände): ~5.478
  - Nach Eliminierung ungültiger Zustände
  - Symmetrien noch nicht berücksichtigt
  
- **Mit Symmetrien**: ~765 eindeutige Zustände
  - Berücksichtigung von Rotationen und Spiegelungen

➡️ **Fazit**: Tic-Tac-Toe ist klein genug für Tabular RL (Q-Table), aber neuronale Netze sind trotzdem sinnvoll als Lernübung und für Generalisierung.

---

## 5. RL-Algorithmen (Optionen)

### Option A: Q-Learning mit Tabelle
✅ **Vorteile**: Einfach, garantierte Konvergenz  
❌ **Nachteile**: Nicht skalierbar, keine Generalisierung

### Option B: Deep Q-Learning (DQN)
✅ **Vorteile**: Moderne Methode, Generalisierung, gute Lernübung  
❌ **Nachteile**: Komplexer, braucht NN-Framework

### Option C: Policy Gradient / Actor-Critic
✅ **Vorteile**: State-of-the-art  
❌ **Nachteile**: Sehr komplex für Tic-Tac-Toe

**Empfehlung**: Start mit Q-Learning + Tabelle, später auf DQN erweitern (falls Zeit)

---

## 6. Ressourcen & Bibliotheken

### Java RL/ML Frameworks
- **Deeplearning4j** - Deep Learning für Java
- **ND4J** - Numerische Operationen (NumPy für Java)
- **Burlap** - Java RL Library

### Alternative: Python → Java
- Python für Training (TensorFlow/PyTorch)
- Modell exportieren (ONNX)
- Java für Inferenz

---

## 7. Zeitplan (Vorschlag)

| Phase | Aufwand | Priorität |
|-------|---------|-----------|
| Grundstruktur | 2-4h | Hoch |
| Spiellogik | 4-6h | Hoch |
| RL-Kern | 8-12h | Hoch |
| Neuronales Netz | 6-10h | Mittel |
| Training | 4-8h | Hoch |
| Persistenz | 2-3h | Mittel |
| Evaluierung | 3-5h | Hoch |
| Dokumentation | 8-12h | Hoch |
| **Gesamt** | **37-60h** | - |

---

## 8. Erfolgs-Kriterien

✅ Spieler implementiert alle geforderten Schnittstellen  
✅ Spieler kann gegen Zufallsspieler spielen  
✅ Messbare Verbesserung durch Training nachweisbar  
✅ Modell kann gespeichert und geladen werden  
✅ Ausarbeitung vollständig und verständlich  
✅ Code gut strukturiert und kommentiert  
✅ Nutzungsanleitung vorhanden  

---

**Viel Erfolg bei der Implementierung! 🎮🤖**
