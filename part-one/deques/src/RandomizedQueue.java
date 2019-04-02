import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] a = (Item[]) new Object[1];
    private int tail = 0;

    public boolean isEmpty(){
        return tail == 0;
    }

    public int size(){
        return tail;
    }

    public void enqueue(Item value){
        if (value == null) throw new IllegalArgumentException();
        a[tail++] = value;
        if (tail >= a.length) resize(a.length * 2);
    }

    public Item dequeue(){
        checkIfEmpty();
        int random = StdRandom.uniform(tail);
        Item value = a[random];
        a[random] = a[--tail];
        a[tail] = null;
        if (tail == a.length / 4) resize(a.length / 2);
        return value;
    }

    public Item sample(){
        checkIfEmpty();
        return a[StdRandom.uniform(tail)];
    }

    private void resize(int capacity){
        Item[] newA = (Item[]) new Object[capacity];
        if (tail >= 0) System.arraycopy(a, 0, newA, 0, tail);
        a = newA;
    }

    private void checkIfEmpty(){
        if (isEmpty()) throw new NoSuchElementException();
    }

    @Override
    public Iterator<Item> iterator() {
        return new RandomQueueIterator();
    }

    private class RandomQueueIterator implements Iterator<Item>{
        private int[] permutation = StdRandom.permutation(tail);
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < permutation.length;
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return a[permutation[i++]];
        }
    }

    public static void main(String[] args) {

    }

}
