package algs.week4.puzzle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

/**
 * Solver. First, insert the initial search node (the initial board, 0 moves,
 * and a null predecessor search node) into a priority queue. Then, delete from
 * the priority queue the search node with the minimum priority, and insert onto
 * the priority queue all neighboring search nodes (those that can be reached in
 * one move from the dequeued search node). Repeat this procedure until the
 * search node dequeued corresponds to a goal board.
 */
public class Solver {
  private final boolean solvable;
  private final int movesTotal;
  // use this one to store the answer
  // the elements dequeued from pq is not the answer
  // it contains other nodes
  // need to link the answer node by tracing back from goal to initial
  private final Node ptrGoal;

  /**
   * find a solution to the initial board (using the A* algorithm).
   */
  public Solver(Board initial) {

    // boundary check
    if (initial == null) {
      throw new IllegalArgumentException();
    }

    // initialize default values
    boolean solvableLocal = false;

    // first search node
    Node node = new Node(initial, 0, null);
    Node nodeTwin = new Node(initial.twin(), 0, null);

    // initialize priority queue
    MinPQ<Node> queue = new MinPQ<Node>();
    MinPQ<Node> queueTwin = new MinPQ<Node>();
    queue.insert(node);
    queueTwin.insert(nodeTwin);

    // initialize nodeGoal
    Node nodeGoal = null;

    // search loop
    while (true) {

      // deqeue
      Node curNode = queue.delMin();
      if (curNode.getBoard().isGoal()) {
        solvableLocal = true;
        // only add to the result in advance when problem is solved
        nodeGoal = curNode;
        break;
      }

      Node curNodeTwin = queueTwin.delMin();
      if (curNodeTwin.getBoard().isGoal()) {
        // don't add curNodeTwin because it's not needed.
        break;
      }

      // enqeue original solver
      for (Board neighbor : curNode.getBoard().neighbors()) {
        if (curNode.getPredecessor() != null
            && neighbor.equals(curNode.getPredecessor().getBoard())) {
          continue;
        }
        queue.insert(new Node(neighbor, curNode.getMoves() + 1, curNode));
      }

      // enqeue twin solver
      for (Board neighbor : curNodeTwin.getBoard().neighbors()) {
        if (curNodeTwin.getPredecessor() != null
            && neighbor.equals(curNodeTwin.getPredecessor().getBoard())) {
          continue;
        }
        queueTwin.insert(new Node(neighbor, curNodeTwin.getMoves() + 1, curNodeTwin));
      }
    }

    solvable = solvableLocal;
    if (solvable) {
      ptrGoal = nodeGoal;
      movesTotal = ptrGoal.getMoves();
    } else {
      ptrGoal = null;
      movesTotal = -1;
    }
  }

  /**
   * data type associated with priority queue. note: must construct predecessor
   * node
   */
  private class Node implements Comparable<Node> {
    private final int moves;
    private final Board thisBoard;
    private int manhattan;
    private boolean isComputedManhattan;
    private final Node nodePre;

    /**
     * constructor.
     */
    public Node(Board board, int steps, Node pre) {
      thisBoard = board;
      moves = steps;
      nodePre = pre;
    }

    // get board
    public Board getBoard() {
      return thisBoard;
    }

    // get moves so far
    public int getMoves() {
      return moves;
    }

    // get preNode
    public Node getPredecessor() {
      return nodePre;
    }

    // implement compare function
    public int compareTo(Node other) {
      // in fact, also use hamming downgrads performance
      return this.getManhattan() - other.getManhattan();
    }

    // return manhattan distance
    private int getManhattan() {
      if (!isComputedManhattan) {
        manhattan = thisBoard.manhattan() + moves;
        isComputedManhattan = true;
      }
      return manhattan;
    }
  }

  /**
   * is the initial board solvable?.
   */
  public boolean isSolvable() {
    return solvable;
  }

  /**
   * min number of moves to solve initial board, -1 if unsolvable.
   */
  public int moves() {
    return movesTotal;
  }

  /**
   * sequence of boards in a shortest solution, null if unsolvable.
   */
  public Iterable<Board> solution() {

    // deal with unsolvable case
    if (!isSolvable()) {
      return null;
    }

    // else solvable
    LinkedList<Board> solutionList = new LinkedList<Board>();
    solutionList.add(ptrGoal.getBoard());
    Node ptrNode = ptrGoal;
    while (ptrNode.getPredecessor() != null) {
      ptrNode = ptrNode.getPredecessor();
      solutionList.addFirst(ptrNode.getBoard());
    }
    return solutionList;
  }

  /**
   * solve a slider puzzle (given below).
   */
  public static void main(String[] args) {

    // create initial board from file
    In in = new In(args[0]);
    int n = in.readInt();
    int[][] blocks = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        blocks[i][j] = in.readInt();
      }
    }
    Board initial = new Board(blocks);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable()) {
      StdOut.println("No solution possible");
    } else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution()) {
        StdOut.println(board);
      }
    }
  }
}