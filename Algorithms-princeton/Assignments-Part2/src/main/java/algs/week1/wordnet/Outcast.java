package algs.week1.wordnet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Outcast. Measuring the semantic relatedness of two nouns. compute the sum of
 * the distances between each noun and every other one.
 */
public class Outcast {

  private final WordNet wordnet;

  /**
   * constructor takes a WordNet object.
   */
  public Outcast(WordNet wordnet) {
    this.wordnet = wordnet;
  }

  /**
   * given an array of WordNet nouns, return an outcast.
   */
  public String outcast(String[] nouns) {
    int distLongest = -1;
    String outcast = null;
    for (String noun : nouns) {
      int dist = 0;
      for (String other : nouns) {
        dist += wordnet.distance(noun, other);
      }
      if (dist > distLongest) {
        distLongest = dist;
        outcast = noun;
      }
    }
    return outcast;
  }

  /**
   * see test client below.
   */
  public static void main(String[] args) {
    WordNet wordnet = new WordNet(args[0], args[1]);
    Outcast outcast = new Outcast(wordnet);
    for (int t = 2; t < args.length; t++) {
      In in = new In(args[t]);
      String[] nouns = in.readAllStrings();
      StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
  }
}