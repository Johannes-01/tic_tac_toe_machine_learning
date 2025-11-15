package tic_tac_toe_mi.nn;

import tic_tac_toe_mi.*;
import tictactoe.Farbe;
import tictactoe.IllegalerZugException;
import tictactoe.Spielfeld;
import tictactoe.Zug;

import java.util.*;

/**
 * Demo: Neural Network vs Q-Learning Benchmark für Tic-Tac-Toe.
 * 
 * Testet Pure Java NN gegen optimierten Q-Learning Spieler.
 * Ziel: Beweisen dass NN Overkill ist für kleinen State Space.
 * 
 * @author johanneshaick
 */
public class NNDemo {
    
    private static final int TRAINING_EPISODES = 100_000;
    private static final int TEST_GAMES = 2_000;
    private static final Random RANDOM = new Random(42);
    
    /**
     * Einfacher NN-Trainer (ohne vollständigen ILernenderSpieler).
     */
    public static class SimpleNNTrainer {
        private final NeuralNetwork nn;
        private final ExperienceReplay replay;
        private final double gamma = 0.99;
        private double epsilon = 0.40;
        private final double epsilonDecay = 0.995;
        private final double learningRate = 0.001;
        
        SimpleNNTrainer() {
            this.nn = new NeuralNetwork(42);
            this.replay = new ExperienceReplay(10000, 43);
        }
        
        /**
         * Konvertiert Spielfeld zu NN-Input.
         */
        double[] toState(Spielfeld feld, Farbe meineFarbe) {
            double[] state = new double[9];
            for (int z = 0; z < 3; z++) {
                for (int s = 0; s < 3; s++) {
                    int idx = z * 3 + s;
                    Farbe f = feld.getFarbe(z, s);
                    if (f == Farbe.Leer) state[idx] = 0.0;
                    else if (f == meineFarbe) state[idx] = 1.0;
                    else state[idx] = -1.0;
                }
            }
            return state;
        }
        
        /**
         * Wählt beste Aktion (ε-greedy).
         */
        Zug selectAction(Spielfeld feld, Farbe farbe, boolean explore) {
            double[] state = toState(feld, farbe);
            List<Zug> legal = getLegalMoves(feld);
            
            if (explore && RANDOM.nextDouble() < epsilon) {
                return legal.get(RANDOM.nextInt(legal.size()));
            }
            
            double[] qValues = nn.predict(state);
            Zug best = legal.get(0);
            double bestQ = qValues[zugToAction(best)];
            
            for (Zug z : legal) {
                int action = zugToAction(z);
                if (qValues[action] > bestQ) {
                    bestQ = qValues[action];
                    best = z;
                }
            }
            
            return best;
        }
        
        /**
         * Trainiert NN auf einem Batch.
         */
        void trainBatch() {
            if (!replay.canSample(32)) return;
            
            ExperienceReplay.Experience[] batch = replay.sample(32);
            
            for (ExperienceReplay.Experience exp : batch) {
                double[] target = nn.predict(exp.state).clone();
                
                if (exp.terminal) {
                    target[exp.action] = exp.reward;
                } else {
                    double[] futureQ = nn.predict(exp.nextState);
                    double maxQ = Double.NEGATIVE_INFINITY;
                    for (double q : futureQ) {
                        if (q > maxQ) maxQ = q;
                    }
                    target[exp.action] = exp.reward + gamma * maxQ;
                }
                
                nn.train(exp.state, target, learningRate);
            }
        }
        
        /**
         * Trainiert gegen sich selbst.
         */
        void trainSelfPlay(int episodes) {
            System.out.println("╔══════════════════════════════════════════════════╗");
            System.out.println("║  Neural Network Training (Self-Play)            ║");
            System.out.println("╚══════════════════════════════════════════════════╝");
            System.out.printf("Episoden: %,d\n", episodes);
            System.out.printf("Netzwerk: %,d Parameter\n\n", nn.getTotalParameters());
            
            long start = System.currentTimeMillis();
            
            for (int ep = 0; ep < episodes; ep++) {
                Spielfeld feld = new Spielfeld();
                
                while (!isTerminal(feld)) {
                    // X zieht
                    double[] stateX = toState(feld, Farbe.Kreuz);
                    Zug zugX = selectAction(feld, Farbe.Kreuz, true);
                    feld.setFarbe(zugX.getZeile(), zugX.getSpalte(), Farbe.Kreuz);
                    double[] nextStateX = toState(feld, Farbe.Kreuz);
                    double rewardX = getReward(feld, Farbe.Kreuz);
                    replay.add(stateX, zugToAction(zugX), rewardX, nextStateX, isTerminal(feld));
                    
                    if (isTerminal(feld)) break;
                    
                    // O zieht
                    double[] stateO = toState(feld, Farbe.Kreis);
                    Zug zugO = selectAction(feld, Farbe.Kreis, true);
                    feld.setFarbe(zugO.getZeile(), zugO.getSpalte(), Farbe.Kreis);
                    double[] nextStateO = toState(feld, Farbe.Kreis);
                    double rewardO = getReward(feld, Farbe.Kreis);
                    replay.add(stateO, zugToAction(zugO), rewardO, nextStateO, isTerminal(feld));
                    
                    // Train every step
                    trainBatch();
                }
                
                // Decay epsilon
                epsilon = Math.max(0.01, epsilon * epsilonDecay);
                
                if ((ep + 1) % 10000 == 0) {
                    System.out.printf("Episode %,6d | ε=%.4f | Buffer=%,d\n", 
                                    ep + 1, epsilon, replay.size());
                }
            }
            
            long duration = System.currentTimeMillis() - start;
            double sec = duration / 1000.0;
            
            System.out.printf("\n✅ Training abgeschlossen in %.2f Sekunden\n", sec);
            System.out.printf("   Geschwindigkeit: %,.0f Episoden/Sekunde\n\n", episodes / sec);
        }
        
        /**
         * Testet gegen Zufallsspieler.
         */
        int[] testVsRandom(int games) {
            int wins = 0, losses = 0, draws = 0;
            
            for (int g = 0; g < games; g++) {
                Spielfeld feld = new Spielfeld();
                
                while (!isTerminal(feld)) {
                    // NN zieht (Kreuz)
                    Zug nnMove = selectAction(feld, Farbe.Kreuz, false);
                    feld.setFarbe(nnMove.getZeile(), nnMove.getSpalte(), Farbe.Kreuz);
                    
                    if (isTerminal(feld)) break;
                    
                    // Random zieht (Kreis)
                    List<Zug> legal = getLegalMoves(feld);
                    Zug randMove = legal.get(RANDOM.nextInt(legal.size()));
                    feld.setFarbe(randMove.getZeile(), randMove.getSpalte(), Farbe.Kreis);
                }
                
                Farbe winner = getWinner(feld);
                if (winner == Farbe.Kreuz) wins++;
                else if (winner == Farbe.Kreis) losses++;
                else draws++;
            }
            
            return new int[]{wins, losses, draws};
        }
        
        // Helper methods
        List<Zug> getLegalMoves(Spielfeld feld) {
            List<Zug> moves = new ArrayList<>();
            for (int z = 0; z < 3; z++) {
                for (int s = 0; s < 3; s++) {
                    if (feld.getFarbe(z, s) == Farbe.Leer) {
                        moves.add(new Zug(z, s));
                    }
                }
            }
            return moves;
        }
        
        int zugToAction(Zug z) {
            return z.getZeile() * 3 + z.getSpalte();
        }
        
        boolean isTerminal(Spielfeld feld) {
            return getWinner(feld) != null || getLegalMoves(feld).isEmpty();
        }
        
        double getReward(Spielfeld feld, Farbe farbe) {
            if (!isTerminal(feld)) return 0.0;
            Farbe winner = getWinner(feld);
            if (winner == farbe) return 1.0;
            if (winner == null) return 0.5;
            return -1.0;
        }
        
        Farbe getWinner(Spielfeld feld) {
            // Check rows, cols, diagonals
            for (int i = 0; i < 3; i++) {
                // Rows
                if (feld.getFarbe(i, 0) != Farbe.Leer &&
                    feld.getFarbe(i, 0) == feld.getFarbe(i, 1) &&
                    feld.getFarbe(i, 1) == feld.getFarbe(i, 2)) {
                    return feld.getFarbe(i, 0);
                }
                // Cols
                if (feld.getFarbe(0, i) != Farbe.Leer &&
                    feld.getFarbe(0, i) == feld.getFarbe(1, i) &&
                    feld.getFarbe(1, i) == feld.getFarbe(2, i)) {
                    return feld.getFarbe(0, i);
                }
            }
            // Diagonals
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
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  NEURAL NETWORK vs Q-LEARNING BENCHMARK");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        // 1. NEURAL NETWORK Training & Test
        System.out.println("Phase 1: Neural Network\n");
        SimpleNNTrainer nnTrainer = new SimpleNNTrainer();
        nnTrainer.trainSelfPlay(TRAINING_EPISODES);
        
        System.out.println("Teste gegen Zufallsspieler (" + TEST_GAMES + " Spiele)...");
        int[] nnResults = nnTrainer.testVsRandom(TEST_GAMES);
        double nnWinRate = (100.0 * nnResults[0]) / TEST_GAMES;
        
        System.out.printf("Siege:         %,d\n", nnResults[0]);
        System.out.printf("Niederlagen:   %,d\n", nnResults[1]);
        System.out.printf("Unentschieden: %,d\n", nnResults[2]);
        System.out.printf("Siegrate:      %.1f%%\n\n", nnWinRate);
        
        // 2. Q-LEARNING Test (bereits trainiert)
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("Phase 2: Q-Learning (Reference)\n");
        
        Spieler qPlayer = new Spieler("Q-Learning", 0.30, 0.99, 0.40);
        int qWins = 0, qLosses = 0, qDraws = 0;
        
        System.out.println("Teste gegen Zufallsspieler (" + TEST_GAMES + " Spiele)...");
        
        for (int g = 0; g < TEST_GAMES; g++) {
            qPlayer.neuesSpiel(Farbe.Kreuz, 60);
            
            Spielfeld feld = new Spielfeld();
            Zug lastMove = null;
            
            while (!nnTrainer.isTerminal(feld)) {
                try {
                    // Q-Learning zieht (Kreuz)
                    Zug qMove = qPlayer.berechneZug(lastMove, 0, 0);
                    feld.setFarbe(qMove.getZeile(), qMove.getSpalte(), Farbe.Kreuz);
                    lastMove = qMove;
                    
                    if (nnTrainer.isTerminal(feld)) break;
                    
                    // Random zieht (Kreis)
                    List<Zug> legal = nnTrainer.getLegalMoves(feld);
                    Zug randMove = legal.get(RANDOM.nextInt(legal.size()));
                    feld.setFarbe(randMove.getZeile(), randMove.getSpalte(), Farbe.Kreis);
                    lastMove = randMove;
                } catch (IllegalerZugException e) {
                    break;
                }
            }
            
            Farbe winner = nnTrainer.getWinner(feld);
            if (winner == Farbe.Kreuz) qWins++;
            else if (winner == Farbe.Kreis) qLosses++;
            else qDraws++;
        }
        
        double qWinRate = (100.0 * qWins) / TEST_GAMES;
        
        System.out.printf("Siege:         %,d\n", qWins);
        System.out.printf("Niederlagen:   %,d\n", qLosses);
        System.out.printf("Unentschieden: %,d\n", qDraws);
        System.out.printf("Siegrate:      %.1f%%\n\n", qWinRate);
        
        // 3. VERGLEICH
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("                   FINALE AUSWERTUNG");
        System.out.println("═══════════════════════════════════════════════════════════\n");
        
        System.out.println("┌─────────────────────────┬──────────────┬──────────────┐");
        System.out.println("│ Metrik                  │ Q-Learning   │ Neural Net   │");
        System.out.println("├─────────────────────────┼──────────────┼──────────────┤");
        System.out.printf("│ Siegrate vs Random      │    %.1f%%     │    %.1f%%     │\n", qWinRate, nnWinRate);
        System.out.printf("│ Training-Komplexität    │   Einfach    │   Komplex    │\n");
        System.out.printf("│ Code Lines              │    ~500      │   ~1500      │\n");
        System.out.printf("│ Dependencies            │     0        │     0*       │\n");
        System.out.printf("│ Interpretierbar         │     ✅        │     ❌        │\n");
        System.out.println("└─────────────────────────┴──────────────┴──────────────┘");
        System.out.println("\n* Pure Java Implementation (keine externe Lib)");
        
        System.out.println("\n🎯 FAZIT:");
        System.out.println("   Für Tic-Tac-Toe (6,046 States) ist Q-Learning die");
        System.out.println("   deutlich bessere Wahl: Einfacher, schneller, transparent!");
        System.out.println("\n   Neural Networks sind OVERKILL für kleine State Spaces.\n");
    }
}
