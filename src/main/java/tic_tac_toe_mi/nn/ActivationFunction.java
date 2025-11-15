package tic_tac_toe_mi.nn;

/**
 * Aktivierungsfunktionen für Neural Networks.
 * Implementiert Forward und Backward (Ableitung) für verschiedene Funktionen.
 */
public enum ActivationFunction {
    
    /**
     * ReLU (Rectified Linear Unit): f(x) = max(0, x)
     * Derivative: f'(x) = 1 if x > 0, else 0
     */
    RELU {
        @Override
        public double activate(double x) {
            return Math.max(0, x);
        }
        
        @Override
        public double derivative(double x) {
            return x > 0 ? 1.0 : 0.0;
        }
    },
    
    /**
     * Sigmoid: f(x) = 1 / (1 + e^(-x))
     * Derivative: f'(x) = f(x) * (1 - f(x))
     */
    SIGMOID {
        @Override
        public double activate(double x) {
            return 1.0 / (1.0 + Math.exp(-x));
        }
        
        @Override
        public double derivative(double x) {
            double fx = activate(x);
            return fx * (1.0 - fx);
        }
    },
    
    /**
     * Tanh: f(x) = (e^x - e^(-x)) / (e^x + e^(-x))
     * Derivative: f'(x) = 1 - f(x)^2
     */
    TANH {
        @Override
        public double activate(double x) {
            return Math.tanh(x);
        }
        
        @Override
        public double derivative(double x) {
            double fx = activate(x);
            return 1.0 - fx * fx;
        }
    },
    
    /**
     * Linear/Identity: f(x) = x
     * Derivative: f'(x) = 1
     * Wird für Output-Layer bei Regression verwendet.
     */
    LINEAR {
        @Override
        public double activate(double x) {
            return x;
        }
        
        @Override
        public double derivative(double x) {
            return 1.0;
        }
    };
    
    /**
     * Forward Pass: Wendet Aktivierungsfunktion auf Input an.
     */
    public abstract double activate(double x);
    
    /**
     * Backward Pass: Berechnet Ableitung für Backpropagation.
     */
    public abstract double derivative(double x);
    
    /**
     * Aktiviert einen ganzen Vektor (element-wise).
     */
    public double[] activate(double[] inputs) {
        double[] outputs = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            outputs[i] = activate(inputs[i]);
        }
        return outputs;
    }
    
    /**
     * Berechnet Ableitungen für ganzen Vektor (element-wise).
     */
    public double[] derivative(double[] inputs) {
        double[] derivatives = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            derivatives[i] = derivative(inputs[i]);
        }
        return derivatives;
    }
}
