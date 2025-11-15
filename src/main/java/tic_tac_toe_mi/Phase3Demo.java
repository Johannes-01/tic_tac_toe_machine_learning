package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

/**
 * Demo und Test für Phase 3: Q-Learning
 * Demonstriert die Q-Learning Funktionalität
 * 
 * @author johanneshaick
 */
public class Phase3Demo {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Phase 3: Q-Learning - Demo");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        // Erstelle Spieler
        Spieler rlSpieler = new Spieler("Q-Learner", 0.1, 0.9, 0.3);
        ISpieler zufallsSpieler = new Zufallsspieler("Zufall");
        TicTacToe spiel = new TicTacToe();
        
        // Test 1: Q-Learning Parameter
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  Test 1: Q-Learning Parameter                ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println("Lernrate (α):        " + rlSpieler.getLernrate());
        System.out.println("Discount (γ):        " + rlSpieler.getDiscountFaktor());
        System.out.println("Exploration (ε):     " + rlSpieler.getExplorationRate());
        System.out.println("Q-Tabelle States:    " + rlSpieler.getQLearningAgent().getAnzahlStates());
        System.out.println("Trainingsmodus:      " + rlSpieler.istTrainingsmodus());
        System.out.println();
        
        // Test 2: Spiele OHNE Training (nur Exploration)
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  Test 2: Spiele ohne Training (100 Spiele)   ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        rlSpieler.setTrainingsmodus(false); // Kein Learning
        int[] ergebnis1 = spieleSerie(spiel, rlSpieler, zufallsSpieler, 100, false);
        System.out.println("Siege RL-Spieler:    " + ergebnis1[0]);
        System.out.println("Siege Zufallsspieler: " + ergebnis1[1]);
        System.out.println("Unentschieden:       " + ergebnis1[2]);
        System.out.println("Q-Tabelle States:    " + rlSpieler.getQLearningAgent().getAnzahlStates());
        System.out.println();
        
        // Test 3: Training aktivieren
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  Test 3: Training aktiviert (1000 Spiele)    ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        rlSpieler.setTrainingsmodus(true); // Learning EIN
        System.out.println("Training läuft...");
        long startZeit = System.currentTimeMillis();
        int[] ergebnis2 = spieleSerie(spiel, rlSpieler, zufallsSpieler, 1000, false);
        long dauer = System.currentTimeMillis() - startZeit;
        System.out.println("Training abgeschlossen in " + (dauer / 1000.0) + " Sekunden");
        System.out.println("Siege RL-Spieler:    " + ergebnis2[0]);
        System.out.println("Siege Zufallsspieler: " + ergebnis2[1]);
        System.out.println("Unentschieden:       " + ergebnis2[2]);
        System.out.println("Q-Tabelle States:    " + rlSpieler.getQLearningAgent().getAnzahlStates());
        System.out.println();
        
        // Test 4: Nach dem Training testen (ohne Learning)
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  Test 4: Nach Training (100 Testspiele)      ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        rlSpieler.setTrainingsmodus(false); // Learning AUS
        rlSpieler.setExplorationRate(0.1); // Weniger Exploration
        int[] ergebnis3 = spieleSerie(spiel, rlSpieler, zufallsSpieler, 100, false);
        System.out.println("Siege RL-Spieler:    " + ergebnis3[0]);
        System.out.println("Siege Zufallsspieler: " + ergebnis3[1]);
        System.out.println("Unentschieden:       " + ergebnis3[2]);
        System.out.println();
        
        // Vergleich
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  Vergleich: Vor vs. Nach Training            ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        double siegrate1 = (double) ergebnis1[0] / 100 * 100;
        double siegrate3 = (double) ergebnis3[0] / 100 * 100;
        double verbesserung = siegrate3 - siegrate1;
        
        System.out.println("Vor Training:   " + siegrate1 + "% Siegrate");
        System.out.println("Nach Training:  " + siegrate3 + "% Siegrate");
        System.out.println("Verbesserung:   " + (verbesserung > 0 ? "+" : "") + verbesserung + "%");
        System.out.println();
        
        if (verbesserung > 5) {
            System.out.println("✅ Q-Learning funktioniert! Signifikante Verbesserung!");
        } else if (verbesserung > 0) {
            System.out.println("⚠️  Leichte Verbesserung - mehr Training nötig");
        } else {
            System.out.println("❌ Keine Verbesserung - Parameter anpassen");
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Phase 3 abgeschlossen!");
        System.out.println("═══════════════════════════════════════════════════════");
    }
    
    /**
     * Spielt eine Serie von Spielen und zählt Ergebnisse
     * @return int[] {Siege Spieler1, Siege Spieler2, Unentschieden}
     */
    private static int[] spieleSerie(TicTacToe spiel, ISpieler spieler1, ISpieler spieler2, int anzahl, boolean debug) {
        int siege1 = 0;
        int siege2 = 0;
        int unentschieden = 0;
        
        for (int i = 0; i < anzahl; i++) {
            // Abwechselnd starten
            ISpieler gewinner;
            if (i % 2 == 0) {
                gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, debug);
            } else {
                gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, debug);
            }
            
            if (gewinner == null) {
                unentschieden++;
            } else if (gewinner == spieler1) {
                siege1++;
            } else {
                siege2++;
            }
        }
        
        return new int[]{siege1, siege2, unentschieden};
    }
}
