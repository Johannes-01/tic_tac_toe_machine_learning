package tic_tac_toe_mi.nn;

import java.util.Random;

/**
 * Fully Connected Layer (Dense Layer) für Neural Network.
 * Implementiert Forward und Backward Pass mit Bias.
 */
public class Layer {
    
    private final Matrix weights;      // Gewichte (outputSize × inputSize)
    private final double[] bias;       // Bias (outputSize)
    private final ActivationFunction activation;
    
    // Cache für Backpropagation
    private double[] lastInput;        // Input vom Forward Pass
    private double[] lastZ;            // Weighted Sum (vor Aktivierung)
    private double[] lastOutput;       // Output (nach Aktivierung)
    
    private final int inputSize;
    private final int outputSize;
    
    /**
     * Erstellt Layer mit Xavier-Initialisierung.
     */
    public Layer(int inputSize, int outputSize, ActivationFunction activation, Random random) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.activation = activation;
        
        // Xavier/He Initialization für bessere Konvergenz
        this.weights = Matrix.randomXavier(outputSize, inputSize, random);
        this.bias = new double[outputSize];
        
        // Bias mit kleinen Werten initialisieren
        for (int i = 0; i < outputSize; i++) {
            bias[i] = (random.nextDouble() * 2 - 1) * 0.01;
        }
    }
    
    /**
     * Forward Pass: Berechnet Output aus Input.
     * output = activation(W * input + b)
     */
    public double[] forward(double[] input) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException(
                String.format("Expected input size %d, got %d", inputSize, input.length)
            );
        }
        
        // Cache für Backpropagation
        this.lastInput = input.clone();
        
        // z = W * input + b (Weighted Sum)
        this.lastZ = weights.multiply(input);
        for (int i = 0; i < outputSize; i++) {
            lastZ[i] += bias[i];
        }
        
        // output = activation(z)
        this.lastOutput = activation.activate(lastZ);
        
        return lastOutput.clone();
    }
    
    /**
     * Backward Pass: Berechnet Gradienten und gibt Error zurück.
     * 
     * @param gradOutput Gradient vom nächsten Layer (∂L/∂output)
     * @param learningRate Learning Rate für Weight Update
     * @return Gradient für vorherigen Layer (∂L/∂input)
     */
    public double[] backward(double[] gradOutput, double learningRate) {
        if (gradOutput.length != outputSize) {
            throw new IllegalArgumentException("gradOutput size mismatch");
        }
        
        // Gradient bzgl. z: dL/dz = dL/dOutput * dOutput/dz
        double[] gradZ = new double[outputSize];
        double[] activationDerivatives = activation.derivative(lastZ);
        
        for (int i = 0; i < outputSize; i++) {
            gradZ[i] = gradOutput[i] * activationDerivatives[i];
        }
        
        // Update Weights: W -= learningRate * (gradZ ⊗ input^T)
        weights.updateWeights(lastInput, gradZ, learningRate);
        
        // Update Bias: b -= learningRate * gradZ
        for (int i = 0; i < outputSize; i++) {
            bias[i] -= learningRate * gradZ[i];
        }
        
        // Gradient für vorherigen Layer: dL/dInput = W^T * gradZ
        return weights.multiplyTranspose(gradZ);
    }
    
    /**
     * Kopiert Gewichte von anderem Layer (für Target Network).
     */
    public void copyWeightsFrom(Layer other) {
        if (this.inputSize != other.inputSize || this.outputSize != other.outputSize) {
            throw new IllegalArgumentException("Layer dimensions must match");
        }
        
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                this.weights.set(i, j, other.weights.get(i, j));
            }
            this.bias[i] = other.bias[i];
        }
    }
    
    public int getInputSize() {
        return inputSize;
    }
    
    public int getOutputSize() {
        return outputSize;
    }
    
    public Matrix getWeights() {
        return weights;
    }
    
    public double[] getBias() {
        return bias.clone();
    }
    
    /**
     * Debugging: Layer-Info.
     */
    @Override
    public String toString() {
        return String.format("Layer [%d → %d, %s]", inputSize, outputSize, activation);
    }
}
