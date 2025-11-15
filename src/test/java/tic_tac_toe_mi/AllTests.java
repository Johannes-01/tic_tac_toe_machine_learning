package tic_tac_toe_mi;

import tictactoe.Farbe;
import tictactoe.Spielfeld;

/**
 * Unit Tests für alle Komponenten
 * Testet SpielzustandAnalyzer, SpielzustandKonverter und QLearningAgent
 * 
 * @author johanneshaick
 */
public class AllTests {
    
    private static int testsGesamt = 0;
    private static int testsErfolgreich = 0;
    private static int testsFehler = 0;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Tic-Tac-Toe RL - Unit Tests");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println();
        
        testSpielzustandAnalyzer();
        testSpielzustandKonverter();
        testQLearningAgent();
        
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  Test-Zusammenfassung");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Gesamt:      " + testsGesamt);
        System.out.println("Erfolgreich: " + testsErfolgreich + " ✅");
        System.out.println("Fehler:      " + testsFehler + " ❌");
        System.out.println();
        
        if (testsFehler == 0) {
            System.out.println("🎉 Alle Tests bestanden!");
        } else {
            System.out.println("⚠️  " + testsFehler + " Test(s) fehlgeschlagen!");
            System.exit(1);
        }
    }
    
    // ========================================================================
    // SpielzustandAnalyzer Tests
    // ========================================================================
    
    private static void testSpielzustandAnalyzer() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  SpielzustandAnalyzer Tests                  ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        
        SpielzustandAnalyzer analyzer = new SpielzustandAnalyzer();
        
        // Zeilen-Sieg
        System.out.println("Test-Gruppe: Zeilen-Sieg");
        for (int zeile = 0; zeile < 3; zeile++) {
            Spielfeld feld = new Spielfeld();
            feld.setFarbe(zeile, 0, Farbe.Kreuz);
            feld.setFarbe(zeile, 1, Farbe.Kreuz);
            feld.setFarbe(zeile, 2, Farbe.Kreuz);
            assertTrue(analyzer.istSieg(feld, Farbe.Kreuz), "Zeile " + zeile);
        }
        System.out.println();
        
        // Spalten-Sieg
        System.out.println("Test-Gruppe: Spalten-Sieg");
        for (int spalte = 0; spalte < 3; spalte++) {
            Spielfeld feld = new Spielfeld();
            feld.setFarbe(0, spalte, Farbe.Kreis);
            feld.setFarbe(1, spalte, Farbe.Kreis);
            feld.setFarbe(2, spalte, Farbe.Kreis);
            assertTrue(analyzer.istSieg(feld, Farbe.Kreis), "Spalte " + spalte);
        }
        System.out.println();
        
        // Diagonalen
        System.out.println("Test-Gruppe: Diagonale-Sieg");
        Spielfeld d1 = new Spielfeld();
        d1.setFarbe(0, 0, Farbe.Kreuz);
        d1.setFarbe(1, 1, Farbe.Kreuz);
        d1.setFarbe(2, 2, Farbe.Kreuz);
        assertTrue(analyzer.istSieg(d1, Farbe.Kreuz), "Diagonale \\");
        
        Spielfeld d2 = new Spielfeld();
        d2.setFarbe(0, 2, Farbe.Kreis);
        d2.setFarbe(1, 1, Farbe.Kreis);
        d2.setFarbe(2, 0, Farbe.Kreis);
        assertTrue(analyzer.istSieg(d2, Farbe.Kreis), "Diagonale /");
        System.out.println();
        
        // Unentschieden
        System.out.println("Test-Gruppe: Unentschieden");
        Spielfeld u = new Spielfeld();
        u.setFarbe(0, 0, Farbe.Kreuz); u.setFarbe(0, 1, Farbe.Kreis); u.setFarbe(0, 2, Farbe.Kreuz);
        u.setFarbe(1, 0, Farbe.Kreuz); u.setFarbe(1, 1, Farbe.Kreis); u.setFarbe(1, 2, Farbe.Kreis);
        u.setFarbe(2, 0, Farbe.Kreis); u.setFarbe(2, 1, Farbe.Kreuz); u.setFarbe(2, 2, Farbe.Kreuz);
        assertTrue(analyzer.istUnentschieden(u), "Unentschieden erkannt");
        assertNull(analyzer.getGewinner(u), "Kein Gewinner");
        System.out.println();
        
        // Reward-Funktion
        System.out.println("Test-Gruppe: Reward-Funktion");
        Spielfeld r = new Spielfeld();
        r.setFarbe(0, 0, Farbe.Kreuz);
        r.setFarbe(0, 1, Farbe.Kreuz);
        r.setFarbe(0, 2, Farbe.Kreuz);
        assertEquals(1.0, analyzer.bewertePosition(r, Farbe.Kreuz), "Sieg = +1.0");
        assertEquals(-1.0, analyzer.bewertePosition(r, Farbe.Kreis), "Niederlage = -1.0");
        System.out.println();
    }
    
    // ========================================================================
    // SpielzustandKonverter Tests
    // ========================================================================
    
    private static void testSpielzustandKonverter() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  SpielzustandKonverter Tests                 ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        
        SpielzustandKonverter konverter = new SpielzustandKonverter();
        
        System.out.println("Test-Gruppe: String-Konvertierung");
        Spielfeld leer = new Spielfeld();
        assertEquals("_________", konverter.zuString(leer), "Leeres Spielfeld");
        
        Spielfeld s1 = new Spielfeld();
        s1.setFarbe(0, 0, Farbe.Kreuz);
        s1.setFarbe(0, 1, Farbe.Kreis);
        s1.setFarbe(1, 1, Farbe.Kreuz);
        assertEquals("XO__X____", konverter.zuString(s1), "Teilgefülltes Spielfeld");
        System.out.println();
        
        System.out.println("Test-Gruppe: Normalisierte Konvertierung");
        String norm1 = konverter.zuStringNormalisiert(s1, Farbe.Kreuz);
        assertTrue(norm1.contains("X") && norm1.contains("O"), "Normalisierung Kreuz-Perspektive");
        String norm2 = konverter.zuStringNormalisiert(s1, Farbe.Kreis);
        assertTrue(norm2.contains("X") && norm2.contains("O"), "Normalisierung Kreis-Perspektive");
        System.out.println();
        
        System.out.println("Test-Gruppe: Spielfeld kopieren");
        Spielfeld original = new Spielfeld();
        original.setFarbe(1, 1, Farbe.Kreuz);
        Spielfeld kopie = konverter.kopiereSpielfeld(original);
        assertEquals(konverter.zuString(original), konverter.zuString(kopie), "Kopie identisch");
        kopie.setFarbe(0, 0, Farbe.Kreis);
        assertNotEquals(konverter.zuString(original), konverter.zuString(kopie), "Kopie unabhängig");
        System.out.println();
    }
    
    // ========================================================================
    // QLearningAgent Tests
    // ========================================================================
    
    private static void testQLearningAgent() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║  QLearningAgent Tests                        ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        
        QLearningAgent agent = new QLearningAgent();
        
        System.out.println("Test-Gruppe: Initialisierung");
        assertEquals(0.1, agent.getLernrate(), "Lernrate = 0.1");
        assertEquals(0.9, agent.getDiscountFaktor(), "Discount = 0.9");
        assertEquals(0.3, agent.getExplorationRate(), "Exploration = 0.3");
        assertEquals(0, agent.getAnzahlStates(), "Q-Tabelle leer");
        System.out.println();
        
        System.out.println("Test-Gruppe: Parameter setzen");
        agent.setLernrate(0.2);
        assertEquals(0.2, agent.getLernrate(), "Lernrate änderbar");
        agent.setDiscountFaktor(0.95);
        assertEquals(0.95, agent.getDiscountFaktor(), "Discount änderbar");
        System.out.println();
        
        System.out.println("Test-Gruppe: Q-Werte");
        double[] qWerte = agent.getQWerte("_________");
        assertEquals(9, qWerte.length, "Q-Array hat 9 Einträge");
        assertEquals(1, agent.getAnzahlStates(), "1 State in Tabelle");
        System.out.println();
        
        System.out.println("Test-Gruppe: Aktion-Konvertierung");
        int aktion = agent.zugZuAktion(new tictactoe.Zug(1, 2));
        assertEquals(5, aktion, "Zug(1,2) → Aktion 5");
        tictactoe.Zug zug = agent.aktionZuZug(5);
        assertEquals(1, zug.getZeile(), "Aktion 5 → Zeile 1");
        assertEquals(2, zug.getSpalte(), "Aktion 5 → Spalte 2");
        System.out.println();
    }
    
    // ========================================================================
    // Test-Hilfsmethoden
    // ========================================================================
    
    private static void assertTrue(boolean condition, String message) {
        testsGesamt++;
        if (condition) {
            testsErfolgreich++;
            System.out.println("  ✅ " + message);
        } else {
            testsFehler++;
            System.out.println("  ❌ " + message);
        }
    }
    
    private static void assertEquals(Object expected, Object actual, String message) {
        testsGesamt++;
        if (expected == null && actual == null) {
            testsErfolgreich++;
            System.out.println("  ✅ " + message);
        } else if (expected != null && expected.equals(actual)) {
            testsErfolgreich++;
            System.out.println("  ✅ " + message);
        } else {
            testsFehler++;
            System.out.println("  ❌ " + message + " (erwartet: " + expected + ", bekommen: " + actual + ")");
        }
    }
    
    private static void assertNotEquals(Object expected, Object actual, String message) {
        testsGesamt++;
        if (expected == null && actual != null || expected != null && !expected.equals(actual)) {
            testsErfolgreich++;
            System.out.println("  ✅ " + message);
        } else {
            testsFehler++;
            System.out.println("  ❌ " + message + " (sollten unterschiedlich sein)");
        }
    }
    
    private static void assertNull(Object object, String message) {
        testsGesamt++;
        if (object == null) {
            testsErfolgreich++;
            System.out.println("  ✅ " + message);
        } else {
            testsFehler++;
            System.out.println("  ❌ " + message + " (sollte null sein)");
        }
    }
}
