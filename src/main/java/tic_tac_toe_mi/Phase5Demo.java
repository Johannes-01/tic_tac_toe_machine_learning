package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

/**
 * Demo und Test für Phase 5: Training Implementation
 * Demonstriert das Self-Play Training mit trainieren() Methode
 * 
 * @author johanneshaick
 */
public class Phase5Demo {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Phase 5: Training Implementation - Demo");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        // ====================================================================
        // TEST 1: Training mit trainieren() Methode
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 1: Self-Play Training (1000 Spiele)            ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        Spieler trainierterSpieler = new Spieler("Q-Learner", 0.1, 0.9, 0.3);
        
        // Training mit AbbruchNachIterationen
        AbbruchNachIterationen abbruch = new AbbruchNachIterationen(1000);
        boolean erfolg = trainierterSpieler.trainieren(abbruch);
        
        System.out.println("Training erfolgreich: " + erfolg);
        System.out.println();
        
        // ====================================================================
        // TEST 2: Performance VOR vs. NACH Training
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 2: Performance-Vergleich                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Untrainierter Spieler (nur zufällige Exploration)
        System.out.println("--- Untrainierter Spieler (100 Testspiele) ---");
        Spieler untrainiert = new Spieler("Untrainiert", 0.1, 0.9, 1.0); // ε=1.0 = nur Zufall
        untrainiert.setTrainingsmodus(false);
        
        TicTacToe spiel = new TicTacToe();
        ISpieler gegner = new Zufallsspieler("Zufall");
        
        int[] ergebnisUntrainiert = spieleSerie(spiel, untrainiert, gegner, 100, false);
        System.out.println("Siege:           " + ergebnisUntrainiert[0]);
        System.out.println("Niederlagen:     " + ergebnisUntrainiert[1]);
        System.out.println("Unentschieden:   " + ergebnisUntrainiert[2]);
        System.out.println("Siegrate:        " + (ergebnisUntrainiert[0] * 100.0 / 100) + "%");
        System.out.println();
        
        // Trainierter Spieler (nach Self-Play)
        System.out.println("--- Trainierter Spieler (100 Testspiele) ---");
        trainierterSpieler.setTrainingsmodus(false); // Kein weiteres Learning
        trainierterSpieler.setExplorationRate(0.1);  // Weniger Exploration
        
        int[] ergebnisTrainiert = spieleSerie(spiel, trainierterSpieler, gegner, 100, false);
        System.out.println("Siege:           " + ergebnisTrainiert[0]);
        System.out.println("Niederlagen:     " + ergebnisTrainiert[1]);
        System.out.println("Unentschieden:   " + ergebnisTrainiert[2]);
        System.out.println("Siegrate:        " + (ergebnisTrainiert[0] * 100.0 / 100) + "%");
        System.out.println();
        
        // ====================================================================
        // TEST 3: Verbesserung berechnen
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 3: Lern-Nachweis                                ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        double siegrateUntrainiert = ergebnisUntrainiert[0] * 100.0 / 100;
        double siegrateTrainiert = ergebnisTrainiert[0] * 100.0 / 100;
        double verbesserung = siegrateTrainiert - siegrateUntrainiert;
        
        System.out.println("Siegrate (untrainiert):  " + String.format("%.1f%%", siegrateUntrainiert));
        System.out.println("Siegrate (trainiert):    " + String.format("%.1f%%", siegrateTrainiert));
        System.out.println("Verbesserung:            " + (verbesserung > 0 ? "+" : "") + String.format("%.1f%%", verbesserung));
        System.out.println();
        
        if (verbesserung > 10) {
            System.out.println("✅ AUSGEZEICHNET! Signifikante Verbesserung durch Training!");
        } else if (verbesserung > 5) {
            System.out.println("✅ GUT! Deutliche Verbesserung erkennbar.");
        } else if (verbesserung > 0) {
            System.out.println("⚠️  Leichte Verbesserung - mehr Training könnte helfen.");
        } else {
            System.out.println("❌ Keine Verbesserung - Parameter überprüfen!");
        }
        
        System.out.println();
        
        // ====================================================================
        // TEST 4: Unterschiedliche Trainingsgrößen
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 4: Trainingsgröße-Vergleich                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        int[] trainingsGroessen = {100, 500, 2000};
        
        for (int groesse : trainingsGroessen) {
            System.out.println("--- Training mit " + groesse + " Spielen ---");
            Spieler testSpieler = new Spieler("Test-" + groesse, 0.1, 0.9, 0.3);
            
            // Training
            AbbruchNachIterationen testAbbruch = new AbbruchNachIterationen(groesse);
            testSpieler.trainieren(testAbbruch);
            
            // Test
            testSpieler.setTrainingsmodus(false);
            testSpieler.setExplorationRate(0.1);
            int[] ergebnis = spieleSerie(spiel, testSpieler, gegner, 100, false);
            
            double siegrate = ergebnis[0] * 100.0 / 100;
            System.out.println("Siegrate nach Training: " + String.format("%.1f%%", siegrate));
            System.out.println("Q-Tabelle States:       " + testSpieler.getQLearningAgent().getAnzahlStates());
            System.out.println();
        }
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Phase 5 abgeschlossen!");
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
