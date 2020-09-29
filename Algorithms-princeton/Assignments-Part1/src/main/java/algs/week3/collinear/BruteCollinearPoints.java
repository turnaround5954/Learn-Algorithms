package algs.week3.collinear;

import java.util.Arrays;
import java.util.LinkedList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * BruteCollinearPoints.
 */
public class BruteCollinearPoints {
  // private static final double DELTA = 1e-8;
  private int numPoints;
  private final LinkedList<LineSegment> lineSegments;

  /**
   * finds all line segments containing 4 points.
   */
  public BruteCollinearPoints(Point[] points) {
    boundCheck(points);
    lineSegments = new LinkedList<LineSegment>();
    if (numPoints < 4) {
      return;
    }
    Point[] aux = new Point[points.length];
    for (int i = 0; i < points.length; i++) {
      aux[i] = points[i];
    }
    Arrays.sort(aux);
    for (int idx1 = 0; idx1 < numPoints; idx1++) {
      for (int idx2 = idx1 + 1; idx2 < numPoints; idx2++) {
        for (int idx3 = idx2 + 1; idx3 < numPoints; idx3++) {
          for (int idx4 = idx3 + 1; idx4 < numPoints; idx4++) {
            Point p1 = aux[idx1];
            Point p2 = aux[idx2];
            Point p3 = aux[idx3];
            Point p4 = aux[idx4];
            if (Double.compare(p1.slopeTo(p2), p2.slopeTo(p3)) == 0
                && Double.compare(p2.slopeTo(p3), p3.slopeTo(p4)) == 0) {
              lineSegments.add(new LineSegment(p1, p4));
            }
          }
        }
      }
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
    BruteCollinearPoints collinear = new BruteCollinearPoints(points);
    for (LineSegment segment : collinear.segments()) {
      StdOut.println(segment);
      segment.draw();
    }
    StdDraw.show();
  }
}