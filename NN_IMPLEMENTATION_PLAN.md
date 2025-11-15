# Neural Network Implementation für Tic-Tac-Toe

## Ziel

Demonstrieren, dass ein Neural Network für Tic-Tac-Toe **Overkill** ist, indem wir:
1. Ein einfaches NN implementieren
2. Performance mit Q-Learning vergleichen
3. Ressourcen-Verbrauch dokumentieren

---

## Architektur

### Neural Network Design (Minimalistisch):

```
Input Layer:    9 Neuronen  (3×3 Board: -1=O, 0=Leer, 1=X)
Hidden Layer 1: 64 Neuronen (ReLU Activation)
Hidden Layer 2: 32 Neuronen (ReLU Activation)
Output Layer:   9 Neuronen  (Q-Values für jede Aktion, Linear)
```

**Total Parameter:** ~2,500 Gewichte

### Framework-Optionen:

#### Option 1: Pure Java (von Grund auf)
**Vorteile:**
- ✅ Keine externen Dependencies
- ✅ Volle Kontrolle
- ✅ Verständnis aller Details

**Nachteile:**
- ❌ Viel Implementierungsaufwand
- ❌ Langsamer als optimierte Libraries
- ❌ Matrix-Operationen manuell

#### Option 2: DeepLearning4J (DL4J)
**Vorteile:**
- ✅ Native Java Library
- ✅ GPU-Support (optional)
- ✅ Production-ready

**Nachteile:**
- ❌ Große Dependency (~150 MB)
- ❌ Komplexe Setup
- ❌ Overhead für kleines Problem

#### Option 3: ONNX + Java Runtime
**Vorteile:**
- ✅ In Python trainieren, in Java ausführen
- ✅ Framework-unabhängig

**Nachteile:**
- ❌ Zwei Sprachen nötig
- ❌ Export/Import Overhead

---

## Implementierungsplan

### Phase 1: Pure Java NN (Empfohlen für Demo)

**Warum Pure Java?**
- Demonstriert die Komplexität ohne Framework-Magie
- Zeigt wie "einfach" Q-Learning im Vergleich ist
- Keine Dependencies = Faire Vergleichbarkeit

**Komponenten:**

```java
tic_tac_toe_mi/
├── nn/
│   ├── NeuralNetwork.java       # Main NN Class
│   ├── Layer.java                # Fully Connected Layer
│   ├── ActivationFunction.java  # ReLU, Sigmoid, etc.
│   ├── LossFunction.java         # MSE für Q-Learning
│   └── NNSpieler.java           # ILernenderSpieler Implementation
```

### Phase 2: Training-Loop

**DQN (Deep Q-Network) Algorithmus:**

```
1. Initialisiere NN mit zufälligen Gewichten
2. Für jede Episode:
   a. Wähle Aktion (ε-greedy)
   b. Führe Aktion aus, beobachte Reward
   c. Speichere Experience (State, Action, Reward, NextState)
   d. Sample Mini-Batch aus Experience Replay
   e. Berechne Target: r + γ * max(Q(s', a'))
   f. Update NN via Backpropagation (Minimize MSE)
3. Reduziere ε über Zeit
```

**Unterschied zu Q-Learning:**
- Q-Table: Direct Update (eine Zelle)
- Neural Net: Gradient Descent über alle Gewichte

### Phase 3: Benchmark & Vergleich

**Metriken sammeln:**
- Training-Zeit bis 75% Siegrate
- Anzahl Spiele nötig
- Memory-Verbrauch
- Inference-Latency
- Code-Komplexität (Lines of Code)

---

## Erwartete Ergebnisse

| Metrik | Q-Learning | Neural Network (erwartet) |
|--------|------------|---------------------------|
| Training-Zeit (75% Siegrate) | 0.1s | 10-60s |
| Anzahl Spiele | 50k | 500k-1M |
| Memory | 594 KB | 5-10 MB |
| Inference | <0.001s | ~0.01s |
| Lines of Code | ~500 | ~1500+ |
| Komplexität | Einfach | Komplex |

---

## Nächste Schritte

### 1. Minimales NN implementieren

```java
public class NeuralNetwork {
    private Layer[] layers;
    
    public NeuralNetwork(int[] layerSizes) {
        // Initialize layers with random weights
    }
    
    public double[] forward(double[] input) {
        // Forward pass through network
    }
    
    public void backward(double[] target, double learningRate) {
        // Backpropagation
    }
}
```

### 2. NNSpieler erstellen

```java
public class NNSpieler implements ILernenderSpieler {
    private NeuralNetwork nn;
    private ExperienceReplay replay;
    
    public void verarbeiteZug(Zug zug) {
        // Store experience, train network
    }
}
```

### 3. Training ausführen

```java
public class NNDemo {
    public static void main(String[] args) {
        NNSpieler nnSpieler = new NNSpieler();
        nnSpieler.trainieren(new AbbruchNachIterationen(500000));
        
        // Compare vs Q-Learning
    }
}
```

---

## Proof of Overkill

**Was wir zeigen werden:**

1. **NN braucht ~100x mehr Zeit** für ähnliche Performance
2. **NN braucht ~10x mehr Memory**
3. **NN braucht ~1000x mehr Code**
4. **NN ist eine Black Box** (Q-Table ist transparent)
5. **NN braucht Hyperparameter-Tuning** (Learning Rate, Batch Size, etc.)

**Conclusion:**
> *"For a state space of ~6,046 states, a simple lookup table (Q-Learning) 
> is orders of magnitude more efficient than a neural network approximator."*

---

## Implementation Status

- [ ] Neural Network Basis-Klassen
- [ ] Activation Functions (ReLU, Sigmoid)
- [ ] Forward Pass
- [ ] Backpropagation
- [ ] NNSpieler Integration
- [ ] Experience Replay Buffer
- [ ] Training Loop
- [ ] Benchmark vs Q-Learning
- [ ] Dokumentation der Ergebnisse

**Geschätzter Aufwand:** 4-6 Stunden für vollständige Implementation + Benchmark

---

**Bereit für Implementation?** → Sage Bescheid und ich starte mit Phase 1!
