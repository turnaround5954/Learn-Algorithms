package algs.week1.wordnet;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * SAP. A shortest ancestral path is an ancestral path of minimum total length.
 */
public class SAP {

  // for immutability
  private final Digraph graph;

  /**
   * constructor takes a digraph (not necessarily a DAG).
   */
  public SAP(Digraph digraph) {
    checkNullArg(digraph);
    graph = new Digraph(digraph);
  }

  /**
   * length of shortest ancestral path between v and w; -1 if no such path.
   */
  public int length(int v, int w) {
    checkNullArg(v);
    checkNullArg(w);
    checkVertexRange(v);
    checkVertexRange(w);
    BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
    int shortest = -1;
    for (int vertex = 0; vertex < graph.V(); vertex++) {
      if ((!bfsV.hasPathTo(vertex)) || (!bfsW.hasPathTo(vertex))) {
        continue;
      }
      int dist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
      if (shortest < 0 || dist < shortest) {
        shortest = dist;
      }
    }
    return shortest;
  }

  /**
   * length of shortest ancestral path between any vertex in v and any vertex in
   * w; -1 if no such path.
   */
  public int length(Iterable<Integer> v, Iterable<Integer> w) {
    checkNullArg(v);
    checkNullArg(w);
    checkIterable(v);
    checkIterable(w);
    BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
    int shortest = -1;
    for (int vertex = 0; vertex < graph.V(); vertex++) {
      if ((!bfsV.hasPathTo(vertex)) || (!bfsW.hasPathTo(vertex))) {
        continue;
      }
      int dist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
      if (shortest < 0 || dist < shortest) {
        shortest = dist;
      }
    }
    return shortest;
  }

  /**
   * a common ancestor of v and w that participates in a shortest ancestral path;
   * -1 if no such path.
   */
  public int ancestor(int v, int w) {
    checkNullArg(v);
    checkNullArg(w);
    checkVertexRange(v);
    checkVertexRange(w);
    BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
    int shortest = -1;
    int ancestor = -1;
    for (int vertex = 0; vertex < graph.V(); vertex++) {
      if ((!bfsV.hasPathTo(vertex)) || (!bfsW.hasPathTo(vertex))) {
        continue;
      }
      int dist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
      if (shortest < 0 || dist < shortest) {
        shortest = dist;
        ancestor = vertex;
      }
    }
    return ancestor;
  }

  /**
   * a common ancestor that participates in shortest ancestral path; -1 if no such
   * path.
   */
  public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
    checkNullArg(v);
    checkNullArg(w);
    checkIterable(v);
    checkIterable(w);
    BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(graph, v);
    BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(graph, w);
    int shortest = -1;
    int ancestor = -1;
    for (int vertex = 0; vertex < graph.V(); vertex++) {
      if ((!bfsV.hasPathTo(vertex)) || (!bfsW.hasPathTo(vertex))) {
        continue;
      }
      int dist = bfsV.distTo(vertex) + bfsW.distTo(vertex);
      if (shortest < 0 || dist < shortest) {
        shortest = dist;
        ancestor = vertex;
      }
    }
    return ancestor;
  }

  /**
   * check null argument.
   */
  private <T> void checkNullArg(T arg) {
    if (arg == null) {
      throw new IllegalArgumentException("null argument");
    }
  }

  /**
   * check vertex range.
   */
  private void checkVertexRange(int v) {
    int bound = graph.V();
    if (v < 0 || v >= bound) {
      throw new IllegalArgumentException("vertex out of range");
    }
  }

  /**
   * check interable.
   */
  private void checkIterable(Iterable<Integer> vertices) {
    for (Integer v : vertices) {
      if (v == null) {
        throw new IllegalArgumentException("null vertex in iterable");
      }
      checkVertexRange(v);
    }
  }

  /**
   * do unit testing of this class.
   */
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph graph = new Digraph(in);
    SAP sap = new SAP(graph);
    while (!StdIn.isEmpty()) {
      int v = StdIn.readInt();
      int w = StdIn.readInt();
      int length = sap.length(v, w);
      int ancestor = sap.ancestor(v, w);
      StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
  }
}