package tic_tac_toe_mi;

import java.io.*;
import java.util.*;

import tictactoe.Farbe;
import tictactoe.Spielfeld;
import tictactoe.Zug;

/**
 * Q-Learning Agent für Tic-Tac-Toe.
 * Verantwortlich für: Q-Tabelle, Lernalgorithmus, Zugauswahl
 * 
 * Single Responsibility Principle: Nur Q-Learning Logik
 * 
 * @author johanneshaick
 */
public class QLearningAgent {
    
    // Q-Tabelle: State → Q-Werte für alle 9 Aktionen (Zeile*3 + Spalte)
    private Map<String, double[]> qTabelle;
    
    // Reinforcement Learning Parameter
    private double lernrate;           // Alpha: Wie schnell wird gelernt
    private double discountFaktor;     // Gamma: Bedeutung zukünftiger Belohnungen
    private double explorationRate;    // Epsilon: Exploration vs. Exploitation
    
    // Hilfsobjekte
    private Random random;
    private SpielzustandKonverter konverter;
    private SpielzustandAnalyzer analyzer;
    
    /**
     * Erstellt einen neuen Q-Learning Agent mit Standard-Parametern
     */
    public QLearningAgent() {
        this(0.1, 0.9, 0.3);
    }
    
    /**
     * Erstellt einen neuen Q-Learning Agent mit benutzerdefinierten Parametern
     * 
     * @param lernrate Alpha-Wert (typisch: 0.1)
     * @param discountFaktor Gamma-Wert (typisch: 0.9)
     * @param explorationRate Epsilon-Wert (typisch: 0.3)
     */
    public QLearningAgent(double lernrate, double discountFaktor, double explorationRate) {
        this.qTabelle = new HashMap<>();
        this.lernrate = lernrate;
        this.discountFaktor = discountFaktor;
        this.explorationRate = explorationRate;
        this.random = new Random();
        this.konverter = new SpielzustandKonverter();
        this.analyzer = new SpielzustandAnalyzer();
    }
    
    /**
     * Wählt den besten Zug basierend auf Q-Werten (Epsilon-Greedy)
     * 
     * @param spielfeld Aktuelles Spielfeld
     * @param meineFarbe Farbe des Spielers
     * @param moeglicheZuege Liste möglicher Züge
     * @return Der gewählte Zug
     */
    public Zug waehleZug(Spielfeld spielfeld, Farbe meineFarbe, List<Zug> moeglicheZuege) {
        if (moeglicheZuege.isEmpty()) {
            throw new IllegalArgumentException("Keine möglichen Züge vorhanden!");
        }
        
        // Epsilon-Greedy: Mit Wahrscheinlichkeit epsilon explorieren
        if (random.nextDouble() < explorationRate) {
            // EXPLORATION: Zufälliger Zug
            return moeglicheZuege.get(random.nextInt(moeglicheZuege.size()));
        } else {
            // EXPLOITATION: Bester Zug basierend auf Q-Werten
            String state = konverter.zuStringNormalisiert(spielfeld, meineFarbe);
            double[] qWerte = getQWerte(state);
            
            // Finde besten Zug aus möglichen Zügen
            Zug besterZug = moeglicheZuege.get(0);
            double maxQ = qWerte[zugZuAktion(besterZug)];
            
            for (Zug zug : moeglicheZuege) {
                int aktion = zugZuAktion(zug);
                if (qWerte[aktion] > maxQ) {
                    maxQ = qWerte[aktion];
                    besterZug = zug;
                }
            }
            
            return besterZug;
        }
    }
    
    /**
     * Aktualisiert Q-Werte basierend auf dem erhaltenen Reward
     * Q-Learning Update-Regel: Q(s,a) ← Q(s,a) + α[r + γ·max(Q(s',a')) - Q(s,a)]
     * 
     * @param state Aktueller Zustand (als String)
     * @param aktion Durchgeführte Aktion (0-8)
     * @param reward Erhaltene Belohnung
     * @param naechsterState Nächster Zustand (als String, null bei Spielende)
     * @param istTerminal Ob der nächste Zustand ein End-Zustand ist
     */
    public void lernen(String state, int aktion, double reward, String naechsterState, boolean istTerminal) {
        double[] qWerte = getQWerte(state);
        double alterQWert = qWerte[aktion];
        
        double neuerQWert;
        if (istTerminal) {
            // Terminal State: Nur immediate reward
            neuerQWert = alterQWert + lernrate * (reward - alterQWert);
        } else {
            // Non-Terminal: Berücksichtige zukünftige Rewards
            double maxNaechsterQ = getMaxQWert(naechsterState);
            neuerQWert = alterQWert + lernrate * (reward + discountFaktor * maxNaechsterQ - alterQWert);
        }
        
        qWerte[aktion] = neuerQWert;
    }
    
    /**
     * Findet den maximalen Q-Wert für einen gegebenen Zustand
     * 
     * @param state Der Spielzustand
     * @return Maximaler Q-Wert
     */
    private double getMaxQWert(String state) {
        double[] qWerte = getQWerte(state);
        double maxQ = qWerte[0];
        for (int i = 1; i < qWerte.length; i++) {
            if (qWerte[i] > maxQ) {
                maxQ = qWerte[i];
            }
        }
        return maxQ;
    }
    
    /**
     * Gibt Q-Werte für einen Zustand zurück
     * Initialisiert mit 0, falls Zustand noch nicht bekannt
     * 
     * @param state Der Spielzustand als String
     * @return Array mit 9 Q-Werten (ein Wert pro Feld)
     */
    public double[] getQWerte(String state) {
        return qTabelle.computeIfAbsent(state, k -> new double[9]);
    }
    
    /**
     * Findet die beste Aktion für einen gegebenen Zustand
     * 
     * @param state Der Spielzustand
     * @param moeglicheAktionen Liste gültiger Aktionen
     * @return Index der besten Aktion
     */
    public int besteAktion(String state, List<Integer> moeglicheAktionen) {
        double[] qWerte = getQWerte(state);
        int besteAktion = moeglicheAktionen.get(0);
        double maxQ = qWerte[besteAktion];
        
        for (int aktion : moeglicheAktionen) {
            if (qWerte[aktion] > maxQ) {
                maxQ = qWerte[aktion];
                besteAktion = aktion;
            }
        }
        
        return besteAktion;
    }
    
    /**
     * Konvertiert Zug zu Aktions-Index (0-8)
     */
    public int zugZuAktion(Zug zug) {
        return zug.getZeile() * 3 + zug.getSpalte();
    }
    
    /**
     * Konvertiert Aktions-Index zu Zug
     */
    public Zug aktionZuZug(int aktion) {
        return new Zug(aktion / 3, aktion % 3);
    }
    
    /**
     * Speichert die Q-Tabelle in eine Datei (Legacy-Format)
     * 
     * @param dateiname Pfad zur Speicherdatei
     * @throws IOException Bei Schreibfehlern
     */
    public void speichern(String dateiname) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dateiname))) {
            oos.writeObject(qTabelle);
        }
    }
    
    /**
     * Speichert die Q-Tabelle mit Metadaten
     * 
     * @param dateiname Pfad zur Speicherdatei
     * @param trainingSpiele Anzahl der Trainingsspiele
     * @throws IOException Bei Schreibfehlern
     */
    public void speichereModell(String dateiname, int trainingSpiele) throws IOException {
        QLearningModel modell = new QLearningModel(
            qTabelle,
            trainingSpiele,
            lernrate,
            discountFaktor,
            explorationRate
        );
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dateiname))) {
            oos.writeObject(modell);
        }
        
        System.out.println("Modell gespeichert:");
        System.out.println(modell);
    }
    
    /**
     * Lädt die Q-Tabelle aus einer Datei (Legacy-Format)
     * 
     * @param dateiname Pfad zur Speicherdatei
     * @throws IOException Bei Lesefehlern
     * @throws ClassNotFoundException Bei Deserializierungs-Fehlern
     */
    @SuppressWarnings("unchecked")
    public void laden(String dateiname) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dateiname))) {
            qTabelle = (Map<String, double[]>) ois.readObject();
        }
    }
    
    /**
     * Lädt ein Modell mit Metadaten
     * 
     * @param dateiname Pfad zur Speicherdatei
     * @return Das geladene Modell
     * @throws IOException Bei Lesefehlern
     * @throws ClassNotFoundException Bei Deserializierungs-Fehlern
     */
    public QLearningModel ladeModell(String dateiname) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dateiname))) {
            Object obj = ois.readObject();
            
            // Versuche als Modell zu laden
            if (obj instanceof QLearningModel) {
                QLearningModel modell = (QLearningModel) obj;
                this.qTabelle = modell.getQTabelle();
                
                // Optio Parameter übernehmen
                this.lernrate = modell.getLernrate();
                this.discountFaktor = modell.getDiscountFaktor();
                this.explorationRate = modell.getExplorationRate();
                
                System.out.println("Modell geladen:");
                System.out.println(modell);
                
                return modell;
            } else {
                // Legacy-Format: Nur Q-Tabelle
                @SuppressWarnings("unchecked")
                Map<String, double[]> legacyTabelle = (Map<String, double[]>) obj;
                this.qTabelle = legacyTabelle;
                
                System.out.println("Legacy Q-Tabelle geladen: " + qTabelle.size() + " States");
                return null;
            }
        }
    }
    
    // ========================================================================
    // Getter und Setter
    // ========================================================================
    
    public double getLernrate() {
        return lernrate;
    }
    
    public void setLernrate(double lernrate) {
        this.lernrate = lernrate;
    }
    
    public double getDiscountFaktor() {
        return discountFaktor;
    }
    
    public void setDiscountFaktor(double discountFaktor) {
        this.discountFaktor = discountFaktor;
    }
    
    public double getExplorationRate() {
        return explorationRate;
    }
    
    public void setExplorationRate(double explorationRate) {
        this.explorationRate = explorationRate;
    }
    
    public int getAnzahlStates() {
        return qTabelle.size();
    }
}
