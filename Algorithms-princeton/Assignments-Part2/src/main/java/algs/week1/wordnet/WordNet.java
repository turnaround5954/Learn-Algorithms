package algs.week1.wordnet;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * WordNet.
 * a semantic lexicon for the English language.
 */
public class WordNet {

  // hashMap for id-synset
  private final ArrayList<String> idSets;

  // hashMap for noun-ids
  private final HashMap<String, HashSet<Integer>> nounIds;

  // digraph for WordNet
  private final Digraph graph;

  // avoid calling constructor SAP too many times
  private final SAP sap; 

  /**
   * constructor takes the name of the two input files.
   */
  public WordNet(String synsets, String hypernyms) {

    // corner check
    checkNullString(synsets);
    checkNullString(hypernyms);

    // build synset
    idSets = new ArrayList<String>();
    nounIds = new HashMap<String, HashSet<Integer>>();
    buildSets(synsets);

    // build diagraph
    int numVertex = idSets.size();
    graph = new Digraph(numVertex);
    buildGraph(hypernyms);

    // DAG check
    DirectedCycle cycleCheck = new DirectedCycle(graph);
    if (cycleCheck.hasCycle()) {
      throw new IllegalArgumentException("not a DAG");
    }
    
    // root check
    int numRoot = 0;
    for (int v = 0; v < numVertex; v++) {
      if (graph.outdegree(v) == 0) {
        numRoot++;
      }
    }
    if (numRoot != 1) {
      throw new IllegalArgumentException("not rooted");
    }

    // initialize SAP
    sap = new SAP(graph);
  }

  /**
   * process synsets. do not use resizable array here.
   */
  private void buildSets(String synsets) {

    // read and parse input
    In input = new In(synsets);
    while (input.hasNextLine()) {

      // parse input
      String line = input.readLine();
      String[] fields = line.split(",");

      // id - set
      int id = Integer.parseInt(fields[0]);
      idSets.add(id, fields[1]);

      // noun - ids
      String[] nouns = fields[1].split(" ");
      for (String noun : nouns) {
        if (!nounIds.containsKey(noun)) {
          nounIds.put(noun, new HashSet<Integer>());
        }
        nounIds.get(noun).add(id);
      }
    }
  }

  /**
   * process hypernyms.
   */
  private void buildGraph(String hypernyms) {
    
    // read and parse input
    In input = new In(hypernyms);
    while (input.hasNextLine()) {
      
      // split line
      String line = input.readLine();
      String[] fields = line.split(",");

      // parse v
      int v = Integer.parseInt(fields[0]);

      // for each w
      for (int i = 1; i < fields.length; i++) {
        
        // parse w
        int w = Integer.parseInt(fields[i]);

        // add edge to graph
        graph.addEdge(v, w);
      }
    }
  }

  /**
   * returns all WordNet nouns.
   */
  public Iterable<String> nouns() {

    // make it immutable
    Bag<String> nouns = new Bag<String>();
    for (String noun : nounIds.keySet()) {
      nouns.add(noun);
    }
    return nouns;
  }

  /**
   * is the word a WordNet noun?.
   */
  public boolean isNoun(String word) {

    // corner check
    checkNullString(word);

    // contains or not
    return nounIds.containsKey(word);
  }

  /**
   * distance between nounA and nounB (defined below).
   * calculate distance.
   * A = set of synsets in which x appears
   * B = set of synsets in which y appears
   * distance(x, y) = length of shortest ancestral path of subsets A and B
   * sca(x, y) = a shortest common ancestor of subsets A and B
   */
  public int distance(String nounA, String nounB) {

    // corner check
    checkNullString(nounA);
    checkNullString(nounB);
    if ((!isNoun(nounA)) || (!isNoun(nounB))) {
      throw new IllegalArgumentException("not a WordNet noun");
    }

    // get distance
    int dist = sap.length(nounIds.get(nounA), nounIds.get(nounB));
    return dist;
  }

  /**
   * a synset (second field of synsets.txt) that is the common ancestor of nounA
   * and nounB in a shortest ancestral path (defined below)
   */
  public String sap(String nounA, String nounB) {

    // corner check
    checkNullString(nounA);
    checkNullString(nounB);
    if ((!isNoun(nounA)) || (!isNoun(nounB))) {
      throw new IllegalArgumentException("not a WordNet noun");
    }
    
    // find ancestor
    int ancestor = sap.ancestor(nounIds.get(nounA), nounIds.get(nounB));
    return idSets.get(ancestor);
  }

  /**
   * check input string, if is null, throw exception.
   */
  private void checkNullString(String inputString) {
    if (inputString == null) {
      throw new IllegalArgumentException("null string input");
    }
  }

  /**
   * do unit testing of this class.
   */
  public static void main(String[] args) {
    String synsets = "algs/src/test/java/algs/week1/wordnet/synsets15.txt";
    String hypernyms = "algs/src/test/java/algs/week1/wordnet/hypernyms15Tree.txt";
    WordNet wordnet = new WordNet(synsets, hypernyms);
    for (String s : wordnet.nouns()) {
      StdOut.print(s + " ");
    }
    StdOut.println();
    StdOut.println(wordnet.sap("e", "i"));
  }
}