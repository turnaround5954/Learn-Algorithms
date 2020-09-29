package algs.week5.kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

/**
 * KdTree. A 2d-tree is a generalization of a BST to two-dimensional keys. The
 * idea is to build a BST with points in the nodes, using the x- and
 * y-coordinates of the points as keys in strictly alternating sequence.
 */
public class KdTree {

  // store orientations as constants
  private static final boolean H = false;
  private static final boolean V = true;
  private Node root;
  private int size;

  /**
   * construct an empty set of points.
   */
  public KdTree() {
    root = null;
    size = 0;
  }

  /**
   * Node class.
   */
  private static class Node {

    private final boolean orient; // the orientation
    private final Point2D pnt; // the point
    private final RectHV rect; // the axis-aligned rectangle corresponding to this node
    private Node lb; // the left/bottom subtree
    private Node rt; // the right/top subtree

    public Node(Point2D p, boolean ori, RectHV rec) {

      // initialize pnt
      pnt = p;

      // initialize orient
      orient = ori;

      // initialize rect
      rect = rec;
    }
  }

  /**
   * is the set empty?.
   */
  public boolean isEmpty() {
    if (root == null) {
      return true;
    }
    return false;
  }

  /**
   * number of points in the set.
   */
  public int size() {
    return size;
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
    root = insert(root, p, V, new RectHV(0, 0, 1, 1));
  }

  /**
   * helper function for insert. ori is the orientation for node.child.
   */
  private Node insert(Node node, Point2D p, boolean ori, RectHV rec) {

    // insert point, not node
    if (node == null) {
      size++;
      return new Node(p, ori, rec);
    }

    // return if contains
    if (node.pnt.equals(p)) {
      return node;
    }

    RectHV rectNext = null;

    // recurrence, use null judgment to reduce call to RectHV
    if (node.orient == V) {
      if (p.x() - node.pnt.x() < 0) {
        if (node.lb == null) {
          rectNext = new RectHV(node.rect.xmin(), node.rect.ymin(), node.pnt.x(), node.rect.ymax());
        }
        node.lb = insert(node.lb, p, H, rectNext);
      } else {
        if (node.rt == null) {
          rectNext = new RectHV(node.pnt.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
        }
        node.rt = insert(node.rt, p, H, rectNext);
      }
    } else {
      if (p.y() - node.pnt.y() < 0) {
        if (node.lb == null) {
          rectNext = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.pnt.y());
        }
        node.lb = insert(node.lb, p, V, rectNext);
      } else {
        if (node.rt == null) {
          rectNext = new RectHV(node.rect.xmin(), node.pnt.y(), node.rect.xmax(), node.rect.ymax());
        }
        node.rt = insert(node.rt, p, V, rectNext);
      }
    }
    return node;
  }

  /**
   * does the set contain point p?.
   */
  public boolean contains(Point2D p) {
    boundCheckPoint2D(p);
    return contains(root, p);
  }

  /**
   * helper function for contains.
   */
  private boolean contains(Node node, Point2D p) {

    // not find
    if (node == null) {
      return false;
    }

    if (node.pnt.equals(p)) {
      return true;
    }

    if (node.orient == V) {
      if (p.x() - node.pnt.x() < 0) {
        return contains(node.lb, p);
      } else {
        return contains(node.rt, p);
      }
    } else {
      if (p.y() - node.pnt.y() < 0) {
        return contains(node.lb, p);
      } else {
        return contains(node.rt, p);
      }
    }
  }

  /**
   * draw 2d tree to standard draw.
   */
  public void draw() {

    // draw recursively
    draw(root);
  }

  /**
   * helper function for draw.
   */
  private void draw(Node node) {

    // check end point
    if (node == null) {
      return;
    }

    // draw point
    StdDraw.setPenColor(StdDraw.BLACK);
    StdDraw.setPenRadius(0.01);
    node.pnt.draw();

    // draw line
    StdDraw.setPenRadius();
    if (node.orient == V) {
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(node.pnt.x(), node.rect.ymin(), node.pnt.x(), node.rect.ymax());
    } else {
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(node.rect.xmin(), node.pnt.y(), node.rect.xmax(), node.pnt.y());
    }

    // recurrence
    draw(node.lb);
    draw(node.rt);
  }

  /**
   * all points that are inside the rectangle (or on the boundary).
   */
  public Iterable<Point2D> range(RectHV rect) {
    boundCheckPoint2D(rect);
    LinkedList<Point2D> pointSet = new LinkedList<Point2D>();
    range(root, rect, pointSet);
    return pointSet;
  }

  /**
   * helper function for range.
   */
  private void range(Node node, RectHV rec, LinkedList<Point2D> points) {

    // check null
    if (node == null) {
      return;
    }

    // check intersection
    if (!node.rect.intersects(rec)) {
      return;
    }

    // add point
    if (rec.contains(node.pnt)) {
      points.add(node.pnt);
    }

    // recurrence
    range(node.lb, rec, points);
    range(node.rt, rec, points);
  }

  /**
   * a nearest neighbor in the set to point p; null if the set is empty.
   */
  public Point2D nearest(Point2D p) {
    boundCheckPoint2D(p);

    // check empty
    if (isEmpty()) {
      return null;
    }

    // call helper
    Nearest findNearest = new Nearest();
    findNearest.nearest(root, p);
    return findNearest.pntNearest;
  }

  /**
   * class for finding nearest.
   */
  private static class Nearest {

    // store the nearest point
    private Point2D pntNearest;
    private double dist;

    // initialize
    public Nearest() {
      pntNearest = null;
      dist = Double.POSITIVE_INFINITY;
    }

    /**
     * helper function for nearest.
     */
    public void nearest(Node node, Point2D p) {

      // check null
      if (node == null || dist <= node.rect.distanceSquaredTo(p)) {
        return;
      }

      // calculate dist
      double curDist = node.pnt.distanceSquaredTo(p);
      if (curDist < dist) {
        dist = curDist;
        pntNearest = node.pnt;
      }

      // recurrence
      if ((node.orient == V && p.x() - node.pnt.x() < 0)
          || (node.orient == H && p.y() - node.pnt.y() < 0)) {
        nearest(node.lb, p);
        nearest(node.rt, p);
      } else {
        nearest(node.rt, p);
        nearest(node.lb, p);
      }
    }
  }

  /**
   * unit testing of the methods (optional).
   */
  public static void main(String[] args) {

    // points' x and y axises should by within [0, 1]
    KdTree ptnsSet = new KdTree();
    ptnsSet.insert(new Point2D(0.7, 0.2));
    ptnsSet.insert(new Point2D(0.5, 0.4));
    ptnsSet.insert(new Point2D(0.2, 0.3));
    ptnsSet.insert(new Point2D(0.4, 0.7));
    ptnsSet.insert(new Point2D(0.9, 0.6));
    ptnsSet.draw();
    StdDraw.show();
    StdOut.println(ptnsSet.range(new RectHV(0.0, 0.0, 0.5, 0.5)));
    StdOut.println(ptnsSet.nearest(new Point2D(0.9, 0.5)));
  }
}