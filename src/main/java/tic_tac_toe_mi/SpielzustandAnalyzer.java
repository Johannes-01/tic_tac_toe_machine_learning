package tic_tac_toe_mi;

import tictactoe.Farbe;
import tictactoe.Spielfeld;

/**
 * Analysiert den aktuellen Spielzustand eines Tic-Tac-Toe Spiels.
 * Verantwortlich für: Sieg-Erkennung, Unentschieden, Spielende
 * 
 * Single Responsibility Principle: Nur Spielzustand-Analyse
 * 
 * @author johanneshaick
 */
public class SpielzustandAnalyzer {
    
    /**
     * Prüft ob das Spiel beendet ist (Sieg oder Unentschieden)
     * @param spielfeld Das zu prüfende Spielfeld
     * @return true wenn Spiel beendet
     */
    public boolean istSpielBeendet(Spielfeld spielfeld) {
        return getGewinner(spielfeld) != null || istUnentschieden(spielfeld);
    }
    
    /**
     * Ermittelt den Gewinner des aktuellen Spiels
     * @param spielfeld Das zu prüfende Spielfeld
     * @return Farbe des Gewinners oder null wenn kein Gewinner
     */
    public Farbe getGewinner(Spielfeld spielfeld) {
        if (istSieg(spielfeld, Farbe.Kreuz)) {
            return Farbe.Kreuz;
        }
        if (istSieg(spielfeld, Farbe.Kreis)) {
            return Farbe.Kreis;
        }
        return null;
    }
    
    /**
     * Prüft ob die angegebene Farbe gewonnen hat
     * @param spielfeld Das zu prüfende Spielfeld
     * @param farbe Zu prüfende Farbe
     * @return true wenn diese Farbe gewonnen hat
     */
    public boolean istSieg(Spielfeld spielfeld, Farbe farbe) {
        return istZeilenSieg(spielfeld, farbe) || 
               istSpaltenSieg(spielfeld, farbe) || 
               istDiagonaleSieg(spielfeld, farbe);
    }
    
    /**
     * Prüft ob das Spiel unentschieden ist (Spielfeld voll, kein Gewinner)
     * @param spielfeld Das zu prüfende Spielfeld
     * @return true wenn unentschieden
     */
    public boolean istUnentschieden(Spielfeld spielfeld) {
        // Wenn es einen Gewinner gibt, kein Unentschieden
        if (getGewinner(spielfeld) != null) {
            return false;
        }
        
        // Prüfe ob noch freie Felder vorhanden
        return anzahlFreieFelder(spielfeld) == 0;
    }
    
    /**
     * Zählt die Anzahl der freien Felder
     * @param spielfeld Das zu prüfende Spielfeld
     * @return Anzahl der leeren Felder
     */
    public int anzahlFreieFelder(Spielfeld spielfeld) {
        int anzahl = 0;
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                if (spielfeld.getFarbe(zeile, spalte) == Farbe.Leer) {
                    anzahl++;
                }
            }
        }
        return anzahl;
    }
    
    /**
     * Bewertet die aktuelle Spielposition für Reinforcement Learning
     * @param spielfeld Das zu bewertende Spielfeld
     * @param perspektive Die Farbe aus deren Perspektive bewertet wird
     * @return Belohnung: +1.0 für Sieg, -1.0 für Niederlage, 0.0 sonst
     */
    public double bewertePosition(Spielfeld spielfeld, Farbe perspektive) {
        Farbe gewinner = getGewinner(spielfeld);
        
        if (gewinner == null) {
            return 0.0; // Kein Gewinner, neutral oder noch nicht beendet
        }
        
        if (gewinner == perspektive) {
            return 1.0; // Sieg!
        } else {
            return -1.0; // Niederlage
        }
    }
    
    // ========================================================================
    // Private Hilfsmethoden
    // ========================================================================
    
    /**
     * Prüft alle Zeilen auf Sieg
     */
    private boolean istZeilenSieg(Spielfeld spielfeld, Farbe farbe) {
        for (int zeile = 0; zeile < 3; zeile++) {
            if (spielfeld.getFarbe(zeile, 0) == farbe &&
                spielfeld.getFarbe(zeile, 1) == farbe &&
                spielfeld.getFarbe(zeile, 2) == farbe) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Prüft alle Spalten auf Sieg
     */
    private boolean istSpaltenSieg(Spielfeld spielfeld, Farbe farbe) {
        for (int spalte = 0; spalte < 3; spalte++) {
            if (spielfeld.getFarbe(0, spalte) == farbe &&
                spielfeld.getFarbe(1, spalte) == farbe &&
                spielfeld.getFarbe(2, spalte) == farbe) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Prüft beide Diagonalen auf Sieg
     */
    private boolean istDiagonaleSieg(Spielfeld spielfeld, Farbe farbe) {
        // Diagonale oben links nach unten rechts
        boolean diagonale1 = spielfeld.getFarbe(0, 0) == farbe &&
                             spielfeld.getFarbe(1, 1) == farbe &&
                             spielfeld.getFarbe(2, 2) == farbe;
        
        // Diagonale oben rechts nach unten links
        boolean diagonale2 = spielfeld.getFarbe(0, 2) == farbe &&
                             spielfeld.getFarbe(1, 1) == farbe &&
                             spielfeld.getFarbe(2, 0) == farbe;
        
        return diagonale1 || diagonale2;
    }
}
