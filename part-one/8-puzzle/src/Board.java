import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

public class Board {
    private short[] grid;
    private int n;
    private int i1, i2;
    private Stack<Board> neighbors;

    public Board(int[][] blocks){
        this.n = blocks.length;
        this.grid = new short[n * n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                grid[i * n + j] = (short) blocks[i][j];
            }
        }
        int[] indices;
        do indices = StdRandom.permutation(grid.length, 2); while (grid[indices[0]] == 0 || grid[indices[1]] == 0);
        i1 = indices[0];
        i2 = indices[1];
    }

    private Board(short[] blocks){
        this.n = (int)Math.sqrt(blocks.length);
        this.grid = blocks;
    }

    public int dimension(){
        return n;
    }

    public int hamming(){
        int outOfPlace = 0;
        for (int i = 0; i < n * n; i++){
            int v = grid[i];
            if (v > 0 && v != i + 1) outOfPlace++;
        }
        return outOfPlace;
    }

    public int manhattan(){
        int avenues = 0;
        for (int i = 0; i < n * n; i++){
            int v = grid[i];
            if (v > 0) avenues += Math.abs(getRow(v - 1) - getRow(i)) + Math.abs(getCol(v - 1) - getCol(i));
        }
        return avenues;
    }

    public boolean isGoal(){
        return hamming() == 0;
    }

    public Board twin(){
        short[] twin = this.grid.clone();
        swap(twin, i1, i2);
        return new Board(twin);
    }

    private int getRow(int value){
        return value / n;
    }

    private int getCol(int value){
        return value % n - 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) return false;
        Board that = (Board) obj;
        if (this.grid.length != that.grid.length) return false;
        for (int i = 0; i < n * n; i++){
            if (this.grid[i] != that.grid[i]) return false;
        }
        return true;
    }

    public Iterable<Board> neighbors(){
        if (neighbors == null) {
            neighbors = new Stack<>();
            for (int i = 0; i < n * n; i++) {
                if (grid[i] == 0) {
                    if ((i + 1) % n != 0) addNeighbor(i, i + 1);
                    if (i % n != 0) addNeighbor(i, i - 1);
                    if (i + n < n * n) addNeighbor(i, i + n);
                    if (i - n >= 0) addNeighbor(i, i - n);
                    break;
                }
            }
        }
        return neighbors;
    }

    private void addNeighbor(int from, int to){
        short[] newGrid = grid.clone();
        swap(newGrid, from, to);
        neighbors.push(new Board(newGrid));
    }

    private void swap(short[] a, int i, int j){
        short temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n);
        sb.append("\n");
        for (int i = 0; i < n * n; i++){
            sb.append(grid[i]);
            sb.append(" ");
            if ((i + 1) % n == 0) sb.append("\n");
        }
        return sb.toString();
    }
}
