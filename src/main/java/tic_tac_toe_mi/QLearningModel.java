package tic_tac_toe_mi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    
    // ========================================================================
    // JSON Export/Import
    // ========================================================================
    
    /**
     * Konvertiert das Modell zu JSON-String (für Analyse und Debugging)
     * Format ist human-readable und kann in Editoren geöffnet werden
     * 
     * @param compact true für kompakte Ausgabe, false für pretty-printed
     * @return JSON-String des Modells
     */
    public String toJSON(boolean compact) {
        StringBuilder json = new StringBuilder();
        String indent = compact ? "" : "  ";
        String newline = compact ? "" : "\n";
        
        json.append("{").append(newline);
        
        // Metadaten
        json.append(indent).append("\"metadata\": {").append(newline);
        json.append(indent).append(indent).append("\"version\": \"").append(version).append("\",").append(newline);
        json.append(indent).append(indent).append("\"created\": \"").append(erstelltAm).append("\",").append(newline);
        json.append(indent).append(indent).append("\"trainingGames\": ").append(trainingSpiele).append(",").append(newline);
        json.append(indent).append(indent).append("\"learningRate\": ").append(lernrate).append(",").append(newline);
        json.append(indent).append(indent).append("\"discountFactor\": ").append(discountFaktor).append(",").append(newline);
        json.append(indent).append(indent).append("\"explorationRate\": ").append(explorationRate).append(",").append(newline);
        json.append(indent).append(indent).append("\"stateCount\": ").append(anzahlStates).append(",").append(newline);
        json.append(indent).append(indent).append("\"avgQValue\": ").append(durchschnittlicherQWert).append(",").append(newline);
        json.append(indent).append(indent).append("\"maxActionsPerState\": ").append(maxQWerteProState).append(newline);
        json.append(indent).append("},").append(newline);
        
        // Q-Tabelle
        json.append(indent).append("\"qTable\": {").append(newline);
        
        int stateIndex = 0;
        for (Map.Entry<String, double[]> entry : qTabelle.entrySet()) {
            json.append(indent).append(indent).append("\"").append(entry.getKey()).append("\": [");
            
            double[] qValues = entry.getValue();
            for (int i = 0; i < qValues.length; i++) {
                // Verwende Punkt als Dezimaltrennzeichen (JSON-Standard)
                json.append(String.format(java.util.Locale.US, "%.6f", qValues[i]));
                if (i < qValues.length - 1) {
                    json.append(", ");
                }
            }
            
            json.append("]");
            if (stateIndex < qTabelle.size() - 1) {
                json.append(",");
            }
            json.append(newline);
            stateIndex++;
        }
        
        json.append(indent).append("}").append(newline);
        json.append("}");
        
        return json.toString();
    }
    
    /**
     * Erstellt QLearningModel aus JSON-String
     * 
     * @param json JSON-String
     * @return QLearningModel Instanz
     */
    public static QLearningModel fromJSON(String json) {
        // Einfacher manueller JSON Parser (funktioniert für unser Format)
        Map<String, double[]> qTabelle = new HashMap<>();
        
        // Extrahiere Metadaten
        int trainingSpiele = extractInt(json, "\"trainingGames\":");
        double lernrate = extractDouble(json, "\"learningRate\":");
        double discountFaktor = extractDouble(json, "\"discountFactor\":");
        double explorationRate = extractDouble(json, "\"explorationRate\":");
        
        // Extrahiere Q-Tabelle
        int qTableStart = json.indexOf("\"qTable\": {") + "\"qTable\": {".length();
        int qTableEnd = json.lastIndexOf("}");
        String qTableSection = json.substring(qTableStart, qTableEnd);
        
        // Parse jede State-Action Zeile
        String[] lines = qTableSection.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals("{") || line.equals("}")) {
                continue;
            }
            
            // Format: "state": [q1, q2, ..., q9],
            int colonIndex = line.indexOf("\":");
            if (colonIndex > 0) {
                String state = line.substring(1, colonIndex);
                
                int bracketStart = line.indexOf("[");
                int bracketEnd = line.indexOf("]");
                if (bracketStart > 0 && bracketEnd > bracketStart) {
                    String valuesStr = line.substring(bracketStart + 1, bracketEnd);
                    String[] valueStrs = valuesStr.split(",");
                    
                    double[] qValues = new double[valueStrs.length];
                    for (int i = 0; i < valueStrs.length; i++) {
                        qValues[i] = Double.parseDouble(valueStrs[i].trim());
                    }
                    
                    qTabelle.put(state, qValues);
                }
            }
        }
        
        return new QLearningModel(qTabelle, trainingSpiele, lernrate, discountFaktor, explorationRate);
    }
    
    /**
     * Hilfsmethode: Extrahiert Integer-Wert aus JSON
     */
    private static int extractInt(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) return 0;
        
        int start = keyIndex + key.length();
        int end = json.indexOf(",", start);
        if (end < 0) end = json.indexOf("}", start);
        
        String value = json.substring(start, end).trim();
        return Integer.parseInt(value);
    }
    
    /**
     * Hilfsmethode: Extrahiert Double-Wert aus JSON
     */
    private static double extractDouble(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) return 0.0;
        
        int start = keyIndex + key.length();
        int end = json.indexOf(",", start);
        if (end < 0) end = json.indexOf("}", start);
        
        String value = json.substring(start, end).trim();
        return Double.parseDouble(value);
    }
}
