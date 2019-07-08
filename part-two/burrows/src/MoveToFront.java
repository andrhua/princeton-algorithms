import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    public static void encode() {
        char[] alphabet = initAlphabet();
        while (!BinaryStdIn.isEmpty()) {
            int i = indexOf(alphabet, BinaryStdIn.readChar());
            BinaryStdOut.write(i, 8);
            moveToFront(alphabet, i);
        }
        BinaryStdOut.flush();
    }

    private static int indexOf(char[] a, char key) {
        for (int i = 0; i < a.length; i++) {
            if (key == a[i]) return i;
        }
        return -1;
    }

    public static void decode() {
        char[] alphabet = initAlphabet();
        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readInt(8);
            BinaryStdOut.write(alphabet[i]);
            moveToFront(alphabet, i);
        }
        BinaryStdOut.flush();
    }

    private static char[] initAlphabet() {
        char[] a = new char[256];
        for (int i = 0; i < a.length; i++) a[i] = (char) i;
        return a;
    }

    private static void moveToFront(char[] a, int p) {
        for (int i = p; i > 0; i--) {
            char tmp = a[i];
            a[i] = a[i - 1];
            a[i - 1] = tmp;
        }
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}
