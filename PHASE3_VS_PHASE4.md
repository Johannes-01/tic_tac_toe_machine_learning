# Phase 3 vs. Phase 4: Q-Table vs. Neuronales Netz

## Die zentrale Frage: Wie speichern wir Q-Werte?

### Phase 3: Q-Learning mit Tabelle (AKTUELL IMPLEMENTIERT)

**Grundprinzip:**
- Wir speichern **jede einzelne Spielsituation** in einer Hash-Map
- Für jeden State (Spielfeld) speichern wir 9 Q-Werte (einen pro Feld)

**Beispiel:**
```java
// Q-Tabelle in QLearningAgent.java
Map<String, double[]> qTabelle = new HashMap<>();

// Speichern:
String state = "XO__X____";  // Spielfeld als String
double[] qWerte = {0.5, -0.2, 0.8, 0.0, 0.3, 0.0, 0.0, 0.0, 0.0};
qTabelle.put(state, qWerte);

// Abrufen:
double qWertFürFeld4 = qTabelle.get("XO__X____")[4];  // 0.3
```

**Was passiert:**
```
State: "XO__X____"  →  Q-Werte: [0.5, -0.2, 0.8, 0.0, 0.3, ...]
State: "XOX_X____"  →  Q-Werte: [0.2, 0.1, 0.9, 0.0, 0.7, ...]
State: "X___O____"  →  Q-Werte: [0.6, 0.0, 0.0, 0.0, 0.4, ...]
... (bis zu 5.478 States)
```

**Vorteile:**
- ✅ **Sehr einfach** - nur eine HashMap
- ✅ **Exakt** - jeder State wird individuell gelernt
- ✅ **Funktioniert perfekt für Tic-Tac-Toe** (nur ~5.000 States)

**Nachteile:**
- ❌ **Keine Generalisierung** - wenn wir State "XO__X____" nie gesehen haben, kennen wir keine Q-Werte dafür
- ❌ **Nicht skalierbar** - bei Schach (10^47 States) unmöglich
- ❌ **Speicherverbrauch** - bei großen Spielen zu viel Speicher

---

### Phase 4: Deep Q-Learning mit Neuronalem Netz (OPTIONAL)

**Grundprinzip:**
- Statt jede Situation einzeln zu speichern, trainieren wir ein **Neuronales Netz**
- Das Netz lernt eine **Funktion**: `Spielfeld → Q-Werte`
- Es kann Q-Werte für **nie gesehene** Situationen schätzen!

**Architektur:**
```
Input Layer:          Hidden Layer:      Output Layer:
(9 Neuronen)         (18 Neuronen)      (9 Neuronen)

[Feld 1]  →                              [Q(Feld 1)]
[Feld 2]  →          [Neuron 1]  →       [Q(Feld 2)]
[Feld 3]  →          [Neuron 2]  →       [Q(Feld 3)]
[Feld 4]  →          [...]        →       [...]
[...]      →         [Neuron 18] →       [Q(Feld 9)]
[Feld 9]  →
```

**Beispiel:**

**Situation 1 (Training):**
```
Input: [1, -1, 0, 0, 1, 0, 0, 0, 0]  (X=1, O=-1, Leer=0)
       → Netz berechnet → [0.5, -0.2, 0.8, 0.0, 0.3, 0.0, 0.0, 0.0, 0.0]
Target: [0.6, -0.2, 0.8, 0.0, 0.4, 0.0, 0.0, 0.0, 0.0]  (nach Q-Update)
       → Netz passt Gewichte an
```

**Situation 2 (NIE GESEHEN!):**
```
Input: [1, -1, 1, 0, 1, 0, 0, 0, 0]  (ähnlich zu Situation 1)
       → Netz berechnet → [0.52, -0.18, 0.75, 0.0, 0.32, ...]
```
👆 **Das ist der Unterschied!** Das Netz kann Q-Werte für neue Situationen **schätzen**!

**Was macht das Neuronale Netz?**

1. **Es lernt Muster:**
   - "Wenn ich 2 X in einer Reihe habe → hoher Q-Wert für das 3. Feld"
   - "Wenn Gegner 2 O in einer Reihe hat → hoher Q-Wert zum Blocken"

2. **Es generalisiert:**
   - Hat es gelernt, dass "XX_" gut ist...
   - ...weiß es auch, dass "_XX" gut ist (ähnliches Muster)

3. **Es komprimiert Wissen:**
   - Statt 5.000 States einzeln zu speichern...
   - ...speichert es Gewichte (z.B. 200 Werte), die alle States abdecken

**Code-Beispiel (hypothetisch für Phase 4):**
```java
// Statt HashMap:
// Map<String, double[]> qTabelle;

// Verwenden wir Neuronales Netz:
NeuralNetwork netz = new NeuralNetwork(9, 18, 9);  // Input, Hidden, Output

// Training:
double[] input = {1, -1, 0, 0, 1, 0, 0, 0, 0};     // Spielfeld
double[] output = netz.forward(input);              // Q-Werte berechnen
double[] target = {0.6, -0.2, 0.8, ...};           // Ziel-Q-Werte
netz.backward(target);                              // Gewichte anpassen

// Verwendung:
double[] qWerte = netz.forward(neuesSpielfeld);    // Funktioniert auch für nie gesehene Felder!
```

---

## Visueller Vergleich

### Phase 3: Q-Tabelle
```
┌─────────────────────────────────────────────────────┐
│  Q-Tabelle (HashMap)                                │
├─────────────────────────────────────────────────────┤
│  "XO__X____" → [0.5, -0.2, 0.8, 0.0, 0.3, ...]     │
│  "XOX_X____" → [0.2, 0.1, 0.9, 0.0, 0.7, ...]      │
│  "X___O____" → [0.6, 0.0, 0.0, 0.0, 0.4, ...]      │
│  ... (5.478 Einträge)                               │
└─────────────────────────────────────────────────────┘
         ↓
   LOOKUP (exakt)
         ↓
  "XO__X____" → [0.5, -0.2, 0.8, ...]
```

### Phase 4: Neuronales Netz
```
┌─────────────────────────────────────────────────────┐
│  Neuronales Netz (Gewichte)                         │
├─────────────────────────────────────────────────────┤
│  Gewicht[0][0] = 0.23                               │
│  Gewicht[0][1] = -0.45                              │
│  Gewicht[1][0] = 0.67                               │
│  ... (200 Gewichte)                                 │
└─────────────────────────────────────────────────────┘
         ↓
   BERECHNUNG (Generalisierung)
         ↓
  [1, -1, 0, 0, 1, ...] → [0.5, -0.2, 0.8, ...]
  
  Kann auch neue States berechnen:
  [1, -1, 1, 0, 1, ...] → [0.52, -0.18, 0.75, ...]
           ↑
    (nie gesehen!)
```

---

## Wann ist was sinnvoll?

### Q-Tabelle (Phase 3) ist gut für:
- ✅ **Kleine State-Spaces** (< 100.000 States)
- ✅ **Tic-Tac-Toe** (5.478 States)
- ✅ **Einfachheit** (schnell implementiert)
- ✅ **Garantierte Konvergenz** (findet optimale Lösung)

### Neuronales Netz (Phase 4) ist gut für:
- ✅ **Große State-Spaces** (Millionen/Milliarden States)
- ✅ **Schach, Go, Atari-Spiele**
- ✅ **Generalisierung** (lernt Muster, nicht einzelne Situationen)
- ✅ **Unendliche State-Spaces** (kontinuierliche Werte)

---

## Für Tic-Tac-Toe: Unsere Wahl

**Entscheidung: Phase 3 (Q-Tabelle) ist völlig ausreichend!**

**Warum?**
1. Nur 5.478 States → HashMap ist perfekt
2. Schneller zu implementieren
3. Garantiert optimal (keine Approximationsfehler)
4. Einfacher zu debuggen

**Neuronales Netz wäre "Overkill":**
- Wie mit einem Panzer auf eine Fliege schießen 🎯
- Mehr Code, mehr Komplexität, mehr Bugs
- Nicht schneller, nicht besser für dieses Problem

**ABER:** Phase 4 ist trotzdem interessant als **Lernübung**:
- Du lernst moderne RL-Methoden
- Vorbereitung für komplexere Spiele
- Gut für die Ausarbeitung (zeigt tieferes Verständnis)

---

## Zusammenfassung

| Aspekt | Phase 3 (Q-Tabelle) | Phase 4 (Neuronales Netz) |
|--------|---------------------|---------------------------|
| **Speicherung** | HashMap mit States | Gewichte im Netz |
| **Generalisierung** | Nein | Ja ✅ |
| **Für Tic-Tac-Toe** | Perfekt ✅ | Overkill |
| **Für Schach** | Unmöglich | Notwendig ✅ |
| **Komplexität** | Einfach | Komplex |
| **Framework nötig** | Nein | Ja (Deeplearning4j) |
| **Unser Status** | ✅ Implementiert | ⏭️ Optional |

---

## Nächste Schritte

**Empfehlung:** 
1. **Phase 5** zuerst implementieren (Training mit `trainieren()` Methode)
2. **Phase 6** danach (Model Persistence - Q-Tabelle speichern/laden)
3. **Phase 4** nur wenn Zeit übrig ist und du Deep Learning lernen willst

**Phase 4 überspringen ist OK!** Die Aufgabe sagt "sinnvoll/erwünscht" aber nicht "verpflichtend".

---

*Datum: 15. November 2025*  
*Status: Erklärung - Phase 3 ist ausreichend für die Aufgabe!*
