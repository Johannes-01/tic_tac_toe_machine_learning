package tic_tac_toe_mi.nn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Experience Replay Buffer für DQN (Deep Q-Network).
 * Speichert Erfahrungen (State, Action, Reward, NextState) und erlaubt 
 * random sampling für stabiles Training.
 * 
 * Warum Experience Replay?
 * - Bricht Korrelation zwischen aufeinanderfolgenden Samples
 * - Nutzt Daten effizienter (jede Experience mehrfach verwendet)
 * - Stabilisiert Training (verhindert catastrophic forgetting)
 */
public class ExperienceReplay {
    
    /**
     * Eine einzelne Erfahrung (Transition).
     */
    public static class Experience {
        public final double[] state;
        public final int action;
        public final double reward;
        public final double[] nextState;
        public final boolean terminal; // Ist nextState ein End-State?
        
        public Experience(double[] state, int action, double reward, 
                         double[] nextState, boolean terminal) {
            this.state = state;
            this.action = action;
            this.reward = reward;
            this.nextState = nextState;
            this.terminal = terminal;
        }
    }
    
    private final List<Experience> buffer;
    private final int maxSize;
    private final Random random;
    
    /**
     * Erstellt Experience Replay Buffer.
     * 
     * @param maxSize Maximale Anzahl gespeicherter Experiences
     * @param seed Random Seed für Sampling
     */
    public ExperienceReplay(int maxSize, long seed) {
        this.maxSize = maxSize;
        this.buffer = new ArrayList<>(maxSize);
        this.random = new Random(seed);
    }
    
    /**
     * Fügt neue Experience hinzu.
     * Wenn Buffer voll: Überschreibe älteste Experience (FIFO).
     */
    public void add(double[] state, int action, double reward, 
                   double[] nextState, boolean terminal) {
        Experience exp = new Experience(state, action, reward, nextState, terminal);
        
        if (buffer.size() < maxSize) {
            buffer.add(exp);
        } else {
            // FIFO: Ersetze älteste Experience
            int randomIdx = random.nextInt(maxSize);
            buffer.set(randomIdx, exp);
        }
    }
    
    /**
     * Sampelt zufällige Mini-Batch aus Buffer.
     * 
     * @param batchSize Anzahl Samples im Batch
     * @return Array von zufälligen Experiences
     */
    public Experience[] sample(int batchSize) {
        if (buffer.size() < batchSize) {
            batchSize = buffer.size();
        }
        
        Experience[] batch = new Experience[batchSize];
        
        // Random Sampling ohne Replacement innerhalb eines Batches
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < buffer.size(); i++) {
            indices.add(i);
        }
        
        for (int i = 0; i < batchSize; i++) {
            int randomIdx = random.nextInt(indices.size());
            int bufferIdx = indices.remove(randomIdx);
            batch[i] = buffer.get(bufferIdx);
        }
        
        return batch;
    }
    
    /**
     * Gibt aktuelle Anzahl gespeicherter Experiences zurück.
     */
    public int size() {
        return buffer.size();
    }
    
    /**
     * Prüft ob genug Experiences für Training vorhanden sind.
     */
    public boolean canSample(int batchSize) {
        return buffer.size() >= batchSize;
    }
    
    /**
     * Löscht alle Experiences (für neues Training).
     */
    public void clear() {
        buffer.clear();
    }
    
    /**
     * Debugging: Buffer-Info.
     */
    @Override
    public String toString() {
        return String.format("ExperienceReplay [%d / %d experiences]", 
                           buffer.size(), maxSize);
    }
}
