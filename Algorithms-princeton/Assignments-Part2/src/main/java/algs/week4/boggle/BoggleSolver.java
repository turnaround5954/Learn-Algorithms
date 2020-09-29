package algs.week4.boggle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

/**
 * BoggleSolver, Write a program to play the word game Boggle.
 */
public class BoggleSolver {

  // dictionary tree
  private static final int R = 26;
  private static final char OFFSET = 'A';
  private final TrieSymTable tst;

  /**
   * Initializes the data structure using the given array of strings as the
   * dictionary.(You can assume each word in the dictionary contains only the
   * uppercase letters A through Z.)
   */
  public BoggleSolver(String[] dictionary) {

    // use a 26-way trie to store dictionary
    tst = new TrieSymTable();
    for (String s : dictionary) {
      tst.put(s, s);
    }

  }

  private static class Node {
    private String val;
    private Node[] next = new Node[R];
  }

  private static class TrieSymTable {

    private Node root = new Node();

    /**
     * return Node.
     */
    public Node get(Node x, String key, int d) {
      if (x == null) {
        return null;
      }
      if (d == key.length()) {
        return x;
      }
      char c = key.charAt(d);
      return get(x.next[c - OFFSET], key, d + 1);
    }

    /**
     * Insert key-value pair into the symbol table.
     */
    public void put(String key, String val) {
      root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, String val, int d) {
      if (x == null) {
        x = new Node();
      }
      if (d == key.length()) {
        x.val = val;
        return x;
      }
      char c = key.charAt(d);
      x.next[c - OFFSET] = put(x.next[c - OFFSET], key, val, d + 1);
      return x;
    }

  }

  /**
   * Returns the set of all valid words in the given Boggle board, as an Iterable.
   */
  public Iterable<String> getAllValidWords(BoggleBoard board) {

    // get board's borders
    int rows = board.rows();
    int cols = board.cols();

    // do dfs for each die on board
    WordSearch wordSearch = new WordSearch(board, rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        wordSearch.addWords(i, j);
      }
    }
    return wordSearch.wordsValid;
  }

  /**
   * depth first search for word in dictionary.
   */
  private class WordSearch {

    // fields used
    private final HashSet<String> wordsValid;
    private final BoggleBoard searchBoard;
    private final int rows;
    private final int cols;
    private boolean[][] marked;

    // initialization
    public WordSearch(BoggleBoard bd, int rs, int cs) {
      searchBoard = bd;
      rows = rs;
      cols = cs;
      marked = new boolean[rows][cols];
      wordsValid = new HashSet<String>();
    }

    // add valid words
    public void addWords(int i, int j) {

      // call dfs and adds to words
      dfs(i, j, tst.root);
    }

    // recursive dfs
    private void dfs(int i, int j, Node x) {

      // check boundary
      if (i < 0 || i >= rows || j < 0 || j >= cols || marked[i][j] || x == null) {
        return;
      }

      // get char
      char prefix = searchBoard.getLetter(i, j);

      // query next
      Node nextX = x.next[prefix - OFFSET];

      // check if it's a prefix to some words
      if (nextX == null) {
        return;
      }

      // deal with Qu case
      if (prefix == 'Q') {
        nextX = nextX.next['U' - OFFSET];
        if (nextX == null) {
          return;
        }
      }

      // add to valid words
      if (nextX.val != null && nextX.val.length() > 2) {
        wordsValid.add(nextX.val);
      }

      // make it a simple path
      marked[i][j] = true;

      // make recursive calls
      dfs(i - 1, j - 1, nextX);
      dfs(i - 1, j, nextX);
      dfs(i - 1, j + 1, nextX);
      dfs(i, j - 1, nextX);
      dfs(i, j + 1, nextX);
      dfs(i + 1, j - 1, nextX);
      dfs(i + 1, j, nextX);
      dfs(i + 1, j + 1, nextX);

      // make the vertex available
      marked[i][j] = false;
    }

  }

  /**
   * Returns the score of the given word if it is in the dictionary, zero
   * otherwise. (You can assume the word contains only the uppercase letters A
   * through Z.)
   */
  public int scoreOf(String word) {
    Node node = tst.get(tst.root, word, 0);
    if (node == null || node.val == null) {
      return 0;
    }
    int score = 0;
    // value depends on length
    switch (node.val.length()) {
      case 0:
      case 1:
      case 2:
        break;
      case 3:
      case 4:
        score = 1;
        break;
      case 5:
        score = 2;
        break;
      case 6:
        score = 3;
        break;
      case 7:
        score = 5;
        break;
      default:
        score = 11;
        break;
    }
    return score;
  }

  /**
   * test client.
   */
  public static void main(String[] args) {
    In in = new In(args[0]);
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard(args[1]);
    int score = 0;
    for (String word : solver.getAllValidWords(board)) {
      StdOut.println(word);
      score += solver.scoreOf(word);
    }
    StdOut.println("Score = " + score);
  }

}