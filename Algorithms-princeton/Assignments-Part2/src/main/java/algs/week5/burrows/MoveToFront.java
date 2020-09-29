package algs.week5.burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * MoveToFront maintains an ordered sequence of the characters in the alphabet,
 * and repeatedly read a character from the input message, print out the
 * position in which that character appears, and move that character to the
 * front of the sequence.
 */
public class MoveToFront {

  // alphabet size of extended ASCII
  private static final int R = 256;

  /**
   * apply move-to-front encoding, reading from standard input and writing to
   * standard output.
   */
  public static void encode() {

    // use a char array
    char[] alphabet = new char[R];
    for (char c = 0; c < R; c++) {
      alphabet[c] = c;
    }

    // produce output and perform move-to-front operation
    while (!BinaryStdIn.isEmpty()) {
      char c = BinaryStdIn.readChar();
      char pos = 0;
      while (alphabet[pos] != c) {
        pos++;
      }
      if (pos != 0) {
        System.arraycopy(alphabet, 0, alphabet, 1, pos);
        alphabet[0] = c;
      }
      BinaryStdOut.write(pos);
    }

    // close output stream
    BinaryStdOut.close();
  }

  /**
   * apply move-to-front decoding, reading from standard input and writing to
   * standard output.
   */
  public static void decode() {

    // use a char array
    char[] alphabet = new char[R];
    for (char c = 0; c < R; c++) {
      alphabet[c] = c;
    }

    // produce output and perform move-to-front operation
    while (!BinaryStdIn.isEmpty()) {
      char pos = BinaryStdIn.readChar();
      char c = alphabet[pos];
      if (pos != 0) {
        System.arraycopy(alphabet, 0, alphabet, 1, pos);
        alphabet[0] = c;
      }
      BinaryStdOut.write(c);
    }

    // close output stream
    BinaryStdOut.close();
  }

  /**
   * if args[0] is '-', apply move-to-front encoding. if args[0] is '+', apply
   * move-to-front decoding.
   */
  public static void main(String[] args) {
    if (args[0].equals("-")) {
      encode();
    } else if (args[0].equals("+")) {
      decode();
    } else {
      throw new IllegalArgumentException("Illegal command line argument");
    }
  }

}
