# Q-Learning Tic-Tac-Toe

Ein **Reinforcement Learning Projekt**, das einen Q-Learning-basierten Tic-Tac-Toe Spieler implementiert.

## 📋 Übersicht

Dieses Projekt demonstriert, dass **Table-based Q-Learning** für kleine State-Spaces wie Tic-Tac-Toe die optimale Lösung ist. Der Agent erreicht eine Siegrate von **86.1%** gegen einen Zufallsspieler nach 50.000 Trainingsspielen.

| Metrik | Ergebnis |
|--------|----------|
| **Beste Siegrate** | 86.1% (vs. Zufallsspieler) |
| **Training-Speed** | 558.000+ Spiele/Sekunde |
| **Optimale Parameter** | α=0.30, γ=0.99, ε=0.40 |
| **States entdeckt** | ~5.600 (von ~6.046 möglichen) |

---

## 🚀 Voraussetzungen

- **Java 25** oder höher
- **Maven 3.6+**

---

## 🔧 Kompilieren

```bash
# Projekt kompilieren
mvn compile

# Tests ausführen
mvn test
```

---

## ▶️ Ausführbare Demos

Das Projekt enthält mehrere ausführbare Demo-Klassen. Alle können mit Maven oder direkt über die IDE gestartet werden.

### 1. **Interaktives Spiel** (Empfohlen für Einsteiger)
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.InteraktivesSpiel"
```
- 🎮 Interaktives Menü mit verschiedenen Spielmodi
- Q-Learning vs Zufallsspieler
- Neural Network vs Zufallsspieler
- Mensch vs Q-Learning
- Mensch vs Neural Network
- Q-Learning vs Neural Network

---

### 2. **Wettkampf** (Standard-Demo)
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.Wettkampf"
```
- Testet Q-Learning vor und nach dem Training
- Zeigt den Lernfortschritt

---

### 3. **Finale Demo** (Benchmark)
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.FinalesDemo"
```
- Vergleicht drei Konfigurationen:
  - **Optimal:** α=0.30, γ=0.99, ε=0.40
  - **Standard:** α=0.10, γ=0.90, ε=0.30
  - **Konservativ:** α=0.05, γ=0.95, ε=0.30
- Training mit 100.000 Spielen
- Test gegen 2.000 Zufallsspiele

---

### 4. **Parameter-Optimierung**
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.ParameterTest"
```
- Grid-Search über 64 Parameter-Kombinationen
- Exportiert Ergebnisse nach `results/parameter_test.csv`
- Findet optimale Hyperparameter

---

### 5. **JSON Export Demo**
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.JSONDemo"
```
- Demonstriert JSON Export/Import
- Vergleicht `.dat` vs `.json` Dateigrößen
- Zeigt human-readable Q-Table

---

### 6. **Neural Network Demo** ⭐ Vergleich beider Modelle
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.nn.NNDemo"
```
**Diese Demo vergleicht beide Modelle mit jeweils 100.000 Trainings-Episoden:**
- **Neural Network:** Self-Play Training mit Experience Replay (10.121 Parameter)
- **Q-Learning:** Table-based Training gegen Zufallsspieler (HashMap)
- Beide werden nach dem Training gegen 2.000 Zufallsspiele getestet
- Demonstriert, warum NN für Tic-Tac-Toe "Overkill" ist

| Ergebnis | Q-Learning | Neural Network |
|----------|------------|----------------|
| **Siegrate** | 88,0% | 79,0% |
| **Trainingszeit** | 0,22s | 153,32s |
| **Geschwindigkeit** | 458.715 Ep./s | 652 Ep./s |

---

### 7. **State Space Analyzer**
```bash
mvn exec:java -Dexec.mainClass="tic_tac_toe_mi.StateSpaceAnalyzer"
```
- Analysiert den theoretischen State Space
- Erklärt die ~6.046 möglichen Spielzustände

---

## 📁 Projektstruktur

```
tic_tac_toe_mi/
├── src/main/java/tic_tac_toe_mi/
│   ├── Spieler.java              # Q-Learning Spieler (Hauptklasse)
│   ├── QLearningAgent.java       # Q-Learning Logik
│   ├── SpielzustandAnalyzer.java # Win/Draw Detection
│   ├── SpielzustandKonverter.java# State Representation
│   ├── InteraktivesSpiel.java    # Interaktives Menü
│   ├── Wettkampf.java            # Standard-Wettkampf
│   ├── FinalesDemo.java          # Benchmark-Demo
│   ├── ParameterTest.java        # Grid-Search
│   ├── JSONDemo.java             # JSON Export/Import
│   └── nn/                       # Neural Network Package
│       ├── NeuralNetwork.java    # Pure Java NN
│       ├── NNDemo.java           # NN Benchmark
│       └── ...
├── models/                       # Gespeicherte Modelle
│   ├── optimal_config.json       # Beste Konfiguration
│   └── standard_config.json      # Standard Konfiguration
├── results/                      # Ergebnisse
│   ├── parameter_test.csv        # Parameter-Grid Ergebnisse
│   └── PARAMETER_OPTIMIZATION.md # Analyse
└── lib/
    └── tic_tac_toe.jar          # Tic-Tac-Toe Framework
```

---

## 📊 Q-Learning Parameter

| Parameter | Symbol | Optimal | Beschreibung |
|-----------|--------|---------|--------------|
| Lernrate | α | 0.30 | Wie schnell neue Erfahrungen alte überschreiben |
| Discount | γ | 0.99 | Wie wichtig zukünftige Belohnungen sind |
| Exploration | ε | 0.40 | Wie oft zufällig vs. optimal gespielt wird |

---

## 💾 Modelle speichern/laden

```java
// Spieler erstellen und trainieren
Spieler spieler = new Spieler("MeinSpieler", 0.30, 0.99, 0.40);
spieler.trainieren(new AbbruchNachIterationen(100000));

// Binär speichern (kompakt)
spieler.speichereModell("mein_modell.dat", 100000);

// JSON speichern (human-readable)
spieler.exportiereAlsJSON("mein_modell.json", 100000);

// Laden
Spieler geladenerSpieler = new Spieler("Geladen", "mein_modell.dat");
// oder
spieler.ladeVonJSON("mein_modell.json");
```

---

## 🧪 Tests ausführen

```bash
# Alle Tests
mvn test

# Einzelnen Test
mvn test -Dtest=AllTests
```

---

## 📚 Dokumentation

Ausführliche Dokumentation siehe:
- [FINALE_DOKUMENTATION.md`](DOKUMENTATION.md) - Vollständige Projektdokumentation
- [`results/PARAMETER_OPTIMIZATION.md`](results/PARAMETER_OPTIMIZATION.md) - Parameter-Analyse

---

## 👤 Autor

**Johannes Haick**

---

## 📄 Lizenz

Siehe [LICENSE](LICENSE)
