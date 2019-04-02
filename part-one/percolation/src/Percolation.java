import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int openSites = 0;
    private int n;
    private boolean[] isOpen;
    private WeightedQuickUnionUF backend;

    public Percolation(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = size;
        backend = new WeightedQuickUnionUF(n * n + 2);
        isOpen = new boolean[n * n + 2];
    }

    private int toId(final int i, final int j){
        return (i - 1) * n + j;
    }

    private void checkArgs(int row, int col){
        if (row <= 0 || row > n || col <= 0 || col > n)
            throw new IllegalArgumentException("Invalid grid coordinates.");
    }

    public void open(int row, int col){
        checkArgs(row, col);
        int i = toId(row, col);
        if (i > 0 && i <= n) backend.union(0, i);
        if (i > n * (n - 1)  && i <= n * n ) backend.union(i, n * n + 1);
        if (i > n && isOpen[i - n]) backend.union(i, i - n); // up
        if (i <= n*(n-1) && isOpen[i + n]) backend.union(i, i + n); // down
        if (i % n != 0 && isOpen[i + 1]) backend.union(i, i + 1); // right
        if ((i - 1) % n != 0 && isOpen[i - 1]) backend.union(i, i - 1); // left
        if (!isOpen[i]) {
            openSites += 1;
            isOpen[i] = true;
        }
    }

    public boolean isOpen(int row, int col){
        checkArgs(row, col);
        return isOpen[toId(row, col)];
    }

    public boolean isFull(int row, int col){
        checkArgs(row, col);
        return isOpen(row, col) && backend.connected(0, toId(row, col));
    }

    public int numberOfOpenSites(){
        return openSites;
    }

    public boolean percolates(){
        return backend.connected(0, n * n + 1);
    }
}
