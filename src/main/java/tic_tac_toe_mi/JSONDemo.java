package tic_tac_toe_mi;

import tictactoe.Farbe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.TicTacToe;
import tictactoe.spieler.ISpieler;
import java.io.File;

/**
 * Demonstriert JSON Export/Import für Q-Learning Modelle
 * 
 * Vorteile von JSON:
 * - Human-readable: Kann in jedem Editor geöffnet werden
 * - Debugging-freundlich: Siehst Q-Values für jeden State
 * - Statistik-Export: Einfach in Excel/Python/R analysieren
 * - Plattformunabhängig: Nicht Java-spezifisch
 * 
 * @author johanneshaick
 */
public class JSONDemo {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  JSON Export/Import Demo");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        // Test 1: Kleines Modell exportieren (besser lesbar)
        test1KleinesModellJSON();
        
        // Test 2: Vergleich .dat vs .json Dateigröße
        test2DateigroessenVergleich();
        
        // Test 3: JSON Modell laden und testen
        test3JSONLaden();
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Demo abgeschlossen!");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("💡 Verwendung:");
        System.out.println("   // Export als JSON:");
        System.out.println("   spieler.exportiereAlsJSON(\"model.json\", 10000);");
        System.out.println();
        System.out.println("   // Import von JSON:");
        System.out.println("   spieler.ladeVonJSON(\"model.json\");");
        System.out.println();
        System.out.println("📂 Tipp: Öffne die JSON-Dateien in einem Editor!");
        System.out.println("   Du kannst die Q-Values für jeden State sehen.");
    }
    
    /**
     * Test 1: Exportiere kleines Modell als JSON (gut lesbar)
     */
    private static void test1KleinesModellJSON() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 1: Kleines Modell als JSON exportieren         ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        Spieler spieler = new Spieler("Demo");
        spieler.trainieren(new AbbruchNachIterationen(1000));
        
        // Export als JSON
        spieler.exportiereAlsJSON("models/small_model.json", 1000);
        
        // Dateiinfo
        File jsonFile = new File("models/small_model.json");
        System.out.println("📄 Datei: " + jsonFile.getAbsolutePath());
        System.out.println("📊 Größe: " + (jsonFile.length() / 1024) + " KB");
        System.out.println();
    }
    
    /**
     * Test 2: Vergleiche Dateigrößen .dat vs .json
     */
    private static void test2DateigroessenVergleich() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 2: Vergleich .dat vs .json Dateigröße          ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Trainiere verschiedene Größen
        int[] trainingsGroessen = {1000, 10000, 50000};
        
        System.out.println("Training | .dat Größe | .json Größe | Verhältnis");
        System.out.println("---------|------------|-------------|------------");
        
        for (int anzahl : trainingsGroessen) {
            Spieler spieler = new Spieler("Test_" + anzahl);
            spieler.trainieren(new AbbruchNachIterationen(anzahl));
            
            // Speichere beide Formate
            String datFile = "models/compare_" + anzahl + ".dat";
            String jsonFile = "models/compare_" + anzahl + ".json";
            
            try {
                spieler.speichereModell(datFile, anzahl);
            } catch (Exception e) {
                System.err.println("Fehler beim Speichern: " + e.getMessage());
            }
            spieler.exportiereAlsJSON(jsonFile, anzahl);
            
            // Vergleiche Größen
            long datSize = new File(datFile).length();
            long jsonSize = new File(jsonFile).length();
            double ratio = (double) jsonSize / datSize;
            
            System.out.printf("%8d | %9d | %10d | %.2fx%n", 
                anzahl, datSize, jsonSize, ratio);
        }
        
        System.out.println();
        System.out.println("💡 JSON ist größer aber human-readable!");
        System.out.println();
    }
    
    /**
     * Test 3: Lade JSON Modell und teste Funktionalität
     */
    private static void test3JSONLaden() {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 3: JSON Modell laden und testen                ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Erstelle und speichere Modell
        Spieler original = new Spieler("Original");
        original.trainieren(new AbbruchNachIterationen(5000));
        original.exportiereAlsJSON("models/test_load.json", 5000);
        
        int originalStates = original.getQLearningAgent().getAnzahlStates();
        
        // Lade von JSON
        Spieler geladen = new Spieler("Geladen");
        boolean success = geladen.ladeVonJSON("models/test_load.json");
        
        int geladeneStates = geladen.getQLearningAgent().getAnzahlStates();
        
        System.out.println();
        System.out.println("✓ Original States: " + originalStates);
        System.out.println("✓ Geladene States: " + geladeneStates);
        System.out.println("✓ Laden erfolgreich: " + success);
        System.out.println("✓ States identisch: " + (originalStates == geladeneStates));
        
        // Teste Performance mit TicTacToe
        System.out.println();
        System.out.println("Performance-Test (100 Spiele):");
        
        TicTacToe ticTacToe = new TicTacToe();
        int siegeOriginal = 0;
        int siegeGeladen = 0;
        int unentschieden = 0;
        
        for (int i = 0; i < 50; i++) {
            // Original startet
            ISpieler gewinner1 = ticTacToe.neuesSpiel(original, geladen, 150, false);
            if (gewinner1 == original) {
                siegeOriginal++;
            } else if (gewinner1 == geladen) {
                siegeGeladen++;
            } else {
                unentschieden++;
            }
            
            // Geladen startet
            ISpieler gewinner2 = ticTacToe.neuesSpiel(geladen, original, 150, false);
            if (gewinner2 == original) {
                siegeOriginal++;
            } else if (gewinner2 == geladen) {
                siegeGeladen++;
            } else {
                unentschieden++;
            }
        }
        
        System.out.println("  Original: " + siegeOriginal + " Siege");
        System.out.println("  Geladen:  " + siegeGeladen + " Siege");
        System.out.println("  Unentsch: " + unentschieden);
        System.out.println();
        
        if (Math.abs(siegeOriginal - siegeGeladen) <= 10) {
            System.out.println("✓ JSON Import/Export funktioniert korrekt!");
        } else {
            System.out.println("⚠ Warnung: Performance-Unterschied zu groß!");
        }
        
        System.out.println();
    }
}
