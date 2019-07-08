import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray cfa = new CircularSuffixArray(s);
        BinaryStdOut.write(findZero(cfa));
        for (int i = 0; i < s.length(); i++) {
            BinaryStdOut.write(s.charAt(
                    (cfa.index(i) + s.length() - 1) % s.length()
            ));
        }
        BinaryStdOut.flush();
    }

    private static int findZero(CircularSuffixArray cfa) {
        for (int i = 0; i < cfa.length(); i++) {
            if (cfa.index(i) == 0) return i;
        }
        return -1;
    }

    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();
        char[] sorted = new char[t.length()];
        int[] next = new int[t.length()];
        int[] count = countSort(t);

        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            sorted[count[c]] = c;
            next[count[c]++] = i;
        }

        for (int i = 0; i < next.length; i++) {
            BinaryStdOut.write(sorted[first]);
            first = next[first];
        }
        BinaryStdOut.flush();
    }

    private static int[] countSort(String a) {
        int[] count = new int[R + 1];

        for (int i = 0; i < a.length(); i++)
            count[a.charAt(i) + 1]++;

        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];

//        for (char c : a) res[count[c]++] = c;
        return count;
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
    }
}
