import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node head = new Node(null);
    private Node tail = new Node(null);
    private int size = 0;

    public Deque(){
        head.next = tail;
        tail.prev = head;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void addFirst(Item value){
        checkIfNull(value);
        Node node = new Node(value);
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        size++;
    }

    public void addLast(Item value){
        checkIfNull(value);
        Node node = new Node(value);
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;
        size++;
    }

    public Item removeFirst(){
        checkIfEmpty();
        Node realHead = head.next;
        Item first = realHead.value;
        realHead = realHead.next;
        head.next = realHead;
        realHead.prev = head;
        size--;
        return first;
    }

    public Item removeLast(){
        checkIfEmpty();
        Node realTail = tail.prev;
        Item last = realTail.value;
        realTail = realTail.prev;
        tail.prev = realTail;
        realTail.next = tail;
        size--;
        return last;
    }

    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private void checkIfEmpty(){
        if (isEmpty()) throw new NoSuchElementException();
    }

    private void checkIfNull(Item value){
        if (value == null) throw new IllegalArgumentException();
    }

    private class DequeIterator implements Iterator<Item>{
        private Node current = head.next;

        @Override
        public boolean hasNext() {
            return current.value != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            Item value = current.value;
            current = current.next;
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class Node {
        Item value;
        Node next;
        Node prev;

        Node(Item value){
            this.value = value;
        }
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(1);
        deque.addFirst(2);
        deque.addFirst(3);
        deque.addFirst(5);
        deque.removeFirst();
        deque.removeLast();
        Iterator<Integer> iterator = deque.iterator();
        System.out.println(iterator.next());
        System.out.println(iterator.next());

    }
}
