package algs.week2.queues;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * RandomizedQueue. Implemented using resizing array
 */
public class RandomizedQueue<Item> implements Iterable<Item> {
  private Item[] a; // array of items
  private int n; // number of elements on stack

  // construct an empty randomized queue
  @SuppressWarnings("unchecked")
  public RandomizedQueue() {
    n = 0;
    a = (Item[]) new Object[2];
  }

  // is the randomized queue empty?
  public boolean isEmpty() {
    return n == 0;
  }

  // return the number of items on the randomized queue
  public int size() {
    return n;
  }

  // resize the underlying array holding the elements
  @SuppressWarnings("unchecked")
  private void resize(int capacity) {
    // textbook implementation
    Item[] temp = (Item[]) new Object[capacity];
    for (int i = 0; i < n; i++) {
      temp[i] = a[i];
    }
    a = temp;

    // alternative implementation
    // a = java.util.Arrays.copyOf(a, capacity);
  }

  // add the item
  public void enqueue(Item item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    // double size of array if necessary
    if (n == a.length) {
      resize(2 * a.length);
    }

    a[n++] = item;
  }

  // remove and return a random item
  public Item dequeue() {
    checkEmpty();
    int idx = StdRandom.uniform(n);
    // swap the choosen item with the end of stack
    Item val = a[idx];
    a[idx] = a[n - 1];
    a[n - 1] = null;
    n--;
    // shrink size of array if necessary
    if (n > 0 && n == a.length / 4) {
      resize(a.length / 2);
    }
    return val;
  }

  // return a random item (but do not remove it)
  public Item sample() {
    checkEmpty();
    int idx = StdRandom.uniform(n);
    return a[idx];
  }

  // check empty when sample or dequeue
  private void checkEmpty() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
  }

  // return an independent iterator over items in random order
  public Iterator<Item> iterator() {
    return new RandomizedArrayIterator();
  }

  // an iterator, doesn't implement remove() since it's optional
  private class RandomizedArrayIterator implements Iterator<Item> {
    private int capacity;
    private Item[] arrayStorage;
    // do not use shuffle here
    @SuppressWarnings("unchecked")
    public RandomizedArrayIterator() {
      capacity = n;
      arrayStorage = (Item[]) new Object[n];
      for (int i = 0; i < n; i++) {
        arrayStorage[i] = a[i];
      }
    }

    public boolean hasNext() {
      return capacity > 0;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public Item next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      int idx = StdRandom.uniform(capacity);
      Item val = arrayStorage[idx];
      arrayStorage[idx] = arrayStorage[capacity - 1];
      arrayStorage[capacity - 1] = null;
      capacity--;
      return val;
    }
  }

  // unit testing (optional)
  public static void main(String[] args) {
    RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
    rq.enqueue(1);
    rq.enqueue(2);
    rq.enqueue(3);
    rq.enqueue(4);
    for (int e : rq) {
      StdOut.print(e + " ");
    }
    StdOut.print("\n");
    for (int e : rq) {
      StdOut.print(e + " ");
    }
    StdOut.print("\n");
    rq.dequeue();
    for (int e : rq) {
      StdOut.print(e + " ");
    }
  }
}