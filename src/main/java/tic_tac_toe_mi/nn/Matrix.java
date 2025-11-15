package tic_tac_toe_mi.nn;

import java.util.Random;

/**
 * Einfache Matrix-Klasse für Neural Network Operationen.
 * Minimalistisch: Nur was für Forward/Backward Pass nötig ist.
 */
public class Matrix {
    
    private final double[][] data;
    private final int rows;
    private final int cols;
    
    /**
     * Erstellt Matrix mit gegebenen Dimensionen (initialisiert mit 0).
     */
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }
    
    /**
     * Erstellt Matrix aus 2D-Array.
     */
    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }
    
    /**
     * Erstellt Matrix mit Xavier/He-Initialization.
     * Wichtig für gute Konvergenz!
     */
    public static Matrix randomXavier(int rows, int cols, Random random) {
        Matrix m = new Matrix(rows, cols);
        double scale = Math.sqrt(2.0 / (rows + cols)); // Xavier Init
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.data[i][j] = (random.nextDouble() * 2 - 1) * scale;
            }
        }
        return m;
    }
    
    /**
     * Matrix-Vektor Multiplikation: M * v = result
     * M ist (rows × cols), v ist (cols × 1), result ist (rows × 1)
     */
    public double[] multiply(double[] vector) {
        if (vector.length != cols) {
            throw new IllegalArgumentException("Vector size must match matrix cols");
        }
        
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += data[i][j] * vector[j];
            }
            result[i] = sum;
        }
        return result;
    }
    
    /**
     * Transponierte Matrix-Vektor Multiplikation: M^T * v = result
     * Nötig für Backpropagation!
     */
    public double[] multiplyTranspose(double[] vector) {
        if (vector.length != rows) {
            throw new IllegalArgumentException("Vector size must match matrix rows");
        }
        
        double[] result = new double[cols];
        for (int j = 0; j < cols; j++) {
            double sum = 0;
            for (int i = 0; i < rows; i++) {
                sum += data[i][j] * vector[i];
            }
            result[j] = sum;
        }
        return result;
    }
    
    /**
     * Aktualisiert Gewichte basierend auf Gradienten.
     * weight -= learningRate * gradient
     */
    public void updateWeights(double[] input, double[] gradient, double learningRate) {
        // Outer product: gradient (rows) × input^T (cols)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] -= learningRate * gradient[i] * input[j];
            }
        }
    }
    
    /**
     * Zugriff auf einzelnes Element.
     */
    public double get(int i, int j) {
        return data[i][j];
    }
    
    /**
     * Setzen einzelnes Element.
     */
    public void set(int i, int j, double value) {
        data[i][j] = value;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    /**
     * Deep Copy der Matrix.
     */
    public Matrix copy() {
        Matrix m = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(this.data[i], 0, m.data[i], 0, cols);
        }
        return m;
    }
    
    /**
     * Debugging: Matrix als String.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Matrix [%d × %d]:\n", rows, cols));
        for (int i = 0; i < Math.min(rows, 3); i++) {
            sb.append("  [");
            for (int j = 0; j < Math.min(cols, 5); j++) {
                sb.append(String.format("%8.4f", data[i][j]));
            }
            if (cols > 5) sb.append(" ...");
            sb.append("]\n");
        }
        if (rows > 3) sb.append("  ...\n");
        return sb.toString();
    }
}
