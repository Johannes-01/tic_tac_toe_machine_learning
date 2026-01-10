# 3. Aufgabe: Bestärkendes Lernen (Tic-Tac-Toe)

---

## 3.1 Problembeschreibung und Komplexität

Tic-Tac-Toe ist ein klassisches Zwei-Personen-Strategiespiel auf einem $3 \times 3$ Spielfeld. Zwei Spieler setzen abwechselnd ihre Symbole (X und O), wobei X stets beginnt. Gewonnen hat, wer zuerst drei gleiche Symbole in einer Reihe platziert – horizontal, vertikal oder diagonal. Sind alle neun Felder belegt ohne Sieger, endet das Spiel unentschieden.

```
    Spielfeld            Gewinnlinien (8 mögliche)
                    
   0 │ 1 │ 2            ═══════════  ─────────── ───────────
  ───┼───┼───             Zeile 0      Zeile 1     Zeile 2
   3 │ 4 │ 5            
  ───┼───┼───              ║            ║           ║
   6 │ 7 │ 8            Spalte 0    Spalte 1    Spalte 2
                    
                           ╲            ╱
                        Diagonale   Antidiagonale
```

Das Spiel ist ein **deterministisches Nullsummenspiel** mit **perfekter Information**: Der Gewinn des einen Spielers entspricht exakt dem Verlust des anderen, und beide kennen jederzeit den vollständigen Spielzustand.

Die Komplexität des Spiels lässt sich aus verschiedenen Perspektiven betrachten. Die naive obere Grenze ergibt sich aus $3^9 = 19.683$ möglichen Feldbelegungen, da jedes der neun Felder drei Zustände annehmen kann (leer, X oder O). Nach Eliminierung ungültiger Zustände – etwa wenn mehr O als X auf dem Feld stehen oder nach einem bereits entschiedenen Spiel weiter gezogen wird – verbleiben exakt **6.046 gültige Brettzustände**. Berücksichtigt man zusätzlich die acht Symmetrien des Spielfelds (vier Rotationen und zwei Spiegelungen), reduziert sich dieser Wert auf 765 kanonische Positionen. Die Anzahl aller möglichen Spielverläufe vom Start bis zum Ende beträgt 255.168.

```
State Space Hierarchie:

┌─────────────────────────────────────────────────────────────────┐
│                    19.683 (alle Kombinationen)                   │
│    ┌───────────────────────────────────────────────────────┐    │
│    │              6.046 gültige Brettzustände               │    │
│    │    ┌─────────────────────────────────────────────┐    │    │
│    │    │         765 kanonische Positionen            │    │    │
│    │    │           (mit Symmetrie-Reduktion)          │    │    │
│    │    └─────────────────────────────────────────────┘    │    │
│    └───────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

Für das maschinelle Lernen ist primär die Zahl 6.046 relevant, da diese den State Space für eine Q-Tabelle definiert. Diese vergleichsweise geringe Größe macht Tic-Tac-Toe zu einem idealen Lernbeispiel: Eine vollständige Q-Tabelle benötigt nur etwa 400-500 KB Speicher, das Training dauert wenige Sekunden und perfektes Spiel ist theoretisch erreichbar.

---

## 3.2 Theoretische Grundlagen

### Reinforcement Learning

Reinforcement Learning (RL) ist ein Paradigma des maschinellen Lernens, bei dem ein **Agent** durch Interaktion mit einer **Umgebung** lernt. Der Agent beobachtet den aktuellen **Zustand (State)**, führt eine **Aktion** aus und erhält daraufhin eine **Belohnung (Reward)** sowie den neuen Zustand. Durch wiederholte Interaktion lernt der Agent eine **Policy** (Strategie), die den kumulativen Reward maximiert.

```
                    RL-Interaktionszyklus
                    
        ┌─────────────────────────────────────────┐
        │                                         │
        │    ┌───────────┐      Aktion a_t       │
        │    │           │ ───────────────────►  │
        │    │   Agent   │                       │
        │    │           │ ◄─────────────────── │
        │    └───────────┘   State s_t+1        │
        │         ▲          Reward r_t          │
        │         │                              │
        │    beobachtet                          │
        │    & lernt                             │
        │         │          ┌──────────────┐   │
        │         └──────────│  Umgebung    │   │
        │                    │ (Spielfeld)  │   │
        │                    └──────────────┘   │
        └─────────────────────────────────────────┘
```

Tic-Tac-Toe lässt sich als **Markov Decision Process (MDP)** formalisieren, definiert durch das Tupel $(S, A, P, R, \gamma)$. Der Zustandsraum $S$ umfasst die 6.046 gültigen Spielfeldkonfigurationen, der Aktionsraum $A$ enthält bis zu neun mögliche Züge (je nach Spielsituation), die Übergangswahrscheinlichkeit $P$ ist deterministisch (jeder Zug führt zu genau einem Folgezustand), $R$ ist die Belohnungsfunktion und $\gamma$ der Diskontierungsfaktor.

### Q-Learning

Q-Learning ist ein **model-free, off-policy** RL-Algorithmus. "Model-free" bedeutet, dass keine Kenntnis der Übergangswahrscheinlichkeiten nötig ist – der Agent lernt ausschließlich durch Erfahrung. "Off-policy" heißt, dass der Agent aus Erfahrungen lernt, die nicht unbedingt der aktuellen Strategie entsprechen.

Der Kern des Algorithmus ist die **Q-Funktion** $Q(s, a)$, die den erwarteten kumulativen Reward schätzt, wenn im Zustand $s$ die Aktion $a$ ausgeführt und danach optimal gespielt wird. Die Q-Werte werden iterativ durch die **Bellman Update-Regel** aktualisiert:

$$Q(s, a) \leftarrow Q(s, a) + \alpha \left[ r + \gamma \max_{a'} Q(s', a') - Q(s, a) \right]$$

Diese Formel beschreibt: Der neue Q-Wert ergibt sich aus dem alten plus einer Korrektur. Die Korrektur entspricht der Differenz zwischen dem tatsächlich erhaltenen Reward $r$ plus dem diskontierten maximalen zukünftigen Q-Wert einerseits und dem aktuellen Q-Wert andererseits. Die **Lernrate** $\alpha$ (typisch 0.1-0.3) bestimmt, wie stark neue Erfahrungen alte überschreiben – ein hoher Wert führt zu schnellem, aber potenziell instabilem Lernen. Der **Diskontierungsfaktor** $\gamma$ (typisch 0.9-0.99) gewichtet zukünftige Belohnungen gegenüber unmittelbaren – ein Wert nahe 1 bedeutet, dass der Agent langfristig plant.

Ein zentrales Problem im RL ist das Dilemma zwischen **Exploration und Exploitation**: Soll der Agent bekanntes Wissen nutzen (Exploitation) oder neue Strategien ausprobieren (Exploration)? Die **Epsilon-Greedy Strategie** löst dies, indem mit Wahrscheinlichkeit $\epsilon$ eine zufällige Aktion gewählt wird und mit $1-\epsilon$ die nach Q-Werten beste. Ein typischer Wert ist $\epsilon = 0.3-0.4$ während des Trainings.

### Neuronale Netze im RL-Kontext

Die Aufgabenstellung empfiehlt den Einsatz neuronaler Netze und fordert eine Erläuterung, wann dieser sinnvoll ist. In diesem Abschnitt wird zunächst die Theorie erläutert und anschließend begründet, warum für Tic-Tac-Toe eine tabellarische Q-Funktion gewählt wurde.

#### Deep Q-Networks (DQN) – Theorie

Neuronale Netze können die Q-Funktion approximieren: $Q(s, a; \theta) \approx Q^*(s, a)$, wobei $\theta$ die trainierbaren Gewichte darstellt. Anstatt jeden Zustand einzeln in einer Tabelle zu speichern, lernt das Netzwerk eine Funktion, die für beliebige Eingabezustände Q-Werte berechnet. Diese Architektur wird als **Deep Q-Network (DQN)** bezeichnet.

**Wann ist der Einsatz sinnvoll?**
- **Großer State Space:** Bei Spielen wie Schach ($10^{43}$ Zustände) oder Go ($10^{170}$ Zustände) ist eine vollständige Q-Tabelle nicht speicherbar – hier ist Funktionsapproximation zwingend erforderlich.
- **Kontinuierliche Zustände:** Bei Robotersteuerung oder autonomem Fahren sind Zustände keine diskreten Werte, sondern kontinuierliche Sensorwerte (Winkel, Geschwindigkeit, Pixelwerte).
- **Generalisierung:** Neuronale Netze können von ähnlichen Zuständen lernen – ein Schachbrett mit leicht verschobenen Figuren sollte ähnlich bewertet werden.

#### Implementierung und empirischer Vergleich

Um die Empfehlung der Aufgabenstellung zu evaluieren, wurde zusätzlich zur tabellarischen Q-Funktion ein **vollständiges neuronales Netz in Pure Java** implementiert (ohne externe Bibliotheken). Die Architektur umfasst:

- `NeuralNetwork.java`: Multi-Layer Perceptron (9 → 128 → 64 → 9), 10.121 Parameter
- `Matrix.java`: Matrix-Vektor Operationen, Xavier-Initialisierung
- `ActivationFunction.java`: ReLU, Sigmoid, Tanh, Linear mit Ableitungen
- `Layer.java`: Forward- und Backward-Pass (Backpropagation)
- `ExperienceReplay.java`: DQN-typischer Replay Buffer

**Benchmark-Ergebnisse:**

| Metrik | Q-Tabelle | Neural Network | Verhältnis |
|--------|-----------|----------------|------------|
| Trainingszeit | 0,18s | 14,63s | 81× langsamer |
| Episoden | 100.000 | 10.000 | 10× weniger |
| Siegrate | 96,7% | 78,7% | NN schlechter |
| Geschwindigkeit | 558.654 Ep./s | 684 Ep./s | 817× langsamer |
| Code-Komplexität | ~500 LOC | ~1.140 LOC | 2,3× mehr |
| Interpretierbarkeit | ✓ (JSON) | ✗ (Black Box) | – |

#### Begründung der Designentscheidung

Obwohl die Aufgabenstellung neuronale Netze empfiehlt, zeigt der empirische Vergleich eindeutig, dass für Tic-Tac-Toe die **tabellarische Q-Funktion die bessere Wahl** ist:

1. **State Space ist klein:** Mit nur 6.046 gültigen Zuständen ist eine vollständige Q-Tabelle problemlos speicherbar (~400 KB). Neuronale Netze entfalten ihren Vorteil erst bei State Spaces, die zu groß für Tabellen sind.

2. **81-fach schnelleres Training:** Die Q-Tabelle erreicht bessere Ergebnisse in einem Bruchteil der Zeit, da keine Matrixmultiplikationen und kein Backpropagation nötig sind.

3. **Höhere Siegrate:** 96,7% vs. 78,7% – die Q-Tabelle kann jeden Zustand exakt speichern, während das NN nur approximiert und dabei Information verliert.

4. **Keine Hyperparameter-Tuning:** Neuronale Netze erfordern sorgfältige Abstimmung von Lernrate, Netzwerkarchitektur, Batch-Größe etc. Q-Learning ist robuster.

5. **Interpretierbarkeit:** Die Q-Tabelle kann als JSON exportiert und inspiziert werden – welche Züge werden in welchen Situationen bevorzugt? Ein NN ist eine Black Box.

**Fazit:** Die Empfehlung der Aufgabenstellung ist für größere Probleme korrekt, aber für Tic-Tac-Toe gilt das Prinzip "Keep It Simple". Ein neuronales Netz wurde implementiert und getestet, aber bewusst nicht als finale Lösung gewählt.

### Belohnungsstruktur

Die gewählte Reward-Funktion ist einfach: Sieg ergibt $+1.0$, Niederlage $-1.0$, Unentschieden und Zwischenzüge $0.0$. Diese **"sparse reward"-Struktur** – Belohnung nur am Spielende – funktioniert durch den Diskontierungsfaktor, der den Wert rückwärts durch die Zugsequenz propagiert: Ein Zug, der zum Sieg führt, erhält durch wiederholtes Training einen hohen Q-Wert, auch wenn der Reward erst später erfolgt.

```
Reward-Propagation durch Diskontierung (γ = 0.95):

  Zug 1      Zug 2      Zug 3      Zug 4      SIEG!
    │          │          │          │          │
    ▼          ▼          ▼          ▼          ▼
  ┌───┐      ┌───┐      ┌───┐      ┌───┐      ┌───┐
  │ X │ ───► │ O │ ───► │ X │ ───► │ O │ ───► │ X │
  └───┘      └───┘      └───┘      └───┘      └───┘
    │          │          │          │          │
 Q-Wert:    Q-Wert:    Q-Wert:    Q-Wert:   Reward:
  0.81       0.86       0.90       0.95       1.0
   ◄──────────◄──────────◄──────────◄──────────┘
           Rückwärts-Propagation (× γ)
```

---

## 3.3 Lösungsarchitektur und Implementierung

### Bereitgestellte Komponenten

Als Grundlage für die Implementierung wurde von der Hochschule die Bibliothek `tic_tac_toe.jar` bereitgestellt. Diese enthält die grundlegende Spielinfrastruktur und definiert die zu implementierenden Schnittstellen:

```
Bereitgestellte Klassen (tic_tac_toe.jar):

┌─────────────────────────────────────────────────────────────────┐
│  tictactoe.*                                                     │
│  ├── Spielfeld        Repräsentiert das 3×3 Spielbrett          │
│  ├── Zug              Ein Spielzug (Zeile, Spalte)              │
│  ├── Farbe            Enum: KREIS oder KREUZ                    │
│  ├── Spielstand       Ergebnis eines Spiels                     │
│  ├── TicTacToe        Spiellogik und Regelprüfung               │
│  └── Wettkampf        Testumgebung für Spieler-Evaluation       │
│                                                                  │
│  tictactoe.spieler.*                                             │
│  ├── ISpieler              Interface für alle Spieler           │
│  ├── ILernenderSpieler     Erweitertes Interface für RL-Spieler │
│  └── Zufallsspieler        Beispiel-Gegner für Tests            │
└─────────────────────────────────────────────────────────────────┘
```

Das Interface `ILernenderSpieler` schreibt die Methoden `neuesSpiel(Farbe, int)` und `berechneZug(Zug, long, long)` vor, die von der eigenen Implementierung überschrieben werden müssen.

### Architekturübersicht (Eigenentwicklung)

Die eigene Implementierung folgt dem **Single Responsibility Principle (SRP)** sowie dem **Facade Pattern**. Das SRP besagt, dass jede Klasse genau eine Verantwortlichkeit haben soll, was Wartbarkeit und Testbarkeit verbessert. Das Facade Pattern stellt eine vereinfachte Schnittstelle zu einem komplexen Subsystem bereit.

```
Eigenentwickelte Klassen:

┌────────────────────────────────────────────────────────┐
│                      Spieler                            │
│        (Facade - Implementiert ILernenderSpieler)      │
│  Orchestriert alle Komponenten, verwaltet Spielfeld    │
└───────────────────────┬────────────────────────────────┘
                        │ verwendet
                        ▼
┌────────────────────────────────────────────────────────┐
│                   QLearningAgent                        │
│   Q-Tabelle, Bellman-Update, Epsilon-Greedy-Auswahl    │
└───────────────────────┬────────────────────────────────┘
                        │ verwendet
          ┌─────────────┴─────────────┐
          ▼                           ▼
┌──────────────────────┐   ┌──────────────────────┐
│ SpielzustandKonverter│   │ SpielzustandAnalyzer │
│ Spielfeld → String   │   │ Spielende, Reward,   │
│ für Q-Tabelle        │   │ mögliche Züge        │
└──────────────────────┘   └──────────────────────┘
```

Die `Spieler`-Klasse implementiert das Interface `ILernenderSpieler` und dient als Fassade. Sie verwaltet das interne Spielfeld, delegiert die Zugauswahl an den `QLearningAgent` und koordiniert den Trainingsablauf. Der `QLearningAgent` enthält die Q-Tabelle als HashMap, die Spielzustände (Strings) auf Arrays mit neun Q-Werten abbildet – einen pro möglichem Feld. Der `SpielzustandKonverter` transformiert ein Spielfeld in eine normalisierte String-Repräsentation (z.B. "X..O.X..."), wobei stets die eigene Farbe als X und die gegnerische als O kodiert wird. Der `SpielzustandAnalyzer` prüft Gewinnbedingungen, berechnet Rewards und ermittelt gültige Züge.

### Lernalgorithmus im Code

Der zentrale Lernmechanismus ist in der Methode `lernen()` implementiert:

```java
public void lernen(String state, int aktion, double reward, 
                   String naechsterState, boolean istTerminal) {
    double[] qWerte = getQWerte(state);
    double alterQWert = qWerte[aktion];
    
    double neuerQWert;
    if (istTerminal) {
        // Bei Spielende: Nur direkten Reward verwenden
        neuerQWert = alterQWert + lernrate * (reward - alterQWert);
    } else {
        // Während des Spiels: Zukünftigen Wert einbeziehen
        double maxNaechsterQ = getMaxQWert(naechsterState);
        neuerQWert = alterQWert + lernrate * 
            (reward + discountFaktor * maxNaechsterQ - alterQWert);
    }
    qWerte[aktion] = neuerQWert;
}
```

### Trainingsablauf

Das Training erfolgt in einem Loop über 100.000 Episoden. Jede Episode startet mit leerem Spielfeld. Agent und Gegner (Zufallsspieler) ziehen abwechselnd, wobei der Agent per Epsilon-Greedy wählt. Nach jedem Zug wird ein Tupel (Zustand, Aktion, Reward, Folgezustand) gespeichert. Am Spielende werden die Q-Werte aller Züge rückwärts aktualisiert, wodurch der Endreward durch die gesamte Zugsequenz propagiert wird.

```
                         TRAININGS-LOOP
                              │
                              ▼
                 ┌────────────────────────┐
                 │  1. Neues Spiel starten │
                 │     spielfeld.reset()   │
                 └───────────┬─────────────┘
                             │
          ┌──────────────────┴──────────────────┐
          │                                      │
          ▼                                      ▼
  ┌───────────────┐                    ┌───────────────┐
  │ Agent (Spieler)│◄────────────────►│Gegner (Random) │
  │ • ε-Greedy    │    abwechselnd    │                │
  │ • Q-Lookup    │                    │                │
  └───────┬───────┘                    └────────────────┘
          │
          ▼
  ┌─────────────────────────────────────┐
  │ 2. Episode speichern                 │
  │    (state, action, reward, next)    │
  └─────────────────┬───────────────────┘
                    │
                    ▼
  ┌─────────────────────────────────────┐
  │ 3. Bei Spielende:                    │
  │    Rückwärts-Propagation der Q-Werte│
  └─────────────────┬───────────────────┘
                    │
                    ▼
            ┌──────────────┐
            │  Wiederholen  │──── 100.000×
            └──────────────┘
```

Die optimalen **Hyperparameter** nach empirischer Evaluation sind: Lernrate $\alpha = 0.15$, Diskontierungsfaktor $\gamma = 0.95$ und Explorationsrate $\epsilon = 0.40$.

---

## 3.4 Lernerfolgsnachweis und Problemanalyse

### Experimentelles Setup und Ergebnisse

Zur Evaluation spielt der Agent 1.000 Testspiele gegen einen Zufallsspieler, wobei er abwechselnd als Startspieler (X) und Zweitspieler (O) antritt. Vor dem Training erreicht der Agent eine Siegrate von 52.3%, was dem Zufall entspricht. Nach 100.000 Trainings-Episoden steigt diese auf **96.7%** (967 Siege, 9 Niederlagen, 24 Unentschieden). Die State Space Coverage beträgt 92.3% – der Agent hat 5.578 der 6.046 theoretisch möglichen Zustände kennengelernt.

```
Siegrate gegen Zufallsspieler (Entwicklung während Training):

  100% ┤                                          ████████
       │                                    ██████
   90% ┤                              ██████
       │                        ██████
   80% ┤                  ██████
       │            ██████
   70% ┤      ██████
       │██████
   60% ┤
       │
   50% ┼────────────────────────────────────────────────────
       └──────┬──────┬──────┬──────┬──────┬──────┬──────┬───►
             10k    20k    30k    50k    70k   100k   Episoden
       
       Vor Training: 52.3%  →  Nach Training: 96.7%  (+44.4%)
```

Im Vergleich zum neuronalen Netzwerk zeigt sich tabellarisches Q-Learning als deutlich effizienter: Die Trainingszeit beträgt 0.18 Sekunden gegenüber 14.63 Sekunden (81× schneller), die Siegrate ist mit 96.7% gegenüber 78.7% höher, und der Code umfasst nur etwa 500 statt 1.140 Zeilen.

### Festgestellte Probleme und Lösungen

Ein niedriger $\epsilon$-Wert von 0.1 führte zu nur 65% State Coverage, da der Agent zu wenig explorierte und viele Spielsituationen nie kennenlernte. Die Erhöhung auf $\epsilon = 0.4$ löste dieses Problem. Zudem lernte der Agent als Startspieler besser als als Zweitspieler, da X statistisch im Vorteil ist – balanciertes Training mit abwechselnden Farben gleicht dies aus. Die Sparse-Reward-Struktur erschwert das **Credit Assignment** (welcher Zug war für den Sieg verantwortlich?), weshalb die Rückwärts-Propagation der Q-Werte am Spielende essentiell ist.

```
Einfluss der Explorationsrate ε auf State Coverage:

  ε = 0.1 (zu niedrig)       ε = 0.4 (optimal)
  ┌────────────────────┐     ┌────────────────────┐
  │████████████████    │     │████████████████████│
  │████████████████    │     │████████████████████│
  │████████████        │     │████████████████████│
  │████████            │     │████████████████████│
  │████                │     │██████████████████  │
  └────────────────────┘     └────────────────────┘
     65% Coverage               92.3% Coverage
     
  Problem: Zu viel          Lösung: Mehr zufällige
  Exploitation → Agent      Exploration → Agent
  besucht immer gleiche     entdeckt seltenere
  States                    Spielsituationen
```

---

## 3.5 Fazit

Die Implementierung eines Q-Learning-Agenten für Tic-Tac-Toe war erfolgreich. Der Agent erreicht eine Siegrate von 96.7% gegen Zufallsspieler bei einer State Coverage von 92.3% und einer Trainingszeit unter einer Sekunde. Die modulare Architektur mit klarer Trennung der Verantwortlichkeiten ermöglicht einfache Wartung und Erweiterung.

Die zentrale Erkenntnis ist, dass **tabellarisches Q-Learning für kleine State Spaces effizienter ist als neuronale Netze**. Die Wahl des richtigen Algorithmus hängt stark von der Problemgröße ab. Für komplexere Spiele wie Connect Four oder Schach wären Deep Q-Networks oder Policy Gradient Methoden sinnvoll.

---

## Quellenverzeichnis

[1] Sutton, R. S., & Barto, A. G. (2018). *Reinforcement Learning: An Introduction* (2nd ed.). MIT Press.

[2] Watkins, C. J. C. H., & Dayan, P. (1992). Q-learning. *Machine Learning*, 8(3-4), 279-292.

[3] Mnih, V., et al. (2015). Human-level control through deep reinforcement learning. *Nature*, 518(7540), 529-533.