package algs.week5.burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * BurrowsWheeler. 1. Burrows–Wheeler transform. 2. Move-to-front encoding. 3.
 * Huffman compression.
 */
public class BurrowsWheeler {

  // extend ASCII alphabet size
  private static final int R = 256;

  /**
   * apply Burrows-Wheeler transform, reading from standard input and writing to
   * standard output.
   */
  public static void transform() {

    // read the input
    String s = BinaryStdIn.readString();

    // build suffixArray
    CircularSuffixArray csa = new CircularSuffixArray(s);

    // The Burrows–Wheeler transform is the last column in the sorted suffixes array
    // t[], preceded by the row number first in which the original string ends up.
    int suffixLen = csa.length();
    for (int i = 0; i < suffixLen; i++) {
      if (csa.index(i) == 0) {
        BinaryStdOut.write(i);
      }
    }

    // wirte t[]
    for (int i = 0; i < suffixLen; i++) {
      BinaryStdOut.write(charLast(s, csa.index(i), suffixLen));
    }

    // close output stream
    BinaryStdOut.close();
  }

  // return the dth character of suffix
  private static char charLast(String s, int idx, int strLen) {
    int pos = (idx + strLen - 1) % strLen;
    return s.charAt(pos);
  }

  /**
   * apply Burrows-Wheeler inverse transform, reading from standard input and
   * writing to standard output.
   */
  public static void inverseTransform() {

    // read the input to get first and t[]
    final int first = BinaryStdIn.readInt();
    String s = BinaryStdIn.readString();
    int strLen = s.length();
    char[] t = s.toCharArray();

    // calculate next[] using key-indexed counting
    int[] next = new int[strLen];
    int[] count = new int[R + 1];

    // compute frequency counts
    for (int i = 0; i < strLen; i++) {
      count[t[i] + 1]++;
    }

    // compute cumulates
    for (int r = 0; r < R; r++) {
      count[r + 1] += count[r];
    }

    // move data (instead of t[i] on the right of = in sorting, use i instead)
    for (int i = 0; i < strLen; i++) {
      next[count[t[i]]++] = i;
    }

    // get the suffix corresponding to first
    int idx = next[first];
    for (int i = 0; i < strLen; i++) {
      BinaryStdOut.write(t[idx]);
      idx = next[idx];
    }

    // close output stream
    BinaryStdOut.close();
  }

  /**
   * if args[0] is '-', apply Burrows-Wheeler transform. if args[0] is '+', apply
   * Burrows-Wheeler inverse transform.
   */
  public static void main(String[] args) {
    if (args[0].equals("-")) {
      transform();
    } else if (args[0].equals("+")) {
      inverseTransform();
    } else {
      throw new IllegalArgumentException("Illegal command line argument");
    }
  }

}