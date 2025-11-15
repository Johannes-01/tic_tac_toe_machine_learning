package tic_tac_toe_mi.nn;

/**
 * Quick NN Test - Reduzierte Version für schnellen Test.
 */
public class QuickNNTest {
    public static void main(String[] args) throws Exception {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  QUICK NN TEST (10k episodes)");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        NNDemo.SimpleNNTrainer trainer = new NNDemo.SimpleNNTrainer();
        
        // Training
        long start = System.currentTimeMillis();
        trainer.trainSelfPlay(10_000);
        long trainTime = System.currentTimeMillis() - start;
        
        // Testing
        int[] results = trainer.testVsRandom(1_000);
        double winRate = (100.0 * results[0]) / 1_000;
        
        System.out.println("\n📊 ERGEBNISSE:");
        System.out.printf("Training-Zeit:  %.2f Sekunden\n", trainTime / 1000.0);
        System.out.printf("Siegrate:       %.1f%%\n", winRate);
        System.out.printf("Siege:          %d\n", results[0]);
        System.out.printf("Niederlagen:    %d\n", results[1]);
        System.out.printf("Unentschieden:  %d\n\n", results[2]);
        
        if (winRate > 60) {
            System.out.println("✅ NN funktioniert! Aber ist es besser als Q-Learning?");
            System.out.println("   Vergleich: Q-Learning schafft 73.6% in 0.18s");
            System.out.printf("   NN schafft %.1f%% in %.2fs\n", winRate, trainTime/1000.0);
        } else {
            System.out.println("⚠️  NN braucht mehr Training für gute Performance.");
        }
    }
}
