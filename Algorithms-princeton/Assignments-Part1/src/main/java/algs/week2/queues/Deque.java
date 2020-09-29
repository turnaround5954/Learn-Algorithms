package algs.week2.queues;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;

/**
 * Deque.
 * Implemeted using double-ended linked list
 */
public class Deque<Item> implements Iterable<Item> {
  private int n;      // size of the deque
  private Node first; // head of the deque
  private Node last;  // tail of the deque

  // helper double-ended linked list class
  private class Node {
    private Item item;
    private Node prev;
    private Node next;
  }

  // construct an empty deque
  public Deque() {
    n = 0;
    first = null;
    last = null;
  }

  // is the deque empty?
  public boolean isEmpty() {
    return first == null;
  }

  // return the number of items on the deque
  public int size() {
    return n;
  }

  // add the item to the front
  public void addFirst(Item item) {
    checkNull(item);
    Node oldFirst = first;
    first = new Node();
    first.item = item;
    first.prev = null;
    first.next = oldFirst;
    if (oldFirst != null) {
      oldFirst.prev = first;
    }
    if (n == 0) {
      last = first;
    }
    n++;
  }

  // add the item to the end
  public void addLast(Item item) {
    checkNull(item);
    Node oldLast = last;
    last = new Node();
    last.item = item;
    last.prev = oldLast;
    last.next = null;
    if (oldLast != null) {
      oldLast.next = last;
    }
    if (n == 0) {
      first = last;
    }
    n++;
  }

  // remove and return the item from the front
  public Item removeFirst() {
    checkEmpty();
    Item item = first.item;
    first = first.next;
    if (first != null) {
      first.prev = null;
    }
    else {
      last = null;
    }
    n--;
    return item;
  }

  // remove and return the item from the end
  public Item removeLast() {
    checkEmpty();
    Item item = last.item;
    last = last.prev;
    if (last != null) {
      last.next = null;
    }
    else {
      first = null;
    }
    n--;
    return item;
  }

  // return an iterator over items in order from front to end
  public Iterator<Item> iterator() {
    return new DeListIterator();
  }

  // an iterator, doesn't implement remove()
  private class DeListIterator implements Iterator<Item> {
    private Node current = first;

    public boolean hasNext() {
      return current != null;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public Item next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Item item = current.item;
      current = current.next;
      return item;
    }
  }

  // bound check for addFirst() and addLast()
  private void checkNull(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("calls with null argument");
    }
  }

  // bound check for removeFirst() or removeLast()
  private void checkEmpty() {
    if (isEmpty()) {
      throw new NoSuchElementException("calls when the deque is empty");
    }
  }

  // unit testing (optional)
  public static void main(String[] args) {
    Deque<Integer> dq = new Deque<Integer>();
    dq.addFirst(2);
    dq.addFirst(1);
    dq.addLast(3);
    dq.addLast(4);
    for (int e : dq) {
      StdOut.print(e + " ");
    }
    StdOut.print("\n");
    dq.removeFirst();
    dq.removeLast();
    for (int e : dq) {
      StdOut.print(e + " ");
    }
  }
}
