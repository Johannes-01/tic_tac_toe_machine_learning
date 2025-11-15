package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Systematischer Parameter-Test für Q-Learning
 * 
 * Testet verschiedene Kombinationen von:
 * - α (Lernrate): Wie schnell neue Erfahrungen alte überschreiben
 * - γ (Discount): Wie wichtig zukünftige Belohnungen sind
 * - ε (Exploration): Wie oft zufällig vs. optimal gespielt wird
 * 
 * Ziel: Optimale Parameter finden und Daten für Dokumentation sammeln
 * 
 * @author johanneshaick
 */
public class ParameterTest {
    
    // Test-Konfiguration
    private static final int TRAINING_SPIELE = 50000;
    private static final int TEST_SPIELE = 1000;
    
    // Ergebnisse sammeln
    private static List<TestErgebnis> ergebnisse = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Q-Learning Parameter Optimization");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Training: " + TRAINING_SPIELE + " Spiele");
        System.out.println("Testing:  " + TEST_SPIELE + " Spiele pro Konfiguration");
        System.out.println();
        
        long startZeit = System.currentTimeMillis();
        
        // Parameter-Grid definieren
        double[] lernraten = {0.05, 0.1, 0.2, 0.3};
        double[] discounts = {0.8, 0.9, 0.95, 0.99};
        double[] explorations = {0.1, 0.2, 0.3, 0.4};
        
        int totalTests = lernraten.length * discounts.length * explorations.length;
        int currentTest = 0;
        
        System.out.println("Grid-Search: " + totalTests + " Konfigurationen");
        System.out.println();
        
        // Teste alle Kombinationen
        for (double alpha : lernraten) {
            for (double gamma : discounts) {
                for (double epsilon : explorations) {
                    currentTest++;
                    System.out.printf("[%3d/%3d] α=%.2f, γ=%.2f, ε=%.2f ... ", 
                        currentTest, totalTests, alpha, gamma, epsilon);
                    
                    TestErgebnis ergebnis = testeParameter(alpha, gamma, epsilon);
                    ergebnisse.add(ergebnis);
                    
                    System.out.printf("Siegrate: %.1f%% | States: %d | Zeit: %.2fs%n",
                        ergebnis.siegrate * 100,
                        ergebnis.anzahlStates,
                        ergebnis.trainingDauer);
                }
            }
        }
        
        long endZeit = System.currentTimeMillis();
        double gesamtDauer = (endZeit - startZeit) / 1000.0;
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Ergebnisse");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.printf("Gesamtdauer: %.2f Sekunden%n", gesamtDauer);
        System.out.println();
        
        // Finde beste Konfiguration
        TestErgebnis beste = findeBeste();
        System.out.println("🏆 Beste Konfiguration:");
        System.out.printf("   α=%.2f, γ=%.2f, ε=%.2f%n", beste.alpha, beste.gamma, beste.epsilon);
        System.out.printf("   Siegrate: %.1f%%%n", beste.siegrate * 100);
        System.out.printf("   States:   %d%n", beste.anzahlStates);
        System.out.println();
        
        // Exportiere als CSV
        exportiereCSV("results/parameter_test.csv");
        
        // Zeige Top 5
        zeigeTop5();
        
        System.out.println();
        System.out.println("📊 Daten gespeichert in: results/parameter_test.csv");
        System.out.println("💡 Öffne in Excel/Google Sheets für Analyse!");
    }
    
    /**
     * Testet eine Parameter-Kombination
     */
    private static TestErgebnis testeParameter(double alpha, double gamma, double epsilon) {
        // Erstelle Spieler mit spezifischen Parametern
        Spieler spieler = new Spieler("Test", alpha, gamma, epsilon);
        
        // Training
        long trainStart = System.currentTimeMillis();
        spieler.trainieren(new AbbruchNachIterationen(TRAINING_SPIELE));
        long trainEnd = System.currentTimeMillis();
        double trainingDauer = (trainEnd - trainStart) / 1000.0;
        
        // Deaktiviere Training für Tests
        spieler.setTrainingsmodus(false);
        spieler.setExplorationRate(0.1); // Weniger Exploration im Test
        
        // Teste gegen Zufallsspieler
        TicTacToe spiel = new TicTacToe();
        ISpieler gegner = new Zufallsspieler("Zufall");
        
        int siege = 0;
        int niederlagen = 0;
        int unentschieden = 0;
        
        for (int i = 0; i < TEST_SPIELE; i++) {
            // Spieler startet
            ISpieler gewinner = spiel.neuesSpiel(spieler, gegner, 150, false);
            if (gewinner == spieler) {
                siege++;
            } else if (gewinner == gegner) {
                niederlagen++;
            } else {
                unentschieden++;
            }
        }
        
        double siegrate = (double) siege / TEST_SPIELE;
        int anzahlStates = spieler.getQLearningAgent().getAnzahlStates();
        
        return new TestErgebnis(
            alpha, gamma, epsilon,
            siegrate, siege, niederlagen, unentschieden,
            anzahlStates, trainingDauer
        );
    }
    
    /**
     * Findet die beste Konfiguration (höchste Siegrate)
     */
    private static TestErgebnis findeBeste() {
        TestErgebnis beste = ergebnisse.get(0);
        for (TestErgebnis e : ergebnisse) {
            if (e.siegrate > beste.siegrate) {
                beste = e;
            }
        }
        return beste;
    }
    
    /**
     * Zeigt Top 5 Konfigurationen
     */
    private static void zeigeTop5() {
        System.out.println("📈 Top 5 Konfigurationen:");
        System.out.println();
        System.out.println("Rang | α    | γ    | ε    | Siegrate | States | Zeit");
        System.out.println("-----|------|------|------|----------|--------|------");
        
        // Sortiere nach Siegrate
        List<TestErgebnis> sortiert = new ArrayList<>(ergebnisse);
        sortiert.sort((a, b) -> Double.compare(b.siegrate, a.siegrate));
        
        for (int i = 0; i < Math.min(5, sortiert.size()); i++) {
            TestErgebnis e = sortiert.get(i);
            System.out.printf("%4d | %.2f | %.2f | %.2f | %6.1f%% | %6d | %.2fs%n",
                i + 1, e.alpha, e.gamma, e.epsilon,
                e.siegrate * 100, e.anzahlStates, e.trainingDauer);
        }
    }
    
    /**
     * Exportiert Ergebnisse als CSV
     */
    private static void exportiereCSV(String dateiname) {
        try {
            // Erstelle Verzeichnis
            new java.io.File("results").mkdirs();
            
            FileWriter writer = new FileWriter(dateiname);
            
            // Header
            writer.write("alpha,gamma,epsilon,siegrate,siege,niederlagen,unentschieden,states,training_dauer_sekunden\n");
            
            // Daten
            for (TestErgebnis e : ergebnisse) {
                writer.write(String.format(java.util.Locale.US,
                    "%.2f,%.2f,%.2f,%.4f,%d,%d,%d,%d,%.3f\n",
                    e.alpha, e.gamma, e.epsilon,
                    e.siegrate, e.siege, e.niederlagen, e.unentschieden,
                    e.anzahlStates, e.trainingDauer));
            }
            
            writer.close();
            System.out.println("✓ CSV exportiert: " + dateiname);
        } catch (IOException e) {
            System.err.println("Fehler beim CSV-Export: " + e.getMessage());
        }
    }
    
    /**
     * Datenklasse für Test-Ergebnisse
     */
    private static class TestErgebnis {
        double alpha, gamma, epsilon;
        double siegrate;
        int siege, niederlagen, unentschieden;
        int anzahlStates;
        double trainingDauer;
        
        TestErgebnis(double alpha, double gamma, double epsilon,
                     double siegrate, int siege, int niederlagen, int unentschieden,
                     int anzahlStates, double trainingDauer) {
            this.alpha = alpha;
            this.gamma = gamma;
            this.epsilon = epsilon;
            this.siegrate = siegrate;
            this.siege = siege;
            this.niederlagen = niederlagen;
            this.unentschieden = unentschieden;
            this.anzahlStates = anzahlStates;
            this.trainingDauer = trainingDauer;
        }
    }
}
