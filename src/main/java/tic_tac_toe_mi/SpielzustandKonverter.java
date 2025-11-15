package tic_tac_toe_mi;

import tictactoe.Farbe;
import tictactoe.Spielfeld;

/**
 * Konvertiert Spielfeld-Zustände in verschiedene Repräsentationen.
 * Verantwortlich für: String-Konvertierung, State-Keys für Q-Learning
 * 
 * Single Responsibility Principle: Nur State-Repräsentation
 * 
 * @author johanneshaick
 */
public class SpielzustandKonverter {
    
    /**
     * Konvertiert das Spielfeld in einen eindeutigen String.
     * Dieser String dient als Schlüssel für die Q-Tabelle.
     * Format: "X_O______" für ein Spielfeld mit X oben links, O in der Mitte
     * 
     * @param spielfeld Das zu konvertierende Spielfeld
     * @return String-Repräsentation des Spielfelds
     */
    public String zuString(Spielfeld spielfeld) {
        StringBuilder sb = new StringBuilder(9);
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                sb.append(farbeZuZeichen(spielfeld.getFarbe(zeile, spalte)));
            }
        }
        return sb.toString();
    }
    
    /**
     * Normalisierte State-Repräsentation aus Spieler-Perspektive.
     * Eigene Steine = 'X', Gegner = 'O', Leer = '_'
     * Dies macht das Lernen effizienter, da der Spieler aus beiden Perspektiven lernt.
     * 
     * @param spielfeld Das zu konvertierende Spielfeld
     * @param perspektive Die Farbe des Spielers
     * @return Normalisierte String-Repräsentation
     */
    public String zuStringNormalisiert(Spielfeld spielfeld, Farbe perspektive) {
        StringBuilder sb = new StringBuilder(9);
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                Farbe farbe = spielfeld.getFarbe(zeile, spalte);
                if (farbe == perspektive) {
                    sb.append('X'); // Eigener Stein
                } else if (farbe == perspektive.opposite()) {
                    sb.append('O'); // Gegnerischer Stein
                } else {
                    sb.append('_'); // Leeres Feld
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * Erstellt eine Kopie des Spielfelds.
     * Nützlich für Simulationen während des Trainings.
     * 
     * @param original Das zu kopierende Spielfeld
     * @return Neues Spielfeld-Objekt mit gleichen Werten
     */
    public Spielfeld kopiereSpielfeld(Spielfeld original) {
        Spielfeld kopie = new Spielfeld();
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                kopie.setFarbe(zeile, spalte, original.getFarbe(zeile, spalte));
            }
        }
        return kopie;
    }
    
    /**
     * Gibt das Spielfeld auf der Konsole aus (für Debugging).
     * 
     * @param spielfeld Das auszugebende Spielfeld
     */
    public void ausgeben(Spielfeld spielfeld) {
        System.out.println("┌───┬───┬───┐");
        for (int zeile = 0; zeile < 3; zeile++) {
            System.out.print("│");
            for (int spalte = 0; spalte < 3; spalte++) {
                Farbe farbe = spielfeld.getFarbe(zeile, spalte);
                System.out.print(" " + farbeZuSymbol(farbe) + " ");
                if (spalte < 2) {
                    System.out.print("│");
                }
            }
            System.out.println("│");
            if (zeile < 2) {
                System.out.println("├───┼───┼───┤");
            }
        }
        System.out.println("└───┴───┴───┘");
    }
    
    // ========================================================================
    // Private Hilfsmethoden
    // ========================================================================
    
    /**
     * Konvertiert Farbe zu Zeichen für String-Repräsentation
     */
    private char farbeZuZeichen(Farbe farbe) {
        if (farbe == Farbe.Kreuz) {
            return 'X';
        } else if (farbe == Farbe.Kreis) {
            return 'O';
        } else {
            return '_';
        }
    }
    
    /**
     * Konvertiert Farbe zu Symbol für Ausgabe
     */
    private String farbeZuSymbol(Farbe farbe) {
        if (farbe == Farbe.Kreuz) {
            return "X";
        } else if (farbe == Farbe.Kreis) {
            return "O";
        } else {
            return " ";
        }
    }
}
