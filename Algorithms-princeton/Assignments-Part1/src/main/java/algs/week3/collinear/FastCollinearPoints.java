package algs.week3.collinear;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * FastCollinearPoints Do not remove duplicates after all done, since there are
 * quadratic lines, taking n^4 time to remove duplicates.
 */
public class FastCollinearPoints {
  private static final int THRESH = 4;
  private int numPoints;
  private final LinkedList<LineSegment> lineSegments;

  /**
   * Finds all line segments containing 4 or more points. Keep in mind the
   * diagram.
   */
  public FastCollinearPoints(Point[] points) {
    boundCheck(points);
    lineSegments = new LinkedList<LineSegment>();
    // check the minimal requirement.
    if (numPoints < THRESH) {
      return;
    }
    // sort based on points' default.
    // note that stable sort is used.
    Point[] ptsSorted = points.clone();
    Arrays.sort(ptsSorted);
    for (Point p : ptsSorted) {
      Point[] aux = ptsSorted.clone();
      Arrays.sort(aux, p.slopeOrder());
      // count collinear points
      int cnt = 2;
      // jude wheather the segment starts by p.
      int idx = 1;
      for (int i = 1; i < numPoints - 1; i++) {
        if (Double.compare(aux[i].slopeTo(p), aux[i + 1].slopeTo(p)) == 0) {
          cnt++;
        } else {
          if (cnt >= THRESH) {
            // each point on the segment passed to addLine()
            // is the most upper-right.
            // use point at idx to jude wheather the segment starts by p.
            addLine(p, aux[idx], aux[i]);
          }
          idx = i + 1;
          cnt = 2;
        }
      }
      // deal with ended loop
      if (cnt >= THRESH) {
        addLine(p, aux[idx], aux[numPoints - 1]);
      }
    }
  }

  /**
   * check and add segment (which is longest). only add the segment which begins
   * by p
   */
  private void addLine(Point p0, Point p1, Point p2) {
    if (p0.compareTo(p1) <= 0) {
      lineSegments.add(new LineSegment(p0, p2));
    }
  }

  // boundary check
  private void boundCheck(Point[] points) {
    if (points == null) {
      throw new IllegalArgumentException("Null input");
    }

    numPoints = points.length;
    // do not use nested loop here, causing null exception.
    for (int i = 0; i < numPoints; i++) {
      if (points[i] == null) {
        throw new IllegalArgumentException("Contains null points");
      }
    }

    for (int i = 0; i < numPoints; i++) {
      for (int j = i + 1; j < numPoints; j++) {
        if (points[i].compareTo(points[j]) == 0) {
          throw new IllegalArgumentException("Contains duplicates");
        }
      }
    }
  }

  // the number of line segments
  public int numberOfSegments() {
    return lineSegments.size();
  }

  // the line segments
  public LineSegment[] segments() {
    // this is a deep copy
    return lineSegments.toArray(new LineSegment[lineSegments.size()]);
  }

  /**
   * Test client that reads .txt file as input and visualize.
   */
  public static void main(String[] args) {
    // read the n points from a file
    In in = new In(args[0]);
    int n = in.readInt();
    Point[] points = new Point[n];
    for (int i = 0; i < n; i++) {
      int x = in.readInt();
      int y = in.readInt();
      points[i] = new Point(x, y);
    }

    // draw the points
    StdDraw.enableDoubleBuffering();
    StdDraw.setXscale(0, 32768);
    StdDraw.setYscale(0, 32768);
    for (Point p : points) {
      p.draw();
    }
    StdDraw.show();

    // print and draw the line segments
    FastCollinearPoints collinear = new FastCollinearPoints(points);
    for (LineSegment segment : collinear.segments()) {
      StdOut.println(segment);
      segment.draw();
    }
    StdDraw.show();
  }
}