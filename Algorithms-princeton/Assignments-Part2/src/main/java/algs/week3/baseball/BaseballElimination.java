package algs.week3.baseball;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedQueue;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

/**
 * BaseballElimination, determine which teams have been mathematically
 * eliminated from winning their division.
 */
public class BaseballElimination {

  // store information
  private final int num;
  private final String[] names;
  private final int[] wins;
  private final int[] losses;
  private final int[] remaining;
  private final int[][] games;

  // flow network size
  private final int gameVertices;
  private final int teamVertices;
  private final int size;

  // key: eliminated team, value: eliminated by teams
  private final HashMap<String, Bag<String>> eliminationInfo;

  /**
   * create a baseball division from given filename in format specified below.
   */
  public BaseballElimination(String filename) {

    // initialize input stream
    In input = new In(filename);
    num = Integer.parseInt(input.readLine());
    gameVertices = (num - 1) * (num - 2) / 2;
    teamVertices = num - 1;
    size = 2 + gameVertices + teamVertices;
    int idx = 0;

    // initialize containers
    names = new String[num];
    wins = new int[num];
    losses = new int[num];
    remaining = new int[num];
    games = new int[num][num];
    eliminationInfo = new HashMap<String, Bag<String>>();

    // parse lines
    while (input.hasNextLine()) {

      // split fields
      String line = input.readLine();
      String[] fields = line.trim().split("\\s+");

      // names, numbers of wins, losses, remaining games of a team
      int fieldIdx = 0;
      names[idx] = fields[fieldIdx++];
      wins[idx] = Integer.parseInt(fields[fieldIdx++]);
      losses[idx] = Integer.parseInt(fields[fieldIdx++]);
      remaining[idx] = Integer.parseInt(fields[fieldIdx++]);

      // games left to play against team j
      for (int i = 0; i < num; i++) {
        games[idx][i] = Integer.parseInt(fields[fieldIdx++]);
      }

      // increment index
      idx++;
    }
  }

  /**
   * number of teams.
   */
  public int numberOfTeams() {
    return num;
  }

  /**
   * all teams.
   */
  public Iterable<String> teams() {

    // enable immutability
    LinkedQueue<String> queue = new LinkedQueue<String>();
    for (String name : names) {
      queue.enqueue(name);
    }
    return queue;
  }

  /**
   * number of wins for given team.
   */
  public int wins(String team) {
    int idx = checkTeam(team);
    return wins[idx];
  }

  /**
   * number of losses for given team.
   */
  public int losses(String team) {
    int idx = checkTeam(team);
    return losses[idx];
  }

  /**
   * number of remaining games for given team.
   */
  public int remaining(String team) {
    int idx = checkTeam(team);
    return remaining[idx];
  }

  /**
   * number of remaining games between team1 and team2.
   */
  public int against(String team1, String team2) {
    int idx1 = checkTeam(team1);
    int idx2 = checkTeam(team2);
    return games[idx1][idx2];
  }

  /**
   * is given team eliminated?.
   */
  public boolean isEliminated(String team) {

    // check if argument is invalid
    int idx = checkTeam(team);

    // if calculated, return the result
    if (eliminationInfo.containsKey(team)) {
      if (eliminationInfo.get(team).isEmpty()) {
        return false;
      }
      return true;
    }

    // build flowNetwork
    return calcuElimination(team, idx);
  }

  /**
   * subset R of teams that eliminates given team; null if not eliminated.
   */
  public Iterable<String> certificateOfElimination(String team) {

    // return the result; null if not eliminated.
    if (isEliminated(team)) {
      return eliminationInfo.get(team);
    }
    return null;
  }

  /**
   * helper function for calculating maxflow and mincut.
   */
  private boolean calcuElimination(String team, int idx) {

    // trivial elimination
    int[] teamToTarget = new int[teamVertices];

    // move query team to the last by swapping
    swap(names, idx);
    swap(wins, idx);
    swap(losses, idx);
    swap(remaining, idx);
    swap(games, idx);

    int teamIdx = teamVertices;
    Bag<String> eliminators = new Bag<String>();
    for (int i = 0; i < teamVertices; i++) {
      teamToTarget[i] = wins[teamIdx] + remaining[teamIdx] - wins[i];
      if (teamToTarget[i] < 0) {
        eliminators.add(names[i]);
      }
    }

    if (!eliminators.isEmpty()) {
      eliminationInfo.put(team, eliminators);
      return true;
    }

    // Non tivial elimination: build flow
    FlowNetwork flowNet = new FlowNetwork(size);
    int sumGames = 0;

    // add edges
    int v = 1;
    for (int i = 0; i < teamVertices; i++) {
      for (int j = i + 1; j < teamVertices; j++) {

        // source -> game vertices
        flowNet.addEdge(new FlowEdge(0, v, games[i][j]));
        sumGames += games[i][j];

        // game vertices -> team vertices
        flowNet.addEdge(new FlowEdge(v, gameVertices + i + 1, Double.POSITIVE_INFINITY));
        flowNet.addEdge(new FlowEdge(v, gameVertices + j + 1, Double.POSITIVE_INFINITY));
        v++;
      }
    }

    // team vertices -> target
    int target = size - 1;
    for (int i = 0; i < teamVertices; i++) {
      flowNet.addEdge(new FlowEdge(v, target, teamToTarget[i]));
      v++;
    }

    // compute maxflow using ford fulkerson algorithm
    FordFulkerson fordFulkerson = new FordFulkerson(flowNet, 0, target);

    // all edges in the maxflow that are pointing from s are full
    if (Double.compare(fordFulkerson.value(), sumGames) == 0) {

      // update elimination information
      eliminationInfo.put(team, eliminators);
      return false;
    }

    // choosing the team vertices on the source side of a min s-t cut
    for (int i = 0; i < teamVertices; i++) {
      if (fordFulkerson.inCut(gameVertices + i + 1)) {
        eliminators.add(names[i]);
      }
    }

    // update elimination information
    eliminationInfo.put(team, eliminators);
    return true;
  }

  /**
   * swap idx to the last.
   */
  private void swap(String[] array, int idx) {
    String temp = array[idx];
    array[idx] = array[teamVertices];
    array[teamVertices] = temp;
  }

  /**
   * swap int[].
   */
  private void swap(int[] array, int idx) {
    int temp = array[idx];
    array[idx] = array[teamVertices];
    array[teamVertices] = temp;
  }

  /**
   * swap int[][].
   */
  private void swap(int[][] array, int idx) {
    int[] temp = array[idx];
    array[idx] = array[teamVertices];
    array[teamVertices] = temp;
    for (int i = 0; i < teamVertices; i++) {
      swap(array[i], idx);
    }
  }

  /**
   * throw a java.lang.IllegalArgumentException if input argument is a invalid
   * team. return the index of the team.
   */
  private int checkTeam(String team) {

    // initialize idx
    int idx = 0;

    if (team == null) {
      throw new IllegalArgumentException("null team name");
    }
    boolean found = false;
    for (String name : names) {
      if (team.equals(name)) {
        found = true;
        return idx;
      }
      idx++;
    }
    if (!found) {
      throw new IllegalArgumentException("team not found: " + team);
    }
    return idx;
  }

  /**
   * test client.
   */
  public static void main(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    for (String team : division.teams()) {
      if (division.isEliminated(team)) {
        StdOut.print(team + " is eliminated by the subset R = { ");
        for (String t : division.certificateOfElimination(team)) {
          StdOut.print(t + " ");
        }
        StdOut.println("}");
      } else {
        StdOut.println(team + " is not eliminated");
      }
    }
  }
}