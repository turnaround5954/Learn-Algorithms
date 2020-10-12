package algs.week1.percolation;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
	private static final double CONFIDENCE_95 = 1.96;
	private final double[] openNums;
	private final int trials;
	private boolean calculatedMean;
	private boolean calculatedStd;
	private double mean;
	private double stddev;

	// perform trials independent experiments on an n-by-n grid
	public PercolationStats(int n, int trials) {
		if (n <= 0 || trials <= 0) {
			throw new IllegalArgumentException("Invalid input n or trials");
		}

		this.trials = trials;
		openNums = new double[trials];

		for (int trial = 0; trial < trials; trial++) {

			Percolation p = new Percolation(n);

			while (!p.percolates()) {
				int randRow = StdRandom.uniform(n) + 1;
				int randCol = StdRandom.uniform(n) + 1;
				while (p.isOpen(randRow, randCol)) {
					randRow = StdRandom.uniform(n) + 1;
					randCol = StdRandom.uniform(n) + 1;
				}
				p.open(randRow, randCol);
			}
			openNums[trial] = (double) p.numberOfOpenSites() / (n * n);
		}
	}

	// sample mean of percolation threshold
	public double mean() {
		if (!calculatedMean) {
			calculatedMean = true;
			mean = StdStats.mean(openNums);
		}
		return mean;
	}

	// sample standard deviation of percolation threshold
	public double stddev() {
		if (!calculatedStd) {
			calculatedStd = true;
			stddev = StdStats.stddev(openNums);
		}
		return stddev;
	}

	// low endpoint of 95% confidence interval
	public double confidenceLo() {
		return mean() - CONFIDENCE_95 * stddev() / Math.sqrt(trials);
	}

	// high endpoint of 95% confidence interval
	public double confidenceHi() {
		return mean() + CONFIDENCE_95 * stddev() / Math.sqrt(trials);
	}

	// test client (described below)
	public static void main(String[] args) {
		int n = Integer.parseInt(args[0]);
		int t = Integer.parseInt(args[1]);
		PercolationStats pStats = new PercolationStats(n, t);
		StdOut.printf("mean                    = %f\n", pStats.mean());
		StdOut.printf("stddev                  = %f\n", pStats.stddev());
		StdOut.printf("95%% confidence interval = [%f, %f]", pStats.confidenceLo(), pStats.confidenceLo());
	}
}
