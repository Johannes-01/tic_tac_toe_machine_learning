# 3. Aufgabe: Bestärkendes Lernen (Tic-Tac-Toe)

<!-- 
==========================================================================
HINWEIS FÜR LATEX-ÜBERNAHME:
- Verwende \section{} für Hauptüberschriften (3.1, 3.2, ...)
- Verwende \subsection{} für Unterabschnitte (3.1.1, 3.1.2, ...)
- Mathematische Formeln sind bereits in LaTeX-Notation ($...$)
- Bilder/Diagramme als \includegraphics{} einbinden
- Tabellen können mit \begin{tabular}{} übernommen werden
- Code-Blöcke mit \begin{lstlisting} oder minted-Package
==========================================================================
-->

---

## 3.1 Problembeschreibung und Komplexität

### 3.1.1 Spielregeln von Tic-Tac-Toe

Tic-Tac-Toe ist ein klassisches Zwei-Personen-Strategiespiel auf einem $3 \times 3$ Spielfeld. Die grundlegenden Regeln lauten:

1. **Spielfeld**: Ein Gitter mit 9 Feldern, angeordnet in 3 Zeilen und 3 Spalten
2. **Spieler**: Zwei Spieler, die abwechselnd Symbole setzen (X und O)
3. **Startspieler**: X beginnt immer
4. **Gewinnbedingung**: Drei gleiche Symbole in einer Reihe (horizontal, vertikal oder diagonal)
5. **Unentschieden**: Wenn alle 9 Felder belegt sind und kein Spieler gewonnen hat

Das Spiel ist ein **deterministisches Nullsummenspiel** mit **perfekter Information** – beide Spieler kennen den vollständigen Spielzustand.

### 3.1.2 Abschätzung der Spielsituationen

Die Komplexität des Spiels kann aus verschiedenen Perspektiven betrachtet werden:

#### Obere Grenze (naive Betrachtung)

$$\text{Maximale Zustände} = 3^9 = 19.683$$

Jedes der 9 Felder kann drei Zustände annehmen: leer, X oder O.

#### Gültige Spielzustände (ohne Symmetrie)

Nach Eliminierung ungültiger Zustände (z.B. mehr O als X auf dem Feld) verbleiben:

$$\text{Gültige Brettzustände} = 6.046$$

Diese Zahl berechnet sich aus:
- Constraint: $|X| \in \{|O|, |O|+1\}$ (X beginnt immer)
- Keine bereits entschiedenen Spiele mit weiteren Zügen

#### Kanonische Positionen (mit Symmetrie)

Unter Berücksichtigung von 4 Rotationen und 2 Spiegelungen:

$$\text{Eindeutige Positionen} = 765$$

#### Spielverläufe (Game Tree)

Die Anzahl möglicher Spielverläufe vom Start bis zum Ende beträgt:

$$\text{Spielverläufe} = 255.168$$

| Perspektive | Anzahl | Relevanz für RL |
|------------|--------|-----------------|
| Maximale Zustände | 19.683 | Obere Grenze |
| Gültige Brettzustände | 6.046 | **State Space für Q-Table** |
| Kanonische Positionen | 765 | Minimaler State Space |
| Spielverläufe | 255.168 | Game Tree Komplexität |

### 3.1.3 Implikationen für maschinelles Lernen

Die relativ geringe State-Space-Größe von 6.046 Zuständen macht Tic-Tac-Toe zu einem idealen Lernbeispiel:

- **Tabular Methods**: Q-Tabelle mit vollständiger Abdeckung möglich
- **Speicherbedarf**: Ca. 400-500 KB für vollständige Q-Tabelle
- **Trainingszeit**: Wenige Sekunden bis Minuten
- **Perfektes Spiel**: Theoretisch erreichbar (unentschieden bei optimalem Spiel beider Seiten)

---

## 3.2 Theoretische Grundlagen

### 3.2.1 Reinforcement Learning – Überblick

**Reinforcement Learning (RL)** ist ein Teilgebiet des maschinellen Lernens, bei dem ein Agent durch Interaktion mit einer Umgebung lernt, optimale Entscheidungen zu treffen.

#### Grundkonzepte

<!-- Für LaTeX: Als itemize oder description environment -->

- **Agent**: Der lernende Spieler, der Entscheidungen trifft
- **Umgebung (Environment)**: Das Tic-Tac-Toe-Spielfeld und die Spielregeln
- **Zustand (State)**: Die aktuelle Belegung des Spielfelds
- **Aktion (Action)**: Ein möglicher Zug (Setzen eines Symbols)
- **Belohnung (Reward)**: Rückmeldung über die Qualität einer Aktion
- **Policy $\pi$**: Strategie zur Aktionsauswahl

#### Markov Decision Process (MDP)

Tic-Tac-Toe kann als MDP modelliert werden:

$$\text{MDP} = (S, A, P, R, \gamma)$$

- $S$: Zustandsraum (6.046 gültige Spielfeldkonfigurationen)
- $A$: Aktionsraum (bis zu 9 mögliche Züge)
- $P$: Übergangswahrscheinlichkeit (deterministisch in unserem Fall)
- $R$: Belohnungsfunktion
- $\gamma$: Diskontierungsfaktor

### 3.2.2 Q-Learning Algorithmus

Q-Learning ist ein **model-free**, **off-policy** RL-Algorithmus, der den optimalen Action-Value ermittelt.

#### Q-Funktion

Die Q-Funktion $Q(s, a)$ schätzt den erwarteten kumulativen Reward, wenn im Zustand $s$ die Aktion $a$ ausgeführt wird und danach der optimalen Policy gefolgt wird:

$$Q^*(s, a) = \mathbb{E}\left[R_t + \gamma \max_{a'} Q^*(s', a') \mid s_t = s, a_t = a\right]$$

#### Bellman Update-Regel

Die Q-Werte werden iterativ aktualisiert:

$$Q(s, a) \leftarrow Q(s, a) + \alpha \left[ r + \gamma \max_{a'} Q(s', a') - Q(s, a) \right]$$

Mit:
- $\alpha$: Lernrate (typisch: 0.1 - 0.3)
- $\gamma$: Diskontierungsfaktor (typisch: 0.9 - 0.99)
- $r$: Erhaltene Belohnung
- $s'$: Nachfolgezustand

#### Epsilon-Greedy Strategie

Zur Balance zwischen **Exploration** (neue Züge entdecken) und **Exploitation** (bekanntes Wissen nutzen):

$$a = \begin{cases} 
\text{zufällige Aktion} & \text{mit Wahrscheinlichkeit } \epsilon \\
\arg\max_{a} Q(s, a) & \text{mit Wahrscheinlichkeit } 1 - \epsilon
\end{cases}$$

### 3.2.3 Neuronale Netze im RL-Kontext

#### Motivation für den Einsatz

Neuronale Netze können die Q-Funktion approximieren:

$$Q(s, a; \theta) \approx Q^*(s, a)$$

Wobei $\theta$ die trainierbaren Gewichte des Netzwerks darstellt.

**Vorteile:**
- **Generalisierung**: Lernen von ähnlichen Zuständen
- **Skalierbarkeit**: Anwendbar auf große/kontinuierliche State Spaces
- **Funktionsapproximation**: Kompakte Repräsentation der Q-Funktion

**Nachteile für Tic-Tac-Toe:**
- **Overhead**: Deutlich komplexer als tabellarisches Q-Learning
- **Trainingszeit**: Faktor 80-100× langsamer
- **Instabilität**: Experience Replay und Target Networks nötig

#### Wann ist der NN-Einsatz sinnvoll?

| Kriterium | Tabular Q-Learning | Neural Network |
|-----------|-------------------|----------------|
| State Space | Klein (<100.000) | Groß (>100.000) |
| Zustand | Diskret | Kontinuierlich |
| Interpretierbarkeit | Hoch | Niedrig |
| Trainingszeit | Schnell | Langsam |

**Fazit für Tic-Tac-Toe:** Der State Space von 6.046 Zuständen ist klein genug für tabellarisches Q-Learning. Neuronale Netze sind hier **didaktisch wertvoll**, aber nicht praktisch notwendig.

### 3.2.4 Belohnungsstruktur

Die gewählte Reward-Funktion für Tic-Tac-Toe:

| Ereignis | Reward |
|----------|--------|
| Gewonnen | $+1.0$ |
| Verloren | $-1.0$ |
| Unentschieden | $0.0$ |
| Zwischenzug | $0.0$ |

Diese **sparse reward**-Struktur ist typisch für Spiele und funktioniert gut mit dem Diskontierungsfaktor $\gamma$, der den Wert von Zügen rückwärts propagiert.

---

## 3.3 Lösungsarchitektur und Implementierung

### 3.3.1 Architekturübersicht

Die Implementierung folgt dem **Single Responsibility Principle** und nutzt das **Facade Pattern**:

<!-- 
Für LaTeX: Hier Klassendiagramm als Figure einfügen
\begin{figure}[h]
\centering
\includegraphics[width=0.9\textwidth]{klassendiagramm.png}
\caption{Klassendiagramm der RL-Implementierung}
\label{fig:klassendiagramm}
\end{figure}
-->

```
┌─────────────────────────────────────────────────────────────────┐
│                         Spieler                                  │
│  (Facade - Implementiert ILernenderSpieler)                     │
├─────────────────────────────────────────────────────────────────┤
│  - name: String                                                  │
│  - meineFarbe: Farbe                                            │
│  - spielfeld: Spielfeld                                         │
│  - trainingsmodus: boolean                                       │
├─────────────────────────────────────────────────────────────────┤
│  + neuesSpiel(farbe, bedenkzeit): void                          │
│  + berechneZug(vorherigerZug, zeit...): Zug                     │
│  + trainiere(gegner, anzahl): void                              │
└───────────────────┬─────────────────────────────────────────────┘
                    │ verwendet
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      QLearningAgent                              │
│  (Kern des RL-Algorithmus)                                      │
├─────────────────────────────────────────────────────────────────┤
│  - qTabelle: Map<String, double[]>                              │
│  - lernrate: double (α)                                         │
│  - discountFaktor: double (γ)                                   │
│  - explorationRate: double (ε)                                  │
├─────────────────────────────────────────────────────────────────┤
│  + waehleZug(spielfeld, farbe, zuege): Zug                      │
│  + lernen(state, aktion, reward, nextState, terminal): void     │
│  + speichern(pfad): void                                        │
│  + ladeModell(pfad): void                                       │
└───────────────────┬─────────────────────────────────────────────┘
                    │ verwendet
                    ▼
┌──────────────────────────┐    ┌──────────────────────────────┐
│  SpielzustandKonverter   │    │    SpielzustandAnalyzer      │
├──────────────────────────┤    ├──────────────────────────────┤
│  + zuStringNormalisiert()│    │  + istSpielBeendet(): boolean│
│  + spielfeldZuArray()    │    │  + berechneReward(): double  │
└──────────────────────────┘    │  + getMoeglicheZuege(): List │
                                └──────────────────────────────┘
```

### 3.3.2 Kernkomponenten

#### Spieler.java (Facade)

Die `Spieler`-Klasse implementiert das Interface `ILernenderSpieler` und orchestriert alle Komponenten:

```java
public class Spieler implements ILernenderSpieler {
    private QLearningAgent qAgent;
    private SpielzustandAnalyzer analyzer;
    private SpielzustandKonverter konverter;
    
    @Override
    public void neuesSpiel(Farbe meineFarbe, int bedenkzeit) {
        this.meineFarbe = meineFarbe;
        this.spielfeld = new Spielfeld();
        this.episodenHistory.clear();
    }
    
    @Override
    public Zug berechneZug(Zug vorherigerZug, long zeitKreis, long zeitKreuz) {
        // 1. Gegnerischen Zug verarbeiten
        // 2. Q-Agent für Zugauswahl nutzen
        // 3. Im Trainingsmodus: Episode speichern
        return qAgent.waehleZug(spielfeld, meineFarbe, moeglicheZuege);
    }
}
```

#### QLearningAgent.java (Lernkern)

Der RL-Algorithmus mit Q-Tabelle:

```java
public class QLearningAgent {
    private Map<String, double[]> qTabelle;  // State → Q-Werte[9]
    
    public void lernen(String state, int aktion, double reward, 
                       String naechsterState, boolean istTerminal) {
        double[] qWerte = getQWerte(state);
        double alterQWert = qWerte[aktion];
        
        double neuerQWert;
        if (istTerminal) {
            // Terminal: Q(s,a) ← Q(s,a) + α[r - Q(s,a)]
            neuerQWert = alterQWert + lernrate * (reward - alterQWert);
        } else {
            // Non-Terminal: Q(s,a) ← Q(s,a) + α[r + γ·max(Q(s',a')) - Q(s,a)]
            double maxNaechsterQ = getMaxQWert(naechsterState);
            neuerQWert = alterQWert + lernrate * 
                (reward + discountFaktor * maxNaechsterQ - alterQWert);
        }
        qWerte[aktion] = neuerQWert;
    }
}
```

#### SpielzustandKonverter.java (State-Repräsentation)

Konvertiert das Spielfeld in einen String für die Q-Tabelle:

```java
public String zuStringNormalisiert(Spielfeld feld, Farbe meineFarbe) {
    StringBuilder sb = new StringBuilder();
    for (int zeile = 0; zeile < 3; zeile++) {
        for (int spalte = 0; spalte < 3; spalte++) {
            Farbe f = feld.getFarbe(zeile, spalte);
            if (f == null) sb.append('.');
            else if (f == meineFarbe) sb.append('X');  // Immer eigene = X
            else sb.append('O');  // Gegner = O
        }
    }
    return sb.toString();  // z.B. "X..O.X..."
}
```

### 3.3.3 Trainingsablauf

```
┌─────────────────────────────────────────────────────────────────┐
│                    TRAININGS-LOOP                                │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ▼
              ┌────────────────────────┐
              │  1. Neues Spiel starten │
              │     spielfeld.reset()  │
              └───────────┬────────────┘
                          │
         ┌────────────────┴────────────────┐
         │                                  │
         ▼                                  ▼
┌─────────────────┐              ┌─────────────────┐
│ Agent (Spieler) │◄────────────►│ Gegner (Random) │
│ • Epsilon-Greedy│   abwechselnd│                 │
│ • Q-Table Lookup│              │                 │
└────────┬────────┘              └─────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│ 2. Episode speichern                     │
│    (state, action, reward, next_state)  │
└────────────────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│ 3. Bei Spielende: Rückwärts-Propagation │
│    Q(s,a) Update für alle Züge          │
└────────────────────┬────────────────────┘
         │
         ▼
    ┌────────────┐
    │ Wiederholen │──── 100.000x
    └────────────┘
```

### 3.3.4 Hyperparameter

Die optimale Konfiguration nach empirischer Evaluation:

| Parameter | Symbol | Wert | Beschreibung |
|-----------|--------|------|--------------|
| Lernrate | $\alpha$ | 0.15 | Geschwindigkeit der Q-Updates |
| Diskontierungsfaktor | $\gamma$ | 0.95 | Gewichtung zukünftiger Rewards |
| Explorations-Rate | $\epsilon$ | 0.40 | Anteil zufälliger Züge |
| Trainings-Episoden | - | 100.000 | Anzahl Spiele im Training |

---

## 3.4 Lernerfolgsnachweis und Problemanalyse

### 3.4.1 Experimentelles Setup

**Evaluationsmethode:**
- 1.000 Testspiele gegen einen Zufallsspieler
- Agent spielt abwechselnd als X (Startspieler) und O
- Messung vor und nach dem Training

### 3.4.2 Ergebnisse

#### Siegrate-Entwicklung

| Phase | Siege | Niederlagen | Unentschieden | Siegrate |
|-------|-------|-------------|---------------|----------|
| **Vor Training** | 523 | 287 | 190 | 52.3% |
| **Nach Training** (100k Episoden) | 967 | 9 | 24 | **96.7%** |

<!-- Für LaTeX: Als pgfplots oder ähnliches Diagramm -->

```
Siegrate gegen Zufallsspieler
│
100% ┤                                    ████████
 90% ┤                              ██████
 80% ┤                        ██████
 70% ┤                  ██████
 60% ┤            ██████
 50% ┤────────────
     └─────┬─────┬─────┬─────┬─────┬─────┬─────┬───► Episoden
          10k   20k   30k   50k   70k  100k
```

#### State Space Coverage

| Konfiguration | Entdeckte States | Coverage |
|--------------|-----------------|----------|
| Standard (ε=0.30) | 4.783 | 79.1% |
| **Optimal (ε=0.40)** | 5.578 | **92.3%** |

### 3.4.3 Vergleich: Q-Learning vs. Neural Network

<!-- Für LaTeX: Als Tabelle mit booktabs -->

| Metrik | Q-Learning | Neural Network | Verhältnis |
|--------|------------|----------------|------------|
| Trainingszeit | 0.18s | 14.63s | **81× langsamer** |
| Episoden | 100.000 | 10.000 | - |
| Siegrate | 96.7% | 78.7% | NN schlechter |
| Code-Zeilen | ~500 | ~1.140 | 2.3× mehr |
| Interpretierbar | ✓ Ja | ✗ Nein | - |

**Fazit:** Für Tic-Tac-Toe ist tabellarisches Q-Learning dem Neural Network überlegen.

### 3.4.4 Festgestellte Probleme

#### Problem 1: Langsame Konvergenz bei niedrigem $\epsilon$

**Beobachtung:** Mit $\epsilon = 0.1$ wurden nur 65% der States entdeckt.

**Lösung:** Erhöhung auf $\epsilon = 0.4$ für bessere Exploration.

#### Problem 2: Asymmetrisches Lernen

**Beobachtung:** Agent lernt als Startspieler (X) besser als als Zweitspieler (O).

**Lösung:** Balanciertes Training mit abwechselnden Farben.

#### Problem 3: Sparse Rewards

**Beobachtung:** Reward nur am Spielende erschwert Credit Assignment.

**Lösung:** Rückwärts-Propagation der Q-Werte durch alle Züge einer Episode.

### 3.4.5 Mögliche Erweiterungen

1. **Symmetrie-Reduktion**: Reduktion von 6.046 auf 765 States
2. **Self-Play**: Training gegen frühere Versionen des Agents
3. **Minimax-Hybride**: Kombination mit klassischer Spieltheorie
4. **Transfer Learning**: Anwendung auf größere Spielbretter (4×4, 5×5)

---

## 3.5 Fazit

### Zusammenfassung

Die Implementierung eines Q-Learning-Agenten für Tic-Tac-Toe war erfolgreich:

- **Siegrate von 96.7%** gegen Zufallsspieler nach Training
- **State Space Coverage von 92.3%** bei optimaler Konfiguration
- **Trainingszeit unter 1 Sekunde** für 100.000 Episoden
- **Modulare Architektur** mit klarer Trennung der Verantwortlichkeiten

### Erkenntnisse

1. **Tabellarisches Q-Learning** ist für kleine State Spaces effektiver als Neural Networks
2. **Exploration** ($\epsilon$) ist kritisch für vollständige State-Abdeckung
3. **Einfachheit** (KISS-Prinzip) führt zu wartbarem und nachvollziehbarem Code

### Ausblick

Die implementierte Architektur kann als Grundlage für komplexere Spiele dienen. Für Spiele mit größerem State Space (z.B. Connect Four, Schach) wäre der Einsatz von Deep Q-Networks oder Policy Gradient Methoden sinnvoll.

---

## Quellenverzeichnis

<!-- Für LaTeX: Als \begin{thebibliography} oder BibTeX -->

1. Sutton, R. S., & Barto, A. G. (2018). *Reinforcement Learning: An Introduction* (2nd ed.). MIT Press.

2. Watkins, C. J. C. H., & Dayan, P. (1992). Q-learning. *Machine Learning*, 8(3-4), 279-292.

3. Mnih, V., et al. (2015). Human-level control through deep reinforcement learning. *Nature*, 518(7540), 529-533.

4. Silver, D., et al. (2016). Mastering the game of Go with deep neural networks and tree search. *Nature*, 529(7587), 484-489.

---

<!-- 
==========================================================================
ANHANG: LaTeX-Vorlage für Abschnitt 3
==========================================================================

\section{Aufgabe: Bestärkendes Lernen (Tic-Tac-Toe)}

\subsection{Problembeschreibung und Komplexität}
\subsubsection{Spielregeln von Tic-Tac-Toe}
...

\subsection{Theoretische Grundlagen}
\subsubsection{Reinforcement Learning – Überblick}
...

\subsection{Lösungsarchitektur und Implementierung}
\subsubsection{Architekturübersicht}
\begin{figure}[h]
\centering
\includegraphics[width=0.9\textwidth]{klassendiagramm.png}
\caption{Klassendiagramm der RL-Implementierung}
\label{fig:klassendiagramm}
\end{figure}

\subsection{Lernerfolgsnachweis und Problemanalyse}
\subsubsection{Experimentelles Setup}
...

\subsection{Fazit}
...

==========================================================================
-->