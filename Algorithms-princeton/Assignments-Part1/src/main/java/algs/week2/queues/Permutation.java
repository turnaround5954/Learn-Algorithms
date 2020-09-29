package algs.week2.queues;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Permutation. About EOF: Take focus AWAY from the console window and then
 * return it to the console window, then you can enter CTRL-D and it will work.
 */
public class Permutation {
  // calculate wheather to add elem or not when queue is full
  private static boolean replace(int capacity, int cntIn) {
    return StdRandom.uniform() < (double) capacity / (double) cntIn;
  }

  // takes argument k
  public static void main(String[] args) {
    int k = Integer.parseInt(args[0]);
    int cnt = 0;
    // initialize queue
    RandomizedQueue<String> rq = new RandomizedQueue<String>();
    // push while CTRL-Z
    while (!StdIn.isEmpty()) {
      String curString = StdIn.readString();
      cnt++;
      if (k <= 0) {
        continue;
      }
      // extra challenge
      if (cnt <= k) {
        rq.enqueue(curString);
      } else if (replace(k, cnt)) {
        rq.dequeue();
        rq.enqueue(curString);
      }
    }
    // deque k items
    for (String e : rq) {
      StdOut.println(e);
    }
  }
}