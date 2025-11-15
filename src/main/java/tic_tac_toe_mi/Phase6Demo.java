package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

import java.io.File;
import java.io.IOException;

/**
 * Demo und Test für Phase 6: Model Persistence
 * Demonstriert Speichern und Laden von trainierten Modellen
 * 
 * @author johanneshaick
 */
public class Phase6Demo {
    
    private static final String MODEL_DIR = "models/";
    private static final String MODEL_FILE = MODEL_DIR + "trained_200k.dat";
    
    public static void main(String[] args) throws IOException {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Phase 6: Model Persistence - Demo");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        // ====================================================================
        // TEST 1: Training und Speichern
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 1: Training und Modell speichern               ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Lösche altes Modell falls vorhanden
        File modelFile = new File(MODEL_FILE);
        if (modelFile.exists()) {
            modelFile.delete();
            System.out.println("Altes Modell gelöscht");
        }
        
        // Trainiere neuen Spieler
        System.out.println("Trainiere neuen Spieler...");
        Spieler trainierterSpieler = new Spieler("Trainiert", 0.1, 0.9, 0.3);
        
        int trainingSpiele = 200000;
        AbbruchNachIterationen abbruch = new AbbruchNachIterationen(trainingSpiele);
        trainierterSpieler.trainieren(abbruch);
        
        // Speichere Modell
        System.out.println();
        System.out.println("Speichere Modell nach " + trainingSpiele + " Spielen...");
        trainierterSpieler.speichereModell(MODEL_FILE, trainingSpiele);
        
        // Datei-Info
        System.out.println();
        System.out.println("📁 Modell-Datei:");
        System.out.println("   Pfad:   " + modelFile.getAbsolutePath());
        System.out.println("   Größe:  " + (modelFile.length() / 1024) + " KB");
        System.out.println("   Exists: " + modelFile.exists());
        System.out.println();
        
        // ====================================================================
        // TEST 2: Modell laden und verwenden
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 2: Modell laden und testen                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Neuen Spieler mit geladenem Modell erstellen
        System.out.println("Erstelle neuen Spieler mit geladenem Modell...");
        Spieler geladenerSpieler = new Spieler("Geladen", MODEL_FILE);
        
        // Deaktiviere Training für Tests
        geladenerSpieler.setTrainingsmodus(false);
        geladenerSpieler.setExplorationRate(0.1);
        
        System.out.println();
        System.out.println("Geladener Spieler:");
        System.out.println("  States in Q-Tabelle: " + geladenerSpieler.getQLearningAgent().getAnzahlStates());
        System.out.println("  Lernrate:            " + geladenerSpieler.getLernrate());
        System.out.println("  Discount:            " + geladenerSpieler.getDiscountFaktor());
        System.out.println("  Exploration:         " + geladenerSpieler.getExplorationRate());
        System.out.println();
        
        // ====================================================================
        // TEST 3: Vergleich Original vs. Geladen
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 3: Vergleich Original vs. Geladen              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        TicTacToe spiel = new TicTacToe();
        ISpieler gegner = new Zufallsspieler("Zufall");
        
        // Teste beide Spieler
        trainierterSpieler.setTrainingsmodus(false);
        trainierterSpieler.setExplorationRate(0.1);
        
        System.out.println("Teste Original-Spieler (100 Spiele)...");
        int[] ergebnisOriginal = spieleSerie(spiel, trainierterSpieler, gegner, 100);
        
        System.out.println("Teste Geladenen-Spieler (100 Spiele)...");
        int[] ergebnisGeladen = spieleSerie(spiel, geladenerSpieler, gegner, 100);
        
        System.out.println();
        System.out.println("Ergebnisse Original:    " + ergebnisOriginal[0] + " Siege (" + ergebnisOriginal[0] + "%)");
        System.out.println("Ergebnisse Geladen:     " + ergebnisGeladen[0] + " Siege (" + ergebnisGeladen[0] + "%)");
        System.out.println();
        
        if (Math.abs(ergebnisOriginal[0] - ergebnisGeladen[0]) <= 5) {
            System.out.println("✅ SUCCESS: Original und Geladen spielen gleich gut!");
            System.out.println("   Model Persistence funktioniert korrekt!");
        } else {
            System.out.println("⚠️  WARNUNG: Unterschied in der Performance!");
        }
        
        System.out.println();
        
        // ====================================================================
        // TEST 4: Untrainiert vs. Vortrainiert
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 4: Untrainiert vs. Vortrainiert                ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Untrainierter Spieler
        Spieler untrainiert = new Spieler("Untrainiert", 0.1, 0.9, 1.0);
        untrainiert.setTrainingsmodus(false);
        
        System.out.println("Teste Untrainierten Spieler (100 Spiele)...");
        int[] ergebnisUntrainiert = spieleSerie(spiel, untrainiert, gegner, 100);
        
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────┐");
        System.out.println("│  Performance-Vergleich                          │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.printf("│  Untrainiert:   %3d Siege (%3d%%)               │%n", 
                         ergebnisUntrainiert[0], ergebnisUntrainiert[0]);
        System.out.printf("│  Vortrainiert:  %3d Siege (%3d%%)               │%n", 
                         ergebnisGeladen[0], ergebnisGeladen[0]);
        System.out.printf("│  Verbesserung:  +%3d Siege (+%3d%%)             │%n", 
                         ergebnisGeladen[0] - ergebnisUntrainiert[0],
                         ergebnisGeladen[0] - ergebnisUntrainiert[0]);
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.println();
        
        // ====================================================================
        // TEST 5: Modell-Versionierung
        // ====================================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║  Test 5: Mehrere Modell-Versionen speichern          ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Speichere verschiedene Versionen
        String[] versionen = {
            MODEL_DIR + "model_1k.dat",
            MODEL_DIR + "model_10k.dat",
            MODEL_DIR + "model_100k.dat"
        };
        
        int[] trainingsGroessen = {1000, 10000, 100000};
        
        for (int i = 0; i < versionen.length; i++) {
            System.out.println("Trainiere Modell mit " + trainingsGroessen[i] + " Spielen...");
            Spieler tempSpieler = new Spieler("Temp-" + trainingsGroessen[i], 0.1, 0.9, 0.3);
            tempSpieler.trainieren(new AbbruchNachIterationen(trainingsGroessen[i]));
            tempSpieler.speichereModell(versionen[i], trainingsGroessen[i]);
            
            File versionFile = new File(versionen[i]);
            System.out.println("  ✓ Gespeichert: " + versionFile.getName() + " (" + (versionFile.length() / 1024) + " KB)");
            System.out.println();
        }
        
        // Zeige alle Modelle
        System.out.println("📦 Verfügbare Modelle im Verzeichnis '" + MODEL_DIR + "':");
        File modelDir = new File(MODEL_DIR);
        if (modelDir.exists() && modelDir.isDirectory()) {
            File[] files = modelDir.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File f : files) {
                    System.out.printf("   - %-25s %6d KB%n", f.getName(), f.length() / 1024);
                }
            }
        }
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Phase 6 abgeschlossen!");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("💡 Verwendung:");
        System.out.println("   // Spieler mit vortrainiertem Modell:");
        System.out.println("   Spieler spieler = new Spieler(\"Mein Spieler\", \"" + MODEL_FILE + "\");");
        System.out.println();
        System.out.println("   // Training und Speichern:");
        System.out.println("   spieler.trainieren(new AbbruchNachIterationen(200000));");
        System.out.println("   spieler.speichereModell(\"modell.dat\", 200000);");
    }
    
    /**
     * Spielt eine Serie von Spielen und zählt Ergebnisse
     * @return int[] {Siege Spieler1, Siege Spieler2, Unentschieden}
     */
    private static int[] spieleSerie(TicTacToe spiel, ISpieler spieler1, ISpieler spieler2, int anzahl) {
        int siege1 = 0;
        int siege2 = 0;
        int unentschieden = 0;
        
        for (int i = 0; i < anzahl; i++) {
            // Abwechselnd starten
            ISpieler gewinner;
            if (i % 2 == 0) {
                gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, false);
            } else {
                gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, false);
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
