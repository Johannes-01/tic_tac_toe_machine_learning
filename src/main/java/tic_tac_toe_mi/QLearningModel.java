package tic_tac_toe_mi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Wrapper-Klasse für gespeicherte Q-Learning Modelle
 * Enthält Q-Tabelle plus Metadaten über Training
 * 
 * @author johanneshaick
 */
public class QLearningModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Q-Tabelle
    private Map<String, double[]> qTabelle;
    
    // Metadaten
    private String version = "1.0";
    private LocalDateTime erstelltAm;
    private int trainingSpiele;
    private double lernrate;
    private double discountFaktor;
    private double explorationRate;
    private int anzahlStates;
    
    // Optionale Statistiken
    private double durchschnittlicherQWert;
    private int maxQWerteProState;
    
    /**
     * Konstruktor für neues Modell
     */
    public QLearningModel(Map<String, double[]> qTabelle, 
                          int trainingSpiele,
                          double lernrate,
                          double discountFaktor,
                          double explorationRate) {
        this.qTabelle = qTabelle;
        this.erstelltAm = LocalDateTime.now();
        this.trainingSpiele = trainingSpiele;
        this.lernrate = lernrate;
        this.discountFaktor = discountFaktor;
        this.explorationRate = explorationRate;
        this.anzahlStates = qTabelle.size();
        this.maxQWerteProState = qTabelle.values().stream()
                                         .findFirst()
                                         .map(arr -> arr.length)
                                         .orElse(9);
        
        // Berechne durchschnittlichen Q-Wert
        berechneDurchschnittlicherQWert();
    }
    
    /**
     * Berechnet den durchschnittlichen Q-Wert über alle States
     */
    private void berechneDurchschnittlicherQWert() {
        if (qTabelle.isEmpty()) {
            durchschnittlicherQWert = 0.0;
            return;
        }
        
        double summe = 0.0;
        int anzahl = 0;
        
        for (double[] qWerte : qTabelle.values()) {
            for (double q : qWerte) {
                summe += Math.abs(q);
                anzahl++;
            }
        }
        
        durchschnittlicherQWert = anzahl > 0 ? summe / anzahl : 0.0;
    }
    
    // ========================================================================
    // Getter
    // ========================================================================
    
    public Map<String, double[]> getQTabelle() {
        return qTabelle;
    }
    
    public String getVersion() {
        return version;
    }
    
    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }
    
    public int getTrainingSpiele() {
        return trainingSpiele;
    }
    
    public double getLernrate() {
        return lernrate;
    }
    
    public double getDiscountFaktor() {
        return discountFaktor;
    }
    
    public double getExplorationRate() {
        return explorationRate;
    }
    
    public int getAnzahlStates() {
        return anzahlStates;
    }
    
    public double getDurchschnittlicherQWert() {
        return durchschnittlicherQWert;
    }
    
    public int getMaxQWerteProState() {
        return maxQWerteProState;
    }
    
    /**
     * Gibt eine formatierte Beschreibung des Modells zurück
     */
    @Override
    public String toString() {
        return String.format(
            "Q-Learning Modell v%s\n" +
            "Erstellt: %s\n" +
            "Training: %d Spiele\n" +
            "Parameter: α=%.2f, γ=%.2f, ε=%.2f\n" +
            "States: %d\n" +
            "Ø Q-Wert: %.4f",
            version,
            erstelltAm,
            trainingSpiele,
            lernrate, discountFaktor, explorationRate,
            anzahlStates,
            durchschnittlicherQWert
        );
    }
}
