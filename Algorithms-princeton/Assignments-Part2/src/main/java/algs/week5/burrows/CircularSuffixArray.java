package algs.week5.burrows;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * CircularSuffixArray describes the abstraction of a sorted array of the n
 * circular suffixes of a string of length n.
 */
public class CircularSuffixArray {

  // array length
  private final int len;

  // sorted index array
  private final int[] suffixIdx;

  /**
   * circular suffix array of s.
   */
  public CircularSuffixArray(String s) {

    // argument check
    if (s == null) {
      throw new IllegalArgumentException("null string");
    }

    // get length
    len = s.length();

    // use String, idx pair to represent a suffix
    suffixIdx = new int[len];
    for (int i = 0; i < len; i++) {
      suffixIdx[i] = i;
    }

    // deal with special case
    if (len < 1) {
      return;
    }
    boolean repetitive = true;
    char c = s.charAt(0);
    for (int i = 1; i < len; i++) {
      if (s.charAt(i) != c) {
        repetitive = false;
        break;
      }
    }
    if (repetitive) {
      return;
    }

    // sort using a modified version of quick 3 way string sort
    QuickCircularSuffix.sort(s, suffixIdx, len);
  }

  /**
   * inner class for sorting.
   */
  private static class QuickCircularSuffix {

    // cutoff to insertion sort
    private static final int CUTOFF = 15;

    // do not instantiate
    private QuickCircularSuffix() {
      // do nothing
    }

    /**
     * Rearranges the suffixes in ascending order. Suffixes are defined by string
     * and int[].
     */
    public static void sort(String s, int[] index, int strLen) {
      StdRandom.shuffle(index);
      sort(s, index, 0, strLen - 1, 0, strLen);
    }

    // 3-way string quicksort a[lo..hi] starting at dth character
    private static void sort(String s, int[] index, int lo, int hi, int d, int strLen) {

      // cutoff to insertion sort for small subarrays
      if (hi <= lo + CUTOFF) {
        insertion(s, index, lo, hi, d, strLen);
        return;
      }

      int lt = lo;
      int gt = hi;
      int v = charAt(s, index[lo], d, strLen);
      int i = lo + 1;
      while (i <= gt) {
        int t = charAt(s, index[i], d, strLen);
        if (t < v) {
          exch(index, lt++, i++);
        } else if (t > v) {
          exch(index, i, gt--);
        } else {
          i++;
        }
      }

      // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
      sort(s, index, lo, lt - 1, d, strLen);
      if (v >= 0) {
        sort(s, index, lt, gt, d + 1, strLen);
      }
      sort(s, index, gt + 1, hi, d, strLen);
    }

    // return the dth character of s, -1 if '!'
    private static int charAt(String s, int idx, int d, int strLen) {
      return s.charAt((idx + d) % strLen);
    }

    // exchange a[i] and a[j]
    private static void exch(int[] index, int i, int j) {
      int temp = index[i];
      index[i] = index[j];
      index[j] = temp;
    }

    // sort from a[lo] to a[hi], starting at the dth character
    private static void insertion(String s, int[] index, int lo, int hi, int d, int strLen) {
      for (int i = lo; i <= hi; i++) {
        for (int j = i; j > lo && less(s, index[j], index[j - 1], d, strLen); j--) {
          exch(index, j, j - 1);
        }
      }
    }

    // is v less than w, starting at character d
    private static boolean less(String s, int v, int w, int d, int strLen) {
      for (int i = d; i < strLen; i++) {
        int vi = charAt(s, v, i, strLen);
        int wi = charAt(s, w, i, strLen);
        if (vi < wi) {
          return true;
        }
        if (vi > wi) {
          return false;
        }
      }
      return false;
    }

  }

  /**
   * length of s.
   */
  public int length() {
    return len;
  }

  /**
   * returns index of ith sorted suffix.
   */
  public int index(int i) {
    if (i < 0 || i >= len) {
      throw new IllegalArgumentException("index out of range");
    }
    return suffixIdx[i];
  }

  /**
   * unit testing (required).
   */
  public static void main(String[] args) {
    String input = "AAAAAAAAAAAAAAAAAA";
    CircularSuffixArray csa = new CircularSuffixArray(input);
    for (int i = 0; i < csa.length(); i++) {
      StdOut.println(csa.index(i));
    }
  }
}