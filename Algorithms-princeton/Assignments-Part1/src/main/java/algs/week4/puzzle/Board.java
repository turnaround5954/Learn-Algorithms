package algs.week4.puzzle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

/**
 * Board. Write a program to solve the 8-puzzle problem (and its natural
 * generalizations) using the A* search algorithm.
 */
public class Board {
  private static final int BLANK = 0;
  private final int rowBlank;
  private final int colBlank;
  private final int size;
  private final int[] board;

  /**
   * construct a board from an n-by-n array of blocks. (where blocks[i][j] = block
   * in row i, column j)
   */
  public Board(int[][] blocks) {
    size = blocks.length;
    if (size < 2) {
      throw new IllegalArgumentException("Dim must be at least 2");
    }
    board = new int[size * size];
    int row = 0;
    int col = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (blocks[i][j] == BLANK) {
          row = i;
          col = j;
        }
        board[getIdx(i, j)] = blocks[i][j];
      }
    }
    rowBlank = row;
    colBlank = col;
  }

  /**
   * get index. helper function to transfer 2d index to 1d index
   */
  private int getIdx(int i, int j) {
    return size * i + j;
  }

  /**
   * board dimension n.
   */
  public int dimension() {
    return size;
  }

  /**
   * number of blocks out of place.
   */
  public int hamming() {
    int result = 0;
    for (int i = 0; i < board.length - 1; i++) {
      if (board[i] != i + 1) {
        result++;
      }
    }
    return result;
  }

  /**
   * sum of Manhattan distances between blocks and goal.
   */
  public int manhattan() {
    int result = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        int block = board[getIdx(i, j)];
        if (block == BLANK) {
          continue;
        }
        int row = (block - 1) / size;
        int col = (block - 1) % size;
        result += Math.abs(row - i) + Math.abs(col - j);
      }
    }
    return result;
  }

  /**
   * is this board the goal board?.
   */
  public boolean isGoal() {
    for (int i = 0; i < board.length - 1; i++) {
      if (board[i] != i + 1) {
        return false;
      }
    }
    return true;
  }

  /**
   * a board that is obtained by exchanging any pair of blocks.
   */
  public Board twin() {
    int[][] twin = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        twin[i][j] = board[getIdx(i, j)];
      }
    }
    // swap non-blank blocks
    int excRow = rowBlank + 1;
    if (excRow >= size) {
      excRow = rowBlank - 1;
    }
    int tmp = twin[excRow][0];
    twin[excRow][0] = twin[excRow][1];
    twin[excRow][1] = tmp;
    return new Board(twin);
  }

  /**
   * does this board equal y?.
   */
  public boolean equals(Object y) {
    if (y == this) {
      return true;
    }
    if (y == null) {
      return false;
    }
    if (y.getClass() != this.getClass()) {
      return false;
    }
    Board that = (Board) y;
    if (this.dimension() != that.dimension()) {
      return false;
    }
    for (int i = 0; i < this.board.length; i++) {
      if (this.board[i] != that.board[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * all neighboring boards.
   */
  public Iterable<Board> neighbors() {
    LinkedList<Board> neighbBoards = new LinkedList<Board>();
    int row = rowBlank;
    int col = colBlank;

    // move right
    col++;
    if (col < size) {
      neighbBoards.add(generateNeighb(row, col));
    }
    col--;

    // move above
    row--;
    if (row >= 0) {
      neighbBoards.add(generateNeighb(row, col));
    }
    row++;

    // move left
    col--;
    if (col >= 0) {
      neighbBoards.add(generateNeighb(row, col));
    }
    col++;

    // move below
    row++;
    if (row < size) {
      neighbBoards.add(generateNeighb(row, col));
    }
    return neighbBoards;
  }

  /**
   * clone and swap input: row and col to swap with the blank block.
   */
  private Board generateNeighb(int row, int col) {
    // clone
    int[][] blocks = new int[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        blocks[i][j] = board[getIdx(i, j)];
      }
    }
    // swap
    blocks[rowBlank][colBlank] = blocks[row][col];
    blocks[row][col] = BLANK;
    return new Board(blocks);
  }

  /**
   * string representation of this board (in the output format specified below).
   */
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append(size + "\n");
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        s.append(String.format("%2d ", board[getIdx(i, j)]));
      }
      s.append("\n");
    }
    return s.toString();
  }

  /**
   * unit tests (not graded).
   */
  public static void main(String[] args) {
    String[] arguments = new String[1];
    arguments[0] = "algs/src/test/java/algs/week4/puzzle/puzzle3x3-05.txt";
    // for each filename
    for (String filename : arguments) {
      // read in the board specified in the filename
      In in = new In(filename);
      int n = in.readInt();
      int[][] tiles = new int[n][n];
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          tiles[i][j] = in.readInt();
        }
      }

      // solve the slider puzzle
      Board initial = new Board(tiles);
      StdOut.println("Initial\n");
      StdOut.print(initial);
      StdOut.println("hamming: " + initial.hamming());
      StdOut.println("manhattan: " + initial.manhattan());
      StdOut.println("twin\n");
      StdOut.print(initial.twin());
      StdOut.println("twin's hamming: " + initial.twin().hamming());
      StdOut.println("twin's manhattan: " + initial.twin().manhattan());
      StdOut.println("neighbors\n");
      for (Board e : initial.neighbors()) {
        StdOut.print(e);
      }
    }
  }
}