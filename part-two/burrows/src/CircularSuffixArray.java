import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    private final String s;
    private final int[] index;

    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();
        this.s = s;
        index = new int[s.length()];
        for (int i = 0; i < index.length; i++) index[i] = i;
        circularSuffixSort(s);
    }

    public int length() {
        return s.length();
    }

    public int index(int i) {
        if (i < 0 || i >= length()) throw new IllegalArgumentException();
        return index[i];
    }

    public static void main(String[] args) {
        CircularSuffixArray cfa = new CircularSuffixArray("ABRACADABRA!");
        StdOut.println(cfa.length());
        StdOut.println(cfa.index(0));
    }

    // 3-way string quicksort optimized for sorting circular suffixes
    private void circularSuffixSort(String a) {
        sort(a, 0, a.length() - 1, 0);
    }

    private void sort(String a, int lo, int hi, int d) {
//        StdOut.printf("lo: %d   hi: %d  d: %d\n", lo, hi, d);
        if (lo >= hi || d >= a.length()) return;
        int lt = lo, gt = hi;
        char v = charAt(a, index[lo], d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(a, index[i], d);
            if      (t < v) swap(index, lt++, i++);
            else if (t > v) swap(index, i, gt--);
            else            i++;
        }
        sort(a, lo, lt - 1, d);
        sort(a, lt, gt, d + 1);
        sort(a, gt + 1, hi, d);
    }

    private char charAt(String a, int offset, int d) {
        return a.charAt((d + offset) % a.length());
    }

    private void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}

