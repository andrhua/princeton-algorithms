import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] results;
    private int trials;
    private final float moreThanOnce = 1.96f;
    private double mean;
    private double stddev;

    public PercolationStats(int n, int trials){
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException();
        this.trials = trials;
        results = new double[trials];
        for (int i = 0; i < trials; i++){
            Percolation system = new Percolation(n);
            int[] indices = StdRandom.permutation(n*n);
            int j = 0;
            while (!system.percolates()){
                int index = indices[j++] + 1;
                int row = (index - 1) / n;
                system.open(row + 1, index - n * row);
            }
            results[i] = (double)system.numberOfOpenSites() / (n*n);
        }
    }

    public double mean(){
        mean = StdStats.mean(results);
        return mean;
    }

    public double stddev(){
        stddev = StdStats.stddev(results);
        return stddev;
    }

    public double confidenceLo(){
        return mean - moreThanOnce * stddev / Math.sqrt(trials);
    }

    public double confidenceHi(){
        return mean + moreThanOnce * stddev / Math.sqrt(trials);
    }

    public static void main(String[] args){
        int n = Integer.parseInt(args[0]), T = Integer.parseInt(args[1]);
        PercolationStats stats = new PercolationStats(n, T);
        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");
        /*String confidence = "95% confidence interval";
        int width = confidence.length();
        String f = "%-".concat(String.valueOf(width)).concat("s = ");
        System.out.printf(f.concat("%f\n"), "mean", stats.mean());
        System.out.printf(f.concat("%f\n"), "stddev", stats.stddev());
        System.out.printf(f.concat("[%f, %f]"), confidence, stats.confidenceLo(), stats.confidenceHi());*/
    }
}
