package tic_tac_toe_mi.nn;

import java.util.Random;
import java.io.*;

/**
 * Multi-Layer Perceptron (MLP) Neural Network.
 * Implementiert Deep Q-Network (DQN) für Tic-Tac-Toe.
 * 
 * Architektur: 9 → 128 → 64 → 9
 * - Input: 9 Felder (Boardstate: -1=O, 0=Leer, 1=X)
 * - Hidden Layers: 128 → 64 Neuronen (ReLU)
 * - Output: 9 Q-Values für jede mögliche Aktion (Linear)
 */
public class NeuralNetwork {
    
    private final Layer[] layers;
    private final Random random;
    
    // Netzwerk-Konfiguration
    public static final int INPUT_SIZE = 9;
    public static final int OUTPUT_SIZE = 9;
    private static final int[] DEFAULT_HIDDEN = {128, 64};
    
    /**
     * Erstellt Neural Network mit Default-Architektur (9-128-64-9).
     */
    public NeuralNetwork(long seed) {
        this(DEFAULT_HIDDEN, seed);
    }
    
    /**
     * Erstellt Neural Network mit custom Hidden Layers.
     * 
     * @param hiddenSizes Array mit Größen der Hidden Layers
     * @param seed Random Seed für reproducibility
     */
    public NeuralNetwork(int[] hiddenSizes, long seed) {
        this.random = new Random(seed);
        
        // Berechne Layer-Struktur: Input → Hidden[0] → ... → Hidden[n] → Output
        int totalLayers = hiddenSizes.length + 1; // Hidden + Output Layer
        this.layers = new Layer[totalLayers];
        
        // Input → Hidden[0]
        layers[0] = new Layer(INPUT_SIZE, hiddenSizes[0], ActivationFunction.RELU, random);
        
        // Hidden[i] → Hidden[i+1]
        for (int i = 1; i < hiddenSizes.length; i++) {
            layers[i] = new Layer(hiddenSizes[i-1], hiddenSizes[i], ActivationFunction.RELU, random);
        }
        
        // Hidden[last] → Output (LINEAR für Q-Values!)
        int lastHiddenSize = hiddenSizes[hiddenSizes.length - 1];
        layers[totalLayers - 1] = new Layer(lastHiddenSize, OUTPUT_SIZE, ActivationFunction.LINEAR, random);
    }
    
    /**
     * Forward Pass: Berechnet Q-Values für gegebenen State.
     * 
     * @param state Board State als double[] (9 Elemente: -1, 0, 1)
     * @return Q-Values für alle 9 Aktionen
     */
    public double[] predict(double[] state) {
        if (state.length != INPUT_SIZE) {
            throw new IllegalArgumentException("State must have " + INPUT_SIZE + " elements");
        }
        
        double[] output = state;
        for (Layer layer : layers) {
            output = layer.forward(output);
        }
        return output;
    }
    
    /**
     * Training Step: Aktualisiert Gewichte basierend auf Loss.
     * Verwendet Mean Squared Error (MSE) Loss.
     * 
     * @param state Input State
     * @param targetQValues Ziel Q-Values (von Bellman Equation)
     * @param learningRate Learning Rate für Gradient Descent
     * @return Loss (MSE) für Monitoring
     */
    public double train(double[] state, double[] targetQValues, double learningRate) {
        // Forward Pass
        double[] predictedQValues = predict(state);
        
        // Berechne Loss (MSE) und Gradienten
        double loss = 0;
        double[] gradOutput = new double[OUTPUT_SIZE];
        
        for (int i = 0; i < OUTPUT_SIZE; i++) {
            double error = predictedQValues[i] - targetQValues[i];
            loss += error * error;
            gradOutput[i] = 2.0 * error / OUTPUT_SIZE; // MSE Gradient
        }
        loss /= OUTPUT_SIZE;
        
        // Backward Pass (Backpropagation)
        double[] gradient = gradOutput;
        for (int i = layers.length - 1; i >= 0; i--) {
            gradient = layers[i].backward(gradient, learningRate);
        }
        
        return loss;
    }
    
    /**
     * Batch Training: Trainiert auf mehreren Samples gleichzeitig.
     * Verwendet Average Gradient über Batch (Mini-Batch Gradient Descent).
     */
    public double trainBatch(double[][] states, double[][] targets, double learningRate) {
        double totalLoss = 0;
        
        for (int i = 0; i < states.length; i++) {
            totalLoss += train(states[i], targets[i], learningRate);
        }
        
        return totalLoss / states.length;
    }
    
    /**
     * Kopiert Gewichte von anderem Network (für Target Network in DQN).
     */
    public void copyWeightsFrom(NeuralNetwork other) {
        if (this.layers.length != other.layers.length) {
            throw new IllegalArgumentException("Networks must have same architecture");
        }
        
        for (int i = 0; i < layers.length; i++) {
            this.layers[i].copyWeightsFrom(other.layers[i]);
        }
    }
    
    /**
     * Speichert Network-Weights in Datei.
     */
    public void save(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            
            // Speichere Architektur
            int[] hiddenSizes = new int[layers.length - 1];
            for (int i = 0; i < hiddenSizes.length; i++) {
                hiddenSizes[i] = layers[i].getOutputSize();
            }
            oos.writeObject(hiddenSizes);
            
            // Speichere Weights & Bias für jeden Layer
            for (Layer layer : layers) {
                Matrix weights = layer.getWeights();
                for (int i = 0; i < weights.getRows(); i++) {
                    for (int j = 0; j < weights.getCols(); j++) {
                        oos.writeDouble(weights.get(i, j));
                    }
                }
                
                double[] bias = layer.getBias();
                for (double b : bias) {
                    oos.writeDouble(b);
                }
            }
        }
    }
    
    /**
     * Lädt Network-Weights aus Datei.
     */
    public static NeuralNetwork load(String filename, long seed) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            
            // Lade Architektur
            int[] hiddenSizes = (int[]) ois.readObject();
            NeuralNetwork nn = new NeuralNetwork(hiddenSizes, seed);
            
            // Lade Weights & Bias für jeden Layer
            for (Layer layer : nn.layers) {
                Matrix weights = layer.getWeights();
                for (int i = 0; i < weights.getRows(); i++) {
                    for (int j = 0; j < weights.getCols(); j++) {
                        weights.set(i, j, ois.readDouble());
                    }
                }
                
                double[] bias = layer.getBias();
                for (int i = 0; i < bias.length; i++) {
                    bias[i] = ois.readDouble();
                }
            }
            
            return nn;
        }
    }
    
    /**
     * Gibt Anzahl trainbarer Parameter zurück.
     */
    public int getTotalParameters() {
        int total = 0;
        for (Layer layer : layers) {
            int weights = layer.getInputSize() * layer.getOutputSize();
            int bias = layer.getOutputSize();
            total += weights + bias;
        }
        return total;
    }
    
    /**
     * Debugging: Network-Info.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NeuralNetwork [\n");
        for (int i = 0; i < layers.length; i++) {
            sb.append(String.format("  Layer %d: %s\n", i, layers[i]));
        }
        sb.append(String.format("  Total Parameters: %,d\n", getTotalParameters()));
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Test: Einfacher Sanity Check.
     */
    public static void main(String[] args) {
        System.out.println("=== Neural Network Test ===\n");
        
        NeuralNetwork nn = new NeuralNetwork(42);
        System.out.println(nn);
        
        // Test Forward Pass
        double[] testState = {1, 0, -1, 0, 1, 0, -1, 0, 0}; // Beispiel-Board
        double[] qValues = nn.predict(testState);
        
        System.out.println("\nTest Forward Pass:");
        System.out.println("Input: " + java.util.Arrays.toString(testState));
        System.out.println("Q-Values: " + java.util.Arrays.toString(qValues));
        
        // Test Training Step
        double[] target = new double[9];
        target[4] = 1.0; // Aktion 4 (Mitte) soll höchsten Q-Value haben
        
        System.out.println("\nTraining für 1000 Iterationen (Aktion 4 soll am besten sein)...");
        for (int i = 0; i < 1000; i++) {
            nn.train(testState, target, 0.01);
        }
        
        qValues = nn.predict(testState);
        System.out.println("Q-Values nach Training: " + java.util.Arrays.toString(qValues));
        
        int bestAction = 0;
        for (int i = 1; i < qValues.length; i++) {
            if (qValues[i] > qValues[bestAction]) bestAction = i;
        }
        System.out.println("Beste Aktion: " + bestAction + " (erwartet: 4)");
        
        if (bestAction == 4) {
            System.out.println("\n✅ Neural Network funktioniert korrekt!");
        } else {
            System.out.println("\n⚠️ Training konvergiert nicht wie erwartet.");
        }
    }
}
