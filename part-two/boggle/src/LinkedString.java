public class LinkedString {
    private int n;
    private final Letter head = new Letter();
    private final Letter tail = new Letter();

    private static class Letter {
        private char c;
        private Letter prev;
        private Letter next;

        Letter(char c) {
            this.c = c;
        }

        Letter() {
        }
    }

    public LinkedString() {
        clear();
    }

    public LinkedString append(char c) {
        Letter letter = new Letter(c);
        Letter lastLetter = tail.prev;
        lastLetter.next = letter;
        letter.prev = lastLetter;
        letter.next = tail;
        tail.prev = letter;
        n++;
        return this;
    }

    public void deleteLast() {
        Letter lastLetter = tail.prev;
        lastLetter.prev.next = tail;
        tail.prev = lastLetter.prev;
        n--;
    }

    public int length() {
        return n;
    }

    public void clear() {
        head.next = tail;
        tail.prev = head;
        n = 0;
    }

    @Override
    public String toString() {
        char[] chars = new char[n];
        Letter letter = head.next;
        for (int i = 0; i < n; i++) {
            chars[i] = letter.c;
            letter = letter.next;
        }
        return new String(chars);
    }
}
