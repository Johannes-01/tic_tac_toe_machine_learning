package tic_tac_toe_mi;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import tictactoe.Farbe;
import tictactoe.IllegalerZugException;
import tictactoe.Spielfeld;
import tictactoe.Zug;
import tictactoe.spieler.IAbbruchbedingung;
import tictactoe.spieler.ILernenderSpieler;

/**
 * Reinforcement Learning Spieler für Tic-Tac-Toe.
 * Verwendet Q-Learning um optimale Spielzüge zu lernen.
 * 
 * Diese Klasse orchestriert die verschiedenen Komponenten:
 * - QLearningAgent: Für Q-Learning Logik
 * - SpielzustandAnalyzer: Für Spielzustand-Analyse
 * - SpielzustandKonverter: Für State-Repräsentation
 * 
 * Design Pattern: Facade Pattern - Vereinfacht die Nutzung der Komponenten
 * 
 * @author johanneshaick
 */
public class Spieler implements ILernenderSpieler {
    
    // Spieler-Informationen
    private String name;
    private Farbe meineFarbe;
    
    // Spielfeld-Verwaltung
    private Spielfeld spielfeld;
    
    // Komponenten (Dependency Injection / Composition)
    private QLearningAgent qAgent;
    private SpielzustandAnalyzer analyzer;
    private SpielzustandKonverter konverter;
    
    // Episode-Tracking für Q-Learning
    private boolean trainingsmodus = false;
    private List<Episode> episodenHistory;
    
    /**
     * Interne Klasse zum Speichern von Spiel-Episoden für Q-Learning
     */
    private static class Episode {
        String state;
        int aktion;
        double reward;
        String naechsterState;
        boolean istTerminal;
        
        Episode(String state, int aktion, double reward, String naechsterState, boolean istTerminal) {
            this.state = state;
            this.aktion = aktion;
            this.reward = reward;
            this.naechsterState = naechsterState;
            this.istTerminal = istTerminal;
        }
    }
    
    /**
     * Konstruktor - Erstellt einen neuen Reinforcement Learning Spieler
     * @param name Name des Spielers
     */
    public Spieler(String name) {
        this.name = name;
        this.qAgent = new QLearningAgent();
        this.analyzer = new SpielzustandAnalyzer();
        this.konverter = new SpielzustandKonverter();
        this.episodenHistory = new ArrayList<>();
        
        // Versuche vortrainiertes Modell zu laden
        ladeModellFallsVorhanden("models/default_model.dat");
    }
    
    /**
     * Konstruktor mit benutzerdefinierten RL-Parametern
     * @param name Name des Spielers
     * @param lernrate Alpha-Wert für Q-Learning
     * @param discountFaktor Gamma-Wert für Q-Learning
     * @param explorationRate Epsilon-Wert für Exploration
     */
    public Spieler(String name, double lernrate, double discountFaktor, double explorationRate) {
        this.name = name;
        this.qAgent = new QLearningAgent(lernrate, discountFaktor, explorationRate);
        this.analyzer = new SpielzustandAnalyzer();
        this.konverter = new SpielzustandKonverter();
        this.episodenHistory = new ArrayList<>();
    }
    
    /**
     * Konstruktor mit vortrainiertem Modell
     * @param name Name des Spielers
     * @param modellPfad Pfad zum gespeicherten Modell
     */
    public Spieler(String name, String modellPfad) {
        this.name = name;
        this.qAgent = new QLearningAgent();
        this.analyzer = new SpielzustandAnalyzer();
        this.konverter = new SpielzustandKonverter();
        this.episodenHistory = new ArrayList<>();
        
        // Lade Modell
        try {
            qAgent.ladeModell(modellPfad);
            System.out.println("✅ Spieler '" + name + "' mit vortrainiertem Modell erstellt");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("⚠️ Konnte Modell nicht laden: " + e.getMessage());
            System.out.println("Spieler startet ohne Training");
        }
    }
    
    /**
     * Versucht ein Modell zu laden, falls vorhanden (ohne Fehler bei Nicht-Existenz)
     */
    private void ladeModellFallsVorhanden(String pfad) {
        try {
            File datei = new File(pfad);
            if (datei.exists()) {
                qAgent.ladeModell(pfad);
                System.out.println("✅ Vortrainiertes Modell geladen: " + pfad);
            }
        } catch (IOException | ClassNotFoundException e) {
            // Ignoriere - Spieler startet ohne Training
        }
    }
    
    /**
     * Startet ein neues Spiel
     * @param meineFarbe Die Farbe des Spielers (KREIS oder KREUZ)
     * @param bedenkzeitInSekunden Verfügbare Bedenkzeit (wird noch nicht genutzt)
     */
    @Override
    public void neuesSpiel(Farbe meineFarbe, int bedenkzeitInSekunden) {
        this.meineFarbe = meineFarbe;
        this.spielfeld = new Spielfeld();
        this.episodenHistory.clear(); // Neue Episode beginnt
    }
    
    /**
     * Berechnet den nächsten Zug
     * @param vorherigerZug Der letzte Zug des Gegners (null bei erstem Zug)
     * @param zeitKreis Verbrauchte Zeit von Kreis-Spieler (wird noch nicht genutzt)
     * @param zeitKreuz Verbrauchte Zeit von Kreuz-Spieler (wird noch nicht genutzt)
     * @return Der berechnete Zug
     */
    @Override
    public Zug berechneZug(Zug vorherigerZug, long zeitKreis, long zeitKreuz) throws IllegalerZugException {
        // 1. Gegnerischen Zug im eigenen Spielfeld vermerken
        if (vorherigerZug != null) {
            spielfeld.setFarbe(vorherigerZug.getZeile(), 
                             vorherigerZug.getSpalte(), 
                             meineFarbe.opposite());
        }
        
        // 2. Aktuellen State erfassen (BEVOR eigener Zug)
        String aktuellerState = konverter.zuStringNormalisiert(spielfeld, meineFarbe);
        
        // 3. Eigenen Zug berechnen (mit Q-Learning)
        Zug neuerZug = waehleZug();
        int aktion = qAgent.zugZuAktion(neuerZug);
        
        // 4. Eigenen Zug im Spielfeld vermerken
        spielfeld.setFarbe(neuerZug.getZeile(), 
                          neuerZug.getSpalte(), 
                          meineFarbe);
        
        // 5. Neuen State und Reward erfassen (NACH eigenem Zug)
        String neuerState = konverter.zuStringNormalisiert(spielfeld, meineFarbe);
        boolean istTerminal = analyzer.istSpielBeendet(spielfeld);
        double reward = analyzer.bewertePosition(spielfeld, meineFarbe);
        
        // 6. Q-Learning Update (wenn im Trainingsmodus)
        if (trainingsmodus) {
            qAgent.lernen(aktuellerState, aktion, reward, neuerState, istTerminal);
        }
        
        // 7. Episode speichern für spätere Analyse
        episodenHistory.add(new Episode(aktuellerState, aktion, reward, neuerState, istTerminal));
        
        return neuerZug;
    }
    
    /**
     * Wählt einen Zug basierend auf Q-Learning Strategie
     * Delegiert an QLearningAgent
     * @return Der gewählte Zug
     */
    private Zug waehleZug() {
        List<Zug> moeglicheZuege = getMoeglicheZuege();
        
        if (moeglicheZuege.isEmpty()) {
            throw new IllegalStateException("Keine möglichen Züge verfügbar!");
        }
        
        // Delegiere an Q-Learning Agent
        return qAgent.waehleZug(spielfeld, meineFarbe, moeglicheZuege);
    }
    
    /**
     * Findet alle noch möglichen Züge
     * @return Liste aller freien Felder
     */
    private List<Zug> getMoeglicheZuege() {
        List<Zug> zuege = new ArrayList<>();
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                if (spielfeld.getFarbe(zeile, spalte) == Farbe.Leer) {
                    zuege.add(new Zug(zeile, spalte));
                }
            }
        }
        return zuege;
    }
    
    /**
     * Trainiert den Spieler durch Self-Play
     * @param abbruch Bedingung wann das Training beendet werden soll
     * @return true wenn Training erfolgreich
     */
    @Override
    public boolean trainieren(IAbbruchbedingung abbruch) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  Training gestartet - Self-Play Modus           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Lernrate (α):      " + getLernrate());
        System.out.println("Discount (γ):      " + getDiscountFaktor());
        System.out.println("Exploration (ε):   " + getExplorationRate());
        System.out.println();
        
        // Aktiviere Trainingsmodus
        boolean alterTrainingsmodus = trainingsmodus;
        setTrainingsmodus(true);
        
        // Statistik-Variablen
        int spieleGesamt = 0;
        int siegeSpieler1 = 0;
        int siegeSpieler2 = 0;
        int unentschieden = 0;
        long startZeit = System.currentTimeMillis();
        
        // Training Loop
        while (!abbruch.abbruch()) {
            spieleGesamt++;
            
            // Self-Play: Spiele gegen eine Kopie von sich selbst
            ISpielerErgebnis ergebnis = spieleSelbst();
            
            // Statistik aktualisieren
            if (ergebnis == ISpielerErgebnis.SPIELER1_GEWINNT) {
                siegeSpieler1++;
            } else if (ergebnis == ISpielerErgebnis.SPIELER2_GEWINNT) {
                siegeSpieler2++;
            } else {
                unentschieden++;
            }
            
            // Progress-Report alle 100 Spiele
            if (spieleGesamt % 100 == 0) {
                zeigeTrainingsfortschritt(spieleGesamt, siegeSpieler1, siegeSpieler2, unentschieden);
            }
        }
        
        // Trainingsmodus zurücksetzen
        setTrainingsmodus(alterTrainingsmodus);
        
        // Abschluss-Statistik
        long dauer = System.currentTimeMillis() - startZeit;
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║  Training abgeschlossen                          ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("Gespielte Spiele:    " + spieleGesamt);
        System.out.println("Dauer:               " + (dauer / 1000.0) + " Sekunden");
        System.out.println("Spiele/Sekunde:      " + (spieleGesamt / (dauer / 1000.0)));
        System.out.println("Q-Tabelle States:    " + qAgent.getAnzahlStates());
        System.out.println("Siege Spieler 1:     " + siegeSpieler1 + " (" + (siegeSpieler1 * 100.0 / spieleGesamt) + "%)");
        System.out.println("Siege Spieler 2:     " + siegeSpieler2 + " (" + (siegeSpieler2 * 100.0 / spieleGesamt) + "%)");
        System.out.println("Unentschieden:       " + unentschieden + " (" + (unentschieden * 100.0 / spieleGesamt) + "%)");
        System.out.println();
        
        return true;
    }
    
    /**
     * Interne Enum für Spiel-Ergebnisse
     */
    private enum ISpielerErgebnis {
        SPIELER1_GEWINNT,
        SPIELER2_GEWINNT,
        UNENTSCHIEDEN
    }
    
    /**
     * Spielt ein Spiel gegen sich selbst (Self-Play)
     * Beide Spieler nutzen dieselbe Q-Tabelle und lernen daraus
     * @return Das Ergebnis des Spiels
     */
    private ISpielerErgebnis spieleSelbst() {
        // Neues Spielfeld erstellen
        Spielfeld feld = new Spielfeld();
        
        // Zufällig entscheiden wer anfängt
        boolean spieler1AmZug = Math.random() < 0.5;
        Farbe spieler1Farbe = spieler1AmZug ? Farbe.Kreuz : Farbe.Kreis;
        Farbe spieler2Farbe = spieler1Farbe.opposite();
        
        int zuege = 0;
        final int MAX_ZUEGE = 9; // Tic-Tac-Toe hat maximal 9 Züge
        
        // Spiel-Loop
        while (zuege < MAX_ZUEGE) {
            Farbe aktuelleFarbe = spieler1AmZug ? spieler1Farbe : spieler2Farbe;
            
            // State vor dem Zug
            String state = konverter.zuStringNormalisiert(feld, aktuelleFarbe);
            
            // Finde mögliche Züge
            List<Zug> moeglicheZuege = getMoeglicheZuegeAufFeld(feld);
            if (moeglicheZuege.isEmpty()) {
                break; // Sollte nicht passieren
            }
            
            // Wähle Zug mit Q-Learning
            Zug gewaehlterZug = qAgent.waehleZug(feld, aktuelleFarbe, moeglicheZuege);
            int aktion = qAgent.zugZuAktion(gewaehlterZug);
            
            // Führe Zug aus
            feld.setFarbe(gewaehlterZug.getZeile(), gewaehlterZug.getSpalte(), aktuelleFarbe);
            zuege++;
            
            // State nach dem Zug
            String naechsterState = konverter.zuStringNormalisiert(feld, aktuelleFarbe);
            
            // Prüfe auf Spielende
            boolean hatGewonnen = analyzer.istSieg(feld, aktuelleFarbe);
            boolean istVoll = analyzer.istUnentschieden(feld);
            boolean istTerminal = hatGewonnen || istVoll;
            
            // Berechne Reward
            double reward = analyzer.bewertePosition(feld, aktuelleFarbe);
            
            // Q-Learning Update
            qAgent.lernen(state, aktion, reward, naechsterState, istTerminal);
            
            // Spielende-Prüfung
            if (hatGewonnen) {
                return spieler1AmZug ? ISpielerErgebnis.SPIELER1_GEWINNT : ISpielerErgebnis.SPIELER2_GEWINNT;
            }
            if (istVoll) {
                return ISpielerErgebnis.UNENTSCHIEDEN;
            }
            
            // Spieler wechseln
            spieler1AmZug = !spieler1AmZug;
        }
        
        return ISpielerErgebnis.UNENTSCHIEDEN;
    }
    
    /**
     * Hilfsmethode: Findet alle möglichen Züge auf einem gegebenen Spielfeld
     */
    private List<Zug> getMoeglicheZuegeAufFeld(Spielfeld feld) {
        List<Zug> zuege = new ArrayList<>();
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                if (feld.getFarbe(zeile, spalte) == Farbe.Leer) {
                    zuege.add(new Zug(zeile, spalte));
                }
            }
        }
        return zuege;
    }
    
    /**
     * Zeigt den aktuellen Trainingsfortschritt
     */
    private void zeigeTrainingsfortschritt(int spiele, int siege1, int siege2, int unent) {
        double siegrate1 = (siege1 * 100.0) / spiele;
        double siegrate2 = (siege2 * 100.0) / spiele;
        double unentRate = (unent * 100.0) / spiele;
        
        System.out.printf("[Spiel %5d] Siegrate: Sp1=%.1f%% | Sp2=%.1f%% | Unent=%.1f%% | States=%d%n",
                         spiele, siegrate1, siegrate2, unentRate, qAgent.getAnzahlStates());
    }
    
    /**
     * Speichert das gelernte Wissen (Q-Tabelle)
     * Delegiert an QLearningAgent
     * @param dateiname Pfad zur Speicherdatei
     */
    @Override
    public void speichereWissen(String dateiname) throws IOException {
        qAgent.speichern(dateiname);
        System.out.println("Q-Tabelle gespeichert in: " + dateiname);
        System.out.println("Anzahl gelernter Zustände: " + qAgent.getAnzahlStates());
    }
    
    /**
     * Lädt vorher gespeichertes Wissen
     * Delegiert an QLearningAgent
     * @param dateiname Pfad zur Speicherdatei
     */
    @Override
    public void ladeWissen(String dateiname) throws IOException {
        try {
            qAgent.laden(dateiname);
            System.out.println("Q-Tabelle geladen von: " + dateiname);
            System.out.println("Anzahl geladener Zustände: " + qAgent.getAnzahlStates());
        } catch (ClassNotFoundException e) {
            throw new IOException("Fehler beim Laden der Q-Tabelle", e);
        }
    }
    
    // ========================================================================
    // Getter und Setter
    // ========================================================================
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public Farbe getFarbe() {
        return meineFarbe;
    }
    
    @Override
    public void setFarbe(Farbe farbe) {
        this.meineFarbe = farbe;
    }
    
    // Getter und Setter für RL-Parameter - Delegieren an QLearningAgent
    
    public double getLernrate() {
        return qAgent.getLernrate();
    }
    
    public void setLernrate(double lernrate) {
        qAgent.setLernrate(lernrate);
    }
    
    public double getDiscountFaktor() {
        return qAgent.getDiscountFaktor();
    }
    
    public void setDiscountFaktor(double discountFaktor) {
        qAgent.setDiscountFaktor(discountFaktor);
    }
    
    public double getExplorationRate() {
        return qAgent.getExplorationRate();
    }
    
    public void setExplorationRate(double explorationRate) {
        qAgent.setExplorationRate(explorationRate);
    }
    
    /**
     * Gibt Zugriff auf die internen Komponenten für Tests
     */
    public QLearningAgent getQLearningAgent() {
        return qAgent;
    }
    
    public SpielzustandAnalyzer getAnalyzer() {
        return analyzer;
    }
    
    public SpielzustandKonverter getKonverter() {
        return konverter;
    }
    
    /**
     * Aktiviert den Trainingsmodus (Q-Values werden aktualisiert)
     */
    public void setTrainingsmodus(boolean trainingsmodus) {
        this.trainingsmodus = trainingsmodus;
    }
    
    /**
     * Gibt zurück ob Trainingsmodus aktiv ist
     */
    public boolean istTrainingsmodus() {
        return trainingsmodus;
    }
    
    /**
     * Gibt die Anzahl der Episoden in der aktuellen History zurück
     */
    public int getAnzahlEpisoden() {
        return episodenHistory.size();
    }
    
    /**
     * Löscht die Episoden-History
     */
    public void clearEpisodenHistory() {
        episodenHistory.clear();
    }
}
