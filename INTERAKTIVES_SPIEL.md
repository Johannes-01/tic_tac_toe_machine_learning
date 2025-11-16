# Interaktives Tic-Tac-Toe Spiel

## 🎮 Spielmodi

Das Programm bietet 5 verschiedene Spielmodi:

### 1️⃣ Q-Learning vs Zufallsspieler
- Q-Learning Algorithmus spielt gegen Zufallsspieler
- Wählbare Anzahl an Spielen (z.B. 1000)
- Zeigt Statistiken: Siege, Niederlagen, Unentschieden

### 2️⃣ Neural Network vs Zufallsspieler
- Deep Q-Network spielt gegen Zufallsspieler
- Trainiert NN zuerst (10.000 Episoden)
- Zeigt Statistiken nach allen Spielen

### 3️⃣ Q-Learning vs Menschlicher Spieler ⭐
- **Spiele selbst gegen Q-Learning!**
- Wähle deine Farbe (X oder O)
- Interaktive Eingabe über Terminal
- Visuelles Spielfeld

### 4️⃣ Neural Network vs Menschlicher Spieler ⭐
- **Spiele selbst gegen Neural Network!**
- Trainiert NN zuerst
- Wähle deine Farbe (X oder O)
- Interaktive Eingabe

### 5️⃣ Q-Learning vs Neural Network
- Direkter Vergleich der beiden Algorithmen
- Wählbare Anzahl an Spielen
- Zeigt welcher Algorithmus besser ist

---

## 🚀 Starten

```bash
# Kompilieren
javac -cp "lib/tic_tac_toe.jar" -d target/classes src/main/java/tic_tac_toe_mi/*.java src/main/java/tic_tac_toe_mi/nn/*.java

# Starten
java -cp "target/classes:lib/tic_tac_toe.jar" tic_tac_toe_mi.InteraktivesSpiel
```

---

## 📋 Bedienung

### Hauptmenü
```
📋 HAUPTMENÜ - Wähle einen Spielmodus:

  1️⃣  Q-Learning vs Zufallsspieler
  2️⃣  Neural Network vs Zufallsspieler
  3️⃣  Q-Learning vs Menschlicher Spieler (Du!)
  4️⃣  Neural Network vs Menschlicher Spieler (Du!)
  5️⃣  Q-Learning vs Neural Network (Direktvergleich)
  0️⃣  Beenden

>
```

### Spielfeld-Eingabe (wenn du spielst)
```
  Aktuelles Spielfeld:
  ┌───┬───┬───┐
  │   │   │   │
  ├───┼───┼───┤
  │   │ X │   │
  ├───┼───┼───┤
  │   │   │   │
  └───┴───┴───┘

Gib Zeile (0-2) und Spalte (0-2) ein, z.B. '1 1' für Mitte:
>
```

**Koordinaten-System:**
```
     Spalte
     0 1 2
   ┌───────
 0 │ 0,0  0,1  0,2
Z 1 │ 1,0  1,1  1,2
e 2 │ 2,0  2,1  2,2
i
l
e
```

**Beispiel-Eingaben:**
- `0 0` - Oben links
- `1 1` - Mitte
- `2 2` - Unten rechts
- `exit` - Spiel beenden

---

## 📊 Beispiel-Output

### Q-Learning vs Zufallsspieler (1000 Spiele)
```
╔══════════════════════════════════════════════════╗
║  Q-Learning vs Zufallsspieler                    ║
╚══════════════════════════════════════════════════╝

Spiele 1,000 Partien...

Spiel  100 | Siege:   73 | Siegrate: 73.0%
Spiel  200 | Siege:  147 | Siegrate: 73.5%
...
Spiel 1000 | Siege:  736 | Siegrate: 73.6%

📊 ENDERGEBNIS:
Siege:         736 (73.6%)
Niederlagen:   214 (21.4%)
Unentschieden:  50 (5.0%)
```

### Q-Learning vs Du (Interaktiv)
```
╔══════════════════════════════════════════════════╗
║  Q-Learning vs Menschlicher Spieler              ║
╚══════════════════════════════════════════════════╝

Dein Name: Johannes

Wähle deine Farbe:
1) X (du fängst an)
2) O (Q-Learning fängt an)
> 1

Spiel startet! (Gib 'exit' ein um abzubrechen)

  Aktuelles Spielfeld:
  ┌───┬───┬───┐
  │   │   │   │
  ├───┼───┼───┤
  │   │   │   │
  ├───┼───┼───┤
  │   │   │   │
  └───┴───┴───┘

Johannes (Kreuz), wähle dein Feld:
Gib Zeile (0-2) und Spalte (0-2) ein, z.B. '1 1' für Mitte:
> 1 1

Q-Learning denkt nach...
Q-Learning wählt: Zeile 0, Spalte 0

  Aktuelles Spielfeld:
  ┌───┬───┬───┐
  │ O │   │   │
  ├───┼───┼───┤
  │   │ X │   │
  ├───┼───┼───┤
  │   │   │   │
  └───┴───┴───┘

...
```

### Q-Learning vs Neural Network (500 Spiele)
```
╔══════════════════════════════════════════════════╗
║  Q-Learning vs Neural Network                    ║
╚══════════════════════════════════════════════════╝

Trainiere Neural Network (10.000 Episoden)...
Episode 10,000 | ε=0.0100 | Buffer=10,000

✅ Training abgeschlossen in 14.52 Sekunden
   Geschwindigkeit: 689 Episoden/Sekunde

Spiele 500 Partien...

Spiel  100 | Q:   52 (52.0%) | NN:   45 (45.0%)
Spiel  200 | Q:  105 (52.5%) | NN:   91 (45.5%)
...
Spiel  500 | Q:  262 (52.4%) | NN:  226 (45.2%)

📊 ENDERGEBNIS:
Q-Learning:    262 Siege (52.4%)
Neural Net:    226 Siege (45.2%)
Unentschieden:  12 (2.4%)

🏆 Q-Learning gewinnt den Vergleich!
```

---

## 🎯 Tipps zum Spielen

### Gegen Q-Learning
- Q-Learning ist sehr stark trainiert (73.6% Win-Rate)
- Versuche die Mitte zu kontrollieren
- Blockiere Q-Learning's Gewinnchancen früh
- Erstelle "Gabeln" (zwei Gewinnmöglichkeiten gleichzeitig)

### Gegen Neural Network
- NN ist auch stark (78.7% Win-Rate)
- Nutze gleiche Strategien wie gegen Q-Learning
- NN kann manchmal überraschende Züge machen

### Allgemeine Tic-Tac-Toe Strategie
1. **Mitte nehmen** - Gibt die meisten Gewinnoptionen
2. **Ecken bevorzugen** - Mehr Gewinnlinien als Kanten
3. **Gabel erstellen** - Zwei Gewinnmöglichkeiten gleichzeitig
4. **Gegner blocken** - Verhindere Drei-in-einer-Reihe

---

## 🔧 Technische Details

### Architektur

**Q-Learning:**
- Tabular Q-Learning (HashMap)
- 5,636 entdeckte States (93.2% Coverage)
- α=0.30, γ=0.99, ε=0.40
- Training: 100k Spiele in 0.18s

**Neural Network:**
- Deep Q-Network (DQN)
- Architektur: 9 → 128 → 64 → 9
- 10,121 Parameter
- Training: 10k Episoden in ~15s

### Performance

| Metrik | Q-Learning | Neural Network |
|--------|------------|----------------|
| Win-Rate vs Random | 73.6% | 78.7% |
| Training-Zeit | 0.18s | 14.6s |
| Code-Komplexität | ~500 LOC | ~1,140 LOC |
| Interpretierbar | ✅ Ja | ❌ Nein |

---

## 🐛 Troubleshooting

### "IllegalerZugException"
- Stelle sicher dass du Koordinaten zwischen 0-2 eingibst
- Prüfe dass das Feld noch frei ist

### "NumberFormatException"
- Gib Zahlen ein, keine Buchstaben
- Format: `Zeile Leerzeichen Spalte` (z.B. `1 1`)

### Programm hängt
- Drücke `Ctrl+C` zum Abbrechen
- Oder gib `exit` ein

---

## 📚 Lernressourcen

Dieses Projekt demonstriert:
- **Reinforcement Learning** (Q-Learning)
- **Deep Reinforcement Learning** (DQN)
- **Neural Networks** (MLP mit Backpropagation)
- **Vergleich verschiedener RL-Ansätze**

Perfekt zum Lernen und Experimentieren mit KI-Algorithmen!

---

**Viel Spaß beim Spielen! 🎮**
