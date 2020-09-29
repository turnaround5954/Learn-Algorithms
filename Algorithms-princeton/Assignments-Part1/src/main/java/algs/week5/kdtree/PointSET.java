package algs.week5.kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;
import java.util.TreeSet;

/**
 * PointSET. Brute-force implementation. Write a mutable data type PointSET.java
 * that represents a set of points in the unit square.
 */
public class PointSET {

  private final TreeSet<Point2D> pointSet;

  /**
   * construct an empty set of points.
   */
  public PointSET() {
    pointSet = new TreeSet<Point2D>();
  }

  /**
   * is the set empty?.
   */
  public boolean isEmpty() {
    return pointSet.isEmpty();
  }

  /**
   * number of points in the set.
   */
  public int size() {
    return pointSet.size();
  }

  /**
   * helper function bound check.
   */
  private <T> void boundCheckPoint2D(T t) {
    if (t == null) {
      throw new IllegalArgumentException("null input");
    }
  }

  /**
   * add the point to the set (if it is not already in the set).
   */
  public void insert(Point2D p) {
    boundCheckPoint2D(p);
    if (!contains(p)) {
      pointSet.add(p);
    }
  }

  /**
   * does the set contain point p?.
   */
  public boolean contains(Point2D p) {
    boundCheckPoint2D(p);
    return pointSet.contains(p);
  }

  /**
   * draw all points to standard draw.
   */
  public void draw() {
    for (Point2D p : pointSet) {
      p.draw();
    }
  }

  /**
   * all points that are inside the rectangle (or on the boundary).
   */
  public Iterable<Point2D> range(RectHV rect) {
    boundCheckPoint2D(rect);
    LinkedList<Point2D> ptsInRange = new LinkedList<Point2D>();
    for (Point2D p : pointSet) {
      if (rect.contains(p)) {
        ptsInRange.add(p);
      }
    }
    return ptsInRange;
  }

  /**
   * a nearest neighbor in the set to point p; null if the set is empty.
   */
  public Point2D nearest(Point2D p) {
    boundCheckPoint2D(p);
    if (isEmpty()) {
      return null;
    }
    double distMin = Double.POSITIVE_INFINITY;
    Point2D neighbNearest = null;
    for (Point2D q : pointSet) {

      // do not check equal here
      double dist = q.distanceSquaredTo(p);
      if (dist < distMin) {
        distMin = dist;
        neighbNearest = q;
      }
    }
    return neighbNearest;
  }

  /**
   * unit testing of the methods (optional).
   */
  public static void main(String[] args) {
    
    // points' x and y axises should by within [0, 1]
    PointSET ptnsSet = new PointSET();
    ptnsSet.insert(new Point2D(0.1, 0.1));
    ptnsSet.insert(new Point2D(0.1, 0.2));
    ptnsSet.insert(new Point2D(0.2, 0.2));
    ptnsSet.insert(new Point2D(0.3, 0.1));
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(0.01);
    ptnsSet.draw();
    StdDraw.show();
    StdOut.println(ptnsSet.range(new RectHV(0.05, 0.05, 0.15, 0.25)));
    StdOut.println(ptnsSet.nearest(new Point2D(0.3, 0.1)));
  }
}