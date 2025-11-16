package tic_tac_toe_mi;

import tic_tac_toe_mi.nn.NNDemo;
import tictactoe.Farbe;
import tictactoe.IllegalerZugException;
import tictactoe.Spielfeld;
import tictactoe.Zug;

import java.util.*;

/**
 * Interaktives Spiel-Menü für Tic-Tac-Toe.
 * 
 * Ermöglicht verschiedene Spielmodi:
 * - Q-Learning vs Zufallsspieler
 * - Neural Network vs Zufallsspieler
 * - Q-Learning vs Menschlicher Spieler
 * - Neural Network vs Menschlicher Spieler
 * - Q-Learning vs Neural Network
 * 
 * @author johanneshaick
 */
public class InteraktivesSpiel {
    
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    
    /**
     * Einfacher menschlicher Spieler.
     */
    static class MenschlicherSpieler {
        private final String name;
        private Farbe farbe;
        
        MenschlicherSpieler(String name) {
            this.name = name;
        }
        
        void setzeFarbe(Farbe farbe) {
            this.farbe = farbe;
        }
        
        Zug waehleZug(Spielfeld feld) {
            zeigeSpielfeld(feld);
            
            System.out.println("\n" + name + " (" + farbe + "), wähle dein Feld:");
            System.out.println("Gib Zeile (0-2) und Spalte (0-2) ein, z.B. '1 1' für Mitte:");
            
            while (true) {
                try {
                    System.out.print("> ");
                    String input = scanner.nextLine().trim();
                    
                    if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                        System.out.println("Spiel beendet.");
                        System.exit(0);
                    }
                    
                    String[] parts = input.split("\\s+");
                    if (parts.length != 2) {
                        System.out.println("❌ Bitte gib Zeile und Spalte ein (z.B. '1 1')");
                        continue;
                    }
                    
                    int zeile = Integer.parseInt(parts[0]);
                    int spalte = Integer.parseInt(parts[1]);
                    
                    if (zeile < 0 || zeile > 2 || spalte < 0 || spalte > 2) {
                        System.out.println("❌ Zeile und Spalte müssen zwischen 0 und 2 sein!");
                        continue;
                    }
                    
                    if (feld.getFarbe(zeile, spalte) != Farbe.Leer) {
                        System.out.println("❌ Dieses Feld ist bereits belegt!");
                        continue;
                    }
                    
                    return new Zug(zeile, spalte);
                    
                } catch (NumberFormatException e) {
                    System.out.println("❌ Ungültige Eingabe! Verwende Zahlen 0-2.");
                }
            }
        }
    }
    
    /**
     * Zeigt das Spielfeld an.
     */
    static void zeigeSpielfeld(Spielfeld feld) {
        System.out.println("\n  Aktuelles Spielfeld:");
        System.out.println("  ┌───┬───┬───┐");
        
        for (int z = 0; z < 3; z++) {
            System.out.print("  │");
            for (int s = 0; s < 3; s++) {
                Farbe f = feld.getFarbe(z, s);
                String symbol;
                if (f == Farbe.Leer) {
                    symbol = " ";
                } else if (f == Farbe.Kreuz) {
                    symbol = "X";
                } else {
                    symbol = "O";
                }
                System.out.print(" " + symbol + " │");
            }
            System.out.println();
            if (z < 2) {
                System.out.println("  ├───┼───┼───┤");
            }
        }
        System.out.println("  └───┴───┴───┘");
    }
    
    /**
     * Prüft ob Spiel beendet ist.
     */
    static boolean istSpielBeendet(Spielfeld feld) {
        return getGewinner(feld) != null || istVoll(feld);
    }
    
    /**
     * Ermittelt Gewinner.
     */
    static Farbe getGewinner(Spielfeld feld) {
        // Zeilen
        for (int i = 0; i < 3; i++) {
            if (feld.getFarbe(i, 0) != Farbe.Leer &&
                feld.getFarbe(i, 0) == feld.getFarbe(i, 1) &&
                feld.getFarbe(i, 1) == feld.getFarbe(i, 2)) {
                return feld.getFarbe(i, 0);
            }
        }
        
        // Spalten
        for (int i = 0; i < 3; i++) {
            if (feld.getFarbe(0, i) != Farbe.Leer &&
                feld.getFarbe(0, i) == feld.getFarbe(1, i) &&
                feld.getFarbe(1, i) == feld.getFarbe(2, i)) {
                return feld.getFarbe(0, i);
            }
        }
        
        // Diagonalen
        if (feld.getFarbe(0, 0) != Farbe.Leer &&
            feld.getFarbe(0, 0) == feld.getFarbe(1, 1) &&
            feld.getFarbe(1, 1) == feld.getFarbe(2, 2)) {
            return feld.getFarbe(0, 0);
        }
        
        if (feld.getFarbe(0, 2) != Farbe.Leer &&
            feld.getFarbe(0, 2) == feld.getFarbe(1, 1) &&
            feld.getFarbe(1, 1) == feld.getFarbe(2, 0)) {
            return feld.getFarbe(0, 2);
        }
        
        return null;
    }
    
    /**
     * Prüft ob Spielfeld voll ist.
     */
    static boolean istVoll(Spielfeld feld) {
        for (int z = 0; z < 3; z++) {
            for (int s = 0; s < 3; s++) {
                if (feld.getFarbe(z, s) == Farbe.Leer) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Zufälliger Zug.
     */
    static Zug zufallsZug(Spielfeld feld) {
        List<Zug> moeglicheZuege = new ArrayList<>();
        for (int z = 0; z < 3; z++) {
            for (int s = 0; s < 3; s++) {
                if (feld.getFarbe(z, s) == Farbe.Leer) {
                    moeglicheZuege.add(new Zug(z, s));
                }
            }
        }
        return moeglicheZuege.get(random.nextInt(moeglicheZuege.size()));
    }
    
    /**
     * Q-Learning vs Zufallsspieler.
     */
    static void spieleQLearningVsZufall(int anzahlSpiele) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  Q-Learning vs Zufallsspieler                    ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        Spieler qPlayer = new Spieler("Q-Learning", 0.30, 0.99, 0.40);
        int siege = 0, niederlagen = 0, unentschieden = 0;
        
        System.out.println("Spiele " + anzahlSpiele + " Partien...\n");
        
        for (int g = 0; g < anzahlSpiele; g++) {
            qPlayer.neuesSpiel(Farbe.Kreuz, 60);
            Spielfeld feld = new Spielfeld();
            Zug letzterZug = null;
            
            while (!istSpielBeendet(feld)) {
                try {
                    // Q-Learning zieht
                    Zug qZug = qPlayer.berechneZug(letzterZug, 0, 0);
                    feld.setFarbe(qZug.getZeile(), qZug.getSpalte(), Farbe.Kreuz);
                    letzterZug = qZug;
                    
                    if (istSpielBeendet(feld)) break;
                    
                    // Zufall zieht
                    Zug randZug = zufallsZug(feld);
                    feld.setFarbe(randZug.getZeile(), randZug.getSpalte(), Farbe.Kreis);
                    letzterZug = randZug;
                } catch (IllegalerZugException e) {
                    break;
                }
            }
            
            Farbe gewinner = getGewinner(feld);
            if (gewinner == Farbe.Kreuz) siege++;
            else if (gewinner == Farbe.Kreis) niederlagen++;
            else unentschieden++;
            
            if ((g + 1) % 100 == 0) {
                double winRate = (100.0 * siege) / (g + 1);
                System.out.printf("Spiel %,4d | Siege: %,4d | Siegrate: %.1f%%\n", 
                                g + 1, siege, winRate);
            }
        }
        
        double winRate = (100.0 * siege) / anzahlSpiele;
        
        System.out.println("\n📊 ENDERGEBNIS:");
        System.out.printf("Siege:         %,d (%.1f%%)\n", siege, winRate);
        System.out.printf("Niederlagen:   %,d (%.1f%%)\n", niederlagen, (100.0 * niederlagen) / anzahlSpiele);
        System.out.printf("Unentschieden: %,d (%.1f%%)\n", unentschieden, (100.0 * unentschieden) / anzahlSpiele);
    }
    
    /**
     * Neural Network vs Zufallsspieler.
     */
    static void spieleNNVsZufall(int anzahlSpiele) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  Neural Network vs Zufallsspieler                ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        System.out.println("Trainiere Neural Network (10.000 Episoden)...");
        NNDemo.SimpleNNTrainer nnTrainer = new NNDemo.SimpleNNTrainer();
        nnTrainer.trainSelfPlay(10000);
        
        System.out.println("\nSpiele " + anzahlSpiele + " Partien...\n");
        int[] results = nnTrainer.testVsRandom(anzahlSpiele);
        
        double winRate = (100.0 * results[0]) / anzahlSpiele;
        
        System.out.println("\n📊 ENDERGEBNIS:");
        System.out.printf("Siege:         %,d (%.1f%%)\n", results[0], winRate);
        System.out.printf("Niederlagen:   %,d (%.1f%%)\n", results[1], (100.0 * results[1]) / anzahlSpiele);
        System.out.printf("Unentschieden: %,d (%.1f%%)\n", results[2], (100.0 * results[2]) / anzahlSpiele);
    }
    
    /**
     * Q-Learning vs Menschlicher Spieler.
     */
    static void spieleQLearningVsMensch() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  Q-Learning vs Menschlicher Spieler              ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        System.out.print("Dein Name: ");
        String name = scanner.nextLine();
        
        Spieler qPlayer = new Spieler("Q-Learning", 0.30, 0.99, 0.40);
        MenschlicherSpieler mensch = new MenschlicherSpieler(name);
        
        System.out.println("\nWähle deine Farbe:");
        System.out.println("1) X (du fängst an)");
        System.out.println("2) O (Q-Learning fängt an)");
        System.out.print("> ");
        
        String wahl = scanner.nextLine().trim();
        boolean menschBeginnt = wahl.equals("1");
        
        Farbe menschFarbe = menschBeginnt ? Farbe.Kreuz : Farbe.Kreis;
        Farbe qFarbe = menschBeginnt ? Farbe.Kreis : Farbe.Kreuz;
        
        mensch.setzeFarbe(menschFarbe);
        qPlayer.neuesSpiel(qFarbe, 60);
        
        Spielfeld feld = new Spielfeld();
        Zug letzterZug = null;
        
        System.out.println("\nSpiel startet! (Gib 'exit' ein um abzubrechen)");
        
        try {
            while (!istSpielBeendet(feld)) {
                if (menschBeginnt) {
                    // Mensch zieht
                    Zug menschZug = mensch.waehleZug(feld);
                    feld.setFarbe(menschZug.getZeile(), menschZug.getSpalte(), menschFarbe);
                    letzterZug = menschZug;
                    
                    if (istSpielBeendet(feld)) break;
                    
                    // Q-Learning zieht
                    System.out.println("\nQ-Learning denkt nach...");
                    Zug qZug = qPlayer.berechneZug(letzterZug, 0, 0);
                    feld.setFarbe(qZug.getZeile(), qZug.getSpalte(), qFarbe);
                    System.out.println("Q-Learning wählt: Zeile " + qZug.getZeile() + ", Spalte " + qZug.getSpalte());
                    letzterZug = qZug;
                } else {
                    // Q-Learning zieht
                    System.out.println("\nQ-Learning denkt nach...");
                    Zug qZug = qPlayer.berechneZug(letzterZug, 0, 0);
                    feld.setFarbe(qZug.getZeile(), qZug.getSpalte(), qFarbe);
                    System.out.println("Q-Learning wählt: Zeile " + qZug.getZeile() + ", Spalte " + qZug.getSpalte());
                    letzterZug = qZug;
                    
                    if (istSpielBeendet(feld)) break;
                    
                    // Mensch zieht
                    Zug menschZug = mensch.waehleZug(feld);
                    feld.setFarbe(menschZug.getZeile(), menschZug.getSpalte(), menschFarbe);
                    letzterZug = menschZug;
                }
            }
        } catch (IllegalerZugException e) {
            System.out.println("Fehler beim Zug: " + e.getMessage());
        }
        
        // Endergebnis
        zeigeSpielfeld(feld);
        System.out.println("\n🏁 SPIEL BEENDET!\n");
        
        Farbe gewinner = getGewinner(feld);
        if (gewinner == null) {
            System.out.println("⚖️  Unentschieden!");
        } else if (gewinner == menschFarbe) {
            System.out.println("🎉 Glückwunsch, " + name + "! Du hast gewonnen!");
        } else {
            System.out.println("🤖 Q-Learning hat gewonnen! Besser das nächste Mal!");
        }
    }
    
    /**
     * Neural Network vs Menschlicher Spieler.
     */
    static void spieleNNVsMensch() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  Neural Network vs Menschlicher Spieler          ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        System.out.println("Trainiere Neural Network (10.000 Episoden)...");
        NNDemo.SimpleNNTrainer nnTrainer = new NNDemo.SimpleNNTrainer();
        nnTrainer.trainSelfPlay(10000);
        
        System.out.print("\nDein Name: ");
        String name = scanner.nextLine();
        
        MenschlicherSpieler mensch = new MenschlicherSpieler(name);
        
        System.out.println("\nWähle deine Farbe:");
        System.out.println("1) X (du fängst an)");
        System.out.println("2) O (Neural Network fängt an)");
        System.out.print("> ");
        
        String wahl = scanner.nextLine().trim();
        boolean menschBeginnt = wahl.equals("1");
        
        Farbe menschFarbe = menschBeginnt ? Farbe.Kreuz : Farbe.Kreis;
        Farbe nnFarbe = menschBeginnt ? Farbe.Kreis : Farbe.Kreuz;
        
        mensch.setzeFarbe(menschFarbe);
        
        Spielfeld feld = new Spielfeld();
        
        System.out.println("\nSpiel startet! (Gib 'exit' ein um abzubrechen)");
        
        while (!istSpielBeendet(feld)) {
            if (menschBeginnt) {
                // Mensch zieht
                Zug menschZug = mensch.waehleZug(feld);
                feld.setFarbe(menschZug.getZeile(), menschZug.getSpalte(), menschFarbe);
                
                if (istSpielBeendet(feld)) break;
                
                // NN zieht
                System.out.println("\nNeural Network denkt nach...");
                Zug nnZug = nnTrainer.selectAction(feld, nnFarbe, false);
                feld.setFarbe(nnZug.getZeile(), nnZug.getSpalte(), nnFarbe);
                System.out.println("Neural Network wählt: Zeile " + nnZug.getZeile() + ", Spalte " + nnZug.getSpalte());
            } else {
                // NN zieht
                System.out.println("\nNeural Network denkt nach...");
                Zug nnZug = nnTrainer.selectAction(feld, nnFarbe, false);
                feld.setFarbe(nnZug.getZeile(), nnZug.getSpalte(), nnFarbe);
                System.out.println("Neural Network wählt: Zeile " + nnZug.getZeile() + ", Spalte " + nnZug.getSpalte());
                
                if (istSpielBeendet(feld)) break;
                
                // Mensch zieht
                Zug menschZug = mensch.waehleZug(feld);
                feld.setFarbe(menschZug.getZeile(), menschZug.getSpalte(), menschFarbe);
            }
        }
        
        // Endergebnis
        zeigeSpielfeld(feld);
        System.out.println("\n🏁 SPIEL BEENDET!\n");
        
        Farbe gewinner = getGewinner(feld);
        if (gewinner == null) {
            System.out.println("⚖️  Unentschieden!");
        } else if (gewinner == menschFarbe) {
            System.out.println("🎉 Glückwunsch, " + name + "! Du hast gewonnen!");
        } else {
            System.out.println("🤖 Neural Network hat gewonnen! Besser das nächste Mal!");
        }
    }
    
    /**
     * Q-Learning vs Neural Network.
     */
    static void spieleQLearningVsNN(int anzahlSpiele) {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║  Q-Learning vs Neural Network                    ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        System.out.println("Trainiere Neural Network (10.000 Episoden)...");
        NNDemo.SimpleNNTrainer nnTrainer = new NNDemo.SimpleNNTrainer();
        nnTrainer.trainSelfPlay(10000);
        
        Spieler qPlayer = new Spieler("Q-Learning", 0.30, 0.99, 0.40);
        
        int qSiege = 0, nnSiege = 0, unentschieden = 0;
        
        System.out.println("\nSpiele " + anzahlSpiele + " Partien...\n");
        
        for (int g = 0; g < anzahlSpiele; g++) {
            qPlayer.neuesSpiel(Farbe.Kreuz, 60);
            Spielfeld feld = new Spielfeld();
            Zug letzterZug = null;
            
            try {
                while (!istSpielBeendet(feld)) {
                    // Q-Learning zieht
                    Zug qZug = qPlayer.berechneZug(letzterZug, 0, 0);
                    feld.setFarbe(qZug.getZeile(), qZug.getSpalte(), Farbe.Kreuz);
                    letzterZug = qZug;
                    
                    if (istSpielBeendet(feld)) break;
                    
                    // NN zieht
                    Zug nnZug = nnTrainer.selectAction(feld, Farbe.Kreis, false);
                    feld.setFarbe(nnZug.getZeile(), nnZug.getSpalte(), Farbe.Kreis);
                    letzterZug = nnZug;
                }
            } catch (IllegalerZugException e) {
                break;
            }
            
            Farbe gewinner = getGewinner(feld);
            if (gewinner == Farbe.Kreuz) qSiege++;
            else if (gewinner == Farbe.Kreis) nnSiege++;
            else unentschieden++;
            
            if ((g + 1) % 100 == 0) {
                double qWinRate = (100.0 * qSiege) / (g + 1);
                double nnWinRate = (100.0 * nnSiege) / (g + 1);
                System.out.printf("Spiel %,4d | Q: %,4d (%.1f%%) | NN: %,4d (%.1f%%)\n", 
                                g + 1, qSiege, qWinRate, nnSiege, nnWinRate);
            }
        }
        
        double qWinRate = (100.0 * qSiege) / anzahlSpiele;
        double nnWinRate = (100.0 * nnSiege) / anzahlSpiele;
        
        System.out.println("\n📊 ENDERGEBNIS:");
        System.out.printf("Q-Learning:    %,d Siege (%.1f%%)\n", qSiege, qWinRate);
        System.out.printf("Neural Net:    %,d Siege (%.1f%%)\n", nnSiege, nnWinRate);
        System.out.printf("Unentschieden: %,d (%.1f%%)\n", unentschieden, (100.0 * unentschieden) / anzahlSpiele);
        
        if (qSiege > nnSiege) {
            System.out.println("\n🏆 Q-Learning gewinnt den Vergleich!");
        } else if (nnSiege > qSiege) {
            System.out.println("\n🏆 Neural Network gewinnt den Vergleich!");
        } else {
            System.out.println("\n⚖️  Gleichstand!");
        }
    }
    
    /**
     * Hauptmenü.
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        TIC-TAC-TOE: Reinforcement Learning Demo           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        while (true) {
            System.out.println("\n📋 HAUPTMENÜ - Wähle einen Spielmodus:\n");
            System.out.println("  1️⃣  Q-Learning vs Zufallsspieler");
            System.out.println("  2️⃣  Neural Network vs Zufallsspieler");
            System.out.println("  3️⃣  Q-Learning vs Menschlicher Spieler (Du!)");
            System.out.println("  4️⃣  Neural Network vs Menschlicher Spieler (Du!)");
            System.out.println("  5️⃣  Q-Learning vs Neural Network (Direktvergleich)");
            System.out.println("  0️⃣  Beenden");
            System.out.print("\n> ");
            
            String wahl = scanner.nextLine().trim();
            
            switch (wahl) {
                case "1":
                    System.out.print("\nAnzahl Spiele (z.B. 1000): ");
                    int anzahl1 = Integer.parseInt(scanner.nextLine().trim());
                    spieleQLearningVsZufall(anzahl1);
                    break;
                    
                case "2":
                    System.out.print("\nAnzahl Spiele (z.B. 1000): ");
                    int anzahl2 = Integer.parseInt(scanner.nextLine().trim());
                    spieleNNVsZufall(anzahl2);
                    break;
                    
                case "3":
                    spieleQLearningVsMensch();
                    break;
                    
                case "4":
                    spieleNNVsMensch();
                    break;
                    
                case "5":
                    System.out.print("\nAnzahl Spiele (z.B. 500): ");
                    int anzahl5 = Integer.parseInt(scanner.nextLine().trim());
                    spieleQLearningVsNN(anzahl5);
                    break;
                    
                case "0":
                case "exit":
                case "quit":
                    System.out.println("\n👋 Auf Wiedersehen!\n");
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("\n❌ Ungültige Wahl! Bitte 0-5 eingeben.");
            }
            
            System.out.println("\n" + "─".repeat(60));
        }
    }
}
