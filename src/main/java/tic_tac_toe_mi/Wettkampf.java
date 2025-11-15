package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.spieler.ILernenderSpieler;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

/**
 * Wettkampf-Klasse für Tic-Tac-Toe Reinforcement Learning
 * 
 * Testet den lernenden Spieler vor und nach dem Training gegen einen Zufallsspieler.
 * Konfiguration entspricht der Original-Wettkampf.java aus tic_tac_toe.jar
 * 
 * @author johanneshaick
 */
public class Wettkampf {

    public static void main(String[] args) {
        ISpieler spieler1 = new Zufallsspieler("Zufall");
        
        // Unser eigener lernender Spieler
        ILernenderSpieler spieler2 = new Spieler("Lernender Spieler", 0.1, 0.9, 0.3);
        
        TicTacToe spiel = new TicTacToe();
        ISpieler gewinner;
        int gewinne1 = 0;
        int gewinne2 = 0;
        
        // ====================================================================
        // VOR DEM TRAINING
        // ====================================================================
        System.out.println("Vor dem Training");
        System.out.println(spieler1.getName() + " gegen " + spieler2.getName());
        System.out.println("=======================================================");
        
        for (long i = 0; i < 1000; i++) {
            // Spiel 1: Spieler1 startet
            gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, false);
            if (gewinner == spieler1) {
                gewinne1++;
            } else {
                if (gewinner == spieler2)
                    gewinne2++;
            }
            
            // Spiel 2: Spieler2 startet
            gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, false);
            if (gewinner == spieler1) {
                gewinne1++;
            } else {
                if (gewinner == spieler2)
                    gewinne2++;
            }
        }
        
        System.out.println("Gewinne " + spieler1.getName() + ": " + gewinne1 + 
                         ". Gewinne " + spieler2.getName() + ": " + gewinne2);
        System.out.println();
        
        // ====================================================================
        // TRAINING
        // ====================================================================
        gewinne1 = 0;
        gewinne2 = 0;
        
        System.out.println("Starte Training mit 200000 Iterationen. Bitte haben Sie etwas Geduld!");
        long starttime = System.currentTimeMillis();
        spieler2.trainieren(new AbbruchNachIterationen(200000));
        long endtime = System.currentTimeMillis();
        System.out.println("Training beendet. Gesamtdauer in Sekunden: " + ((endtime - starttime) / 1000));
        
        // ====================================================================
        // NACH DEM TRAINING
        // ====================================================================
        System.out.println(spieler1.getName() + " gegen " + spieler2.getName());
        System.out.println("=======================================================");
        
        // Training deaktivieren für Tests
        if (spieler2 instanceof Spieler) {
            ((Spieler) spieler2).setTrainingsmodus(false);
            ((Spieler) spieler2).setExplorationRate(0.1); // Weniger Exploration
        }
        
        for (long i = 0; i < 1000; i++) {
            // Spiel 1: Spieler1 startet
            gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, false);
            if (gewinner == spieler1) {
                gewinne1++;
            } else {
                if (gewinner == spieler2)
                    gewinne2++;
            }
            
            // Spiel 2: Spieler2 startet
            gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, false);
            if (gewinner == spieler1) {
                gewinne1++;
            } else {
                if (gewinner == spieler2)
                    gewinne2++;
            }
        }
        
        System.out.println("Gewinne " + spieler1.getName() + ": " + gewinne1 + 
                         ". Gewinne " + spieler2.getName() + ": " + gewinne2);
        System.out.println();
        
        // ====================================================================
        // DEBUG-SPIELE
        // ====================================================================
        System.out.println("Ein Einzelspiel im DEBUG-Modus, lernender Spieler startet mit X");
        System.out.println("===============================================================");
        gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, true);
        System.out.println("Gewonnen hat: " + (gewinner != null ? gewinner.getName() : "Unentschieden"));
        System.out.println();
        
        System.out.println("Ein Einzelspiel im DEBUG-Modus, lernender Spieler zweiter mit O");
        System.out.println("===============================================================");
        gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, true);
        System.out.println("Gewonnen hat: " + (gewinner != null ? gewinner.getName() : "Unentschieden"));
    }
}
