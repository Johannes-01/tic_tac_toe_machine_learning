package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

/**
 * Finales Demo mit optimaler Konfiguration
 * 
 * Demonstriert die beste gefundene Parameter-Kombination:
 * - α = 0.30 (Lernrate)
 * - γ = 0.99 (Discount Factor)
 * - ε = 0.40 (Exploration Rate)
 * 
 * Vergleicht gegen Standard-Konfiguration und Zufallsspieler
 * 
 * @author johanneshaick
 */
public class FinalesDemo {
    
    private static final int TRAINING_SPIELE = 100000;
    private static final int TEST_SPIELE = 2000;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  FINALE DEMONSTRATION - Optimale Konfiguration");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        // Erstelle drei Spieler-Konfigurationen
        Spieler optimal = new Spieler("Optimal (α=0.30, γ=0.99, ε=0.40)", 0.30, 0.99, 0.40);
        Spieler standard = new Spieler("Standard (α=0.10, γ=0.90, ε=0.30)", 0.10, 0.90, 0.30);
        Spieler konservativ = new Spieler("Konservativ (α=0.05, γ=0.95, ε=0.30)", 0.05, 0.95, 0.30);
        
        // Training
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Phase 1: Training                                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("Training: " + TRAINING_SPIELE + " Spiele pro Konfiguration");
        System.out.println();
        
        trainiereUndZeige("Optimal", optimal);
        trainiereUndZeige("Standard", standard);
        trainiereUndZeige("Konservativ", konservativ);
        
        // Teste gegen Zufallsspieler
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Phase 2: Test gegen Zufallsspieler                   ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println("Test: " + TEST_SPIELE + " Spiele pro Konfiguration");
        System.out.println();
        
        TestErgebnis e1 = testeGegenZufall(optimal, "Optimal");
        TestErgebnis e2 = testeGegenZufall(standard, "Standard");
        TestErgebnis e3 = testeGegenZufall(konservativ, "Konservativ");
        
        // Vergleichstabelle
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Phase 3: Direkter Vergleich                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        vergleicheSpieler(optimal, standard, "Optimal vs. Standard");
        vergleicheSpieler(optimal, konservativ, "Optimal vs. Konservativ");
        vergleicheSpieler(standard, konservativ, "Standard vs. Konservativ");
        
        // Zusammenfassung
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  ZUSAMMENFASSUNG                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Siegrate gegen Zufallsspieler (" + TEST_SPIELE + " Spiele):");
        System.out.println();
        System.out.println("Konfig.      | Siege | Niederl. | Unent. | Siegrate | States");
        System.out.println("-------------|-------|----------|--------|----------|--------");
        System.out.printf("%-12s | %5d | %8d | %6d | %6.1f%% | %6d%n", 
            "Optimal", e1.siege, e1.niederlagen, e1.unentschieden, e1.siegrate * 100, e1.states);
        System.out.printf("%-12s | %5d | %8d | %6d | %6.1f%% | %6d%n", 
            "Standard", e2.siege, e2.niederlagen, e2.unentschieden, e2.siegrate * 100, e2.states);
        System.out.printf("%-12s | %5d | %8d | %6d | %6.1f%% | %6d%n", 
            "Konservativ", e3.siege, e3.niederlagen, e3.unentschieden, e3.siegrate * 100, e3.states);
        
        System.out.println();
        
        // Bestimme Gewinner
        TestErgebnis beste;
        String name;
        if (e1.siegrate >= e2.siegrate && e1.siegrate >= e3.siegrate) {
            beste = e1;
            name = "Optimal";
        } else if (e2.siegrate >= e3.siegrate) {
            beste = e2;
            name = "Standard";
        } else {
            beste = e3;
            name = "Konservativ";
        }
        
        System.out.println("🏆 Gewinner: " + name);
        System.out.printf("   Siegrate: %.1f%% (%d/%d Siege)%n", 
            beste.siegrate * 100, beste.siege, TEST_SPIELE);
        System.out.printf("   States entdeckt: %d (von ~6046 theoretisch möglichen)%n", beste.states);
        
        // Speichere beste Konfiguration
        System.out.println();
        System.out.println("💾 Speichere Modelle...");
        
        try {
            optimal.speichereModell("models/optimal_config.dat", TRAINING_SPIELE);
            optimal.exportiereAlsJSON("models/optimal_config.json", TRAINING_SPIELE);
            
            standard.speichereModell("models/standard_config.dat", TRAINING_SPIELE);
            standard.exportiereAlsJSON("models/standard_config.json", TRAINING_SPIELE);
            
            System.out.println("✓ Modelle gespeichert in models/");
        } catch (Exception e) {
            System.err.println("Fehler beim Speichern: " + e.getMessage());
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Demo abgeschlossen!");
        System.out.println("═══════════════════════════════════════════════════════");
    }
    
    /**
     * Trainiert einen Spieler und zeigt Statistiken
     */
    private static void trainiereUndZeige(String name, Spieler spieler) {
        System.out.print("► " + name + ": ");
        
        long start = System.currentTimeMillis();
        spieler.trainieren(new AbbruchNachIterationen(TRAINING_SPIELE));
        long ende = System.currentTimeMillis();
        
        double dauer = (ende - start) / 1000.0;
        int states = spieler.getQLearningAgent().getAnzahlStates();
        
        System.out.printf("%.2fs | %d States entdeckt%n", dauer, states);
    }
    
    /**
     * Testet einen Spieler gegen Zufallsspieler
     */
    private static TestErgebnis testeGegenZufall(Spieler spieler, String name) {
        // Deaktiviere Training
        spieler.setTrainingsmodus(false);
        spieler.setExplorationRate(0.1);
        
        TicTacToe spiel = new TicTacToe();
        ISpieler zufall = new Zufallsspieler("Zufall");
        
        int siege = 0;
        int niederlagen = 0;
        int unentschieden = 0;
        
        // Spieler startet
        for (int i = 0; i < TEST_SPIELE / 2; i++) {
            ISpieler gewinner = spiel.neuesSpiel(spieler, zufall, 150, false);
            if (gewinner == spieler) siege++;
            else if (gewinner == zufall) niederlagen++;
            else unentschieden++;
        }
        
        // Zufall startet
        for (int i = 0; i < TEST_SPIELE / 2; i++) {
            ISpieler gewinner = spiel.neuesSpiel(zufall, spieler, 150, false);
            if (gewinner == spieler) siege++;
            else if (gewinner == zufall) niederlagen++;
            else unentschieden++;
        }
        
        double siegrate = (double) siege / TEST_SPIELE;
        int states = spieler.getQLearningAgent().getAnzahlStates();
        
        System.out.printf("► %s: %d Siege | %d Niederlagen | %d Unentschieden (%.1f%%)%n",
            name, siege, niederlagen, unentschieden, siegrate * 100);
        
        return new TestErgebnis(siegrate, siege, niederlagen, unentschieden, states);
    }
    
    /**
     * Vergleicht zwei Spieler direkt
     */
    private static void vergleicheSpieler(Spieler sp1, Spieler sp2, String beschreibung) {
        // Deaktiviere Training
        sp1.setTrainingsmodus(false);
        sp1.setExplorationRate(0.1);
        sp2.setTrainingsmodus(false);
        sp2.setExplorationRate(0.1);
        
        TicTacToe spiel = new TicTacToe();
        
        int siege1 = 0;
        int siege2 = 0;
        int unentschieden = 0;
        
        int vergleichsSpiele = 500;
        
        for (int i = 0; i < vergleichsSpiele; i++) {
            ISpieler gewinner = spiel.neuesSpiel(sp1, sp2, 150, false);
            if (gewinner == sp1) siege1++;
            else if (gewinner == sp2) siege2++;
            else unentschieden++;
        }
        
        System.out.printf("► %s: %d-%d-%d (%.1f%% vs %.1f%%)%n",
            beschreibung, siege1, siege2, unentschieden,
            (siege1 / (double) vergleichsSpiele) * 100,
            (siege2 / (double) vergleichsSpiele) * 100);
    }
    
    /**
     * Datenklasse für Test-Ergebnisse
     */
    private static class TestErgebnis {
        double siegrate;
        int siege, niederlagen, unentschieden;
        int states;
        
        TestErgebnis(double siegrate, int siege, int niederlagen, int unentschieden, int states) {
            this.siegrate = siegrate;
            this.siege = siege;
            this.niederlagen = niederlagen;
            this.unentschieden = unentschieden;
            this.states = states;
        }
    }
}
