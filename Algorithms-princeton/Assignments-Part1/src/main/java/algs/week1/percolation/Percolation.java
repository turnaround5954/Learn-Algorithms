package algs.week1.percolation;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
  private final int n;
  private final WeightedQuickUnionUF blockBackWash;
  private final WeightedQuickUnionUF blockFullness;
  private final int top;
  private final int bottom;
  private boolean[] blocks;
  private int openNum;

  // create n-by-n grid, with all sites blocked
  public Percolation(int n) {
    if (n <= 0)
      throw new IllegalArgumentException("n <= 0");
    this.n = n;
    int blocksNum = n * n + 2;
    this.blocks = new boolean[blocksNum];
    blockBackWash = new WeightedQuickUnionUF(blocksNum);
    blockFullness = new WeightedQuickUnionUF(blocksNum);
    top = 0;
    bottom = blocksNum - 1;
    blocks[top] = true;
    blocks[bottom] = true;
    openNum = 0;
    for (int j = 1; j <= n; j++) {
      int idxTopRow = index(1, j);
      blockBackWash.union(top, idxTopRow);
      blockBackWash.union(bottom, index(n, j));
      blockFullness.union(top, idxTopRow);
    }
  }

  // open site (row, col) if it is not open already
  public void open(int row, int col) {
    boundaryCheck(row, col);

    if (isOpen(row, col))
      return;

    int p = index(row, col);
    blocks[p] = true;
    openNum++;

    if (row > 1 && isOpen(row - 1, col)) {
      int q = index(row - 1, col);
      blockBackWash.union(p, q);
      blockFullness.union(p, q);
    }

    if (row < n && isOpen(row + 1, col)) {
      int q = index(row + 1, col);
      blockBackWash.union(p, q);
      blockFullness.union(p, q);
    }
    if (col > 1 && isOpen(row, col - 1)) {
      int q = index(row, col - 1);
      blockBackWash.union(p, q);
      blockFullness.union(p, q);
    }
    if (col < n && isOpen(row, col + 1)) {
      int q = index(row, col + 1);
      blockBackWash.union(p, q);
      blockFullness.union(p, q);
    }
  }

  // is site (row, col) open?
  public boolean isOpen(int row, int col) {
    boundaryCheck(row, col);
    return blocks[index(row, col)];
  }

  // is site (row, col) full?
  public boolean isFull(int row, int col) {
    boundaryCheck(row, col);
    return blockFullness.connected(top, index(row, col)) && isOpen(row, col);
  }

  // number of open sites
  public int numberOfOpenSites() {
    return openNum;
  }

  // does the system percolate?
  public boolean percolates() {
    if (n <= 1)
      return isOpen(1, 1);

    return blockBackWash.connected(top, bottom);
  }

  // convert 2d index to 1d
  private int index(int row, int col) {
    return (row - 1) * n + col;
  }

  // boundary check
  private void boundaryCheck(int row, int col) {
    if (row < 1 || row > n || col < 1 || col > n)
      throw new IllegalArgumentException("Outside prescribed range");
  }

  // test client (optional)
  public static void main(String[] args) {
    int n = 2;
    Percolation p = new Percolation(n);
    while (!p.percolates()) {
      int randRow = StdRandom.uniform(n) + 1;
      int randCol = StdRandom.uniform(n) + 1;
      while (p.isOpen(randRow, randCol)) {
        randRow = StdRandom.uniform(n) + 1;
        randCol = StdRandom.uniform(n) + 1;
      }
      System.out.println(randRow + ", " + randCol);
      p.open(randRow, randCol);
    }
    System.out.println(p.numberOfOpenSites());
  }
}