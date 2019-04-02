import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;


public class Solver {
    private Node initial;
    private Node twin;
    private Node end;
    private int moves;
    private boolean isSolvable = true;
    private Stack<Board> solution = new Stack<>();

    private class NodeL1Comparator implements Comparator<Node>{
        @Override
        public int compare(Node node, Node other) {
            int priority = node.manhattan + node.moves - (other.manhattan + other.moves);
            return priority != 0 ? priority : node.manhattan - other.manhattan;
        }
    }

    public Solver(Board initial){
        if (initial == null) throw new IllegalArgumentException();
        this.initial = new Node(initial);
        this.twin = new Node(initial.twin());
        solve();
    }

    private void solve(){
        MinPQ<Node> pq = new MinPQ<>(new NodeL1Comparator());
        MinPQ<Node> twinPQ = new MinPQ<>(new NodeL1Comparator());
        pq.insert(initial);
        twinPQ.insert(twin);
        while (true) {
            if (step(pq)){
                buildSolution();
                break;
            }
            if (step(twinPQ)) {
                isSolvable = false;
                break;
            }
        }
    }

    private boolean step(MinPQ<Node> pq){
        Node optimal = pq.delMin();
        if (optimal.board.isGoal()) {
            end = optimal;
            return true;
        }
        for (Board neighbor: optimal.board.neighbors()) {
            if (optimal.predecessor == null || !optimal.predecessor.board.equals(neighbor))
                pq.insert(new Node(neighbor, optimal));
        }
        return false;
    }

    private void buildSolution(){
        moves = end.moves;
        do {
            solution.push(end.board);
            end = end.predecessor;
        } while (end != null);
    }


    public boolean isSolvable(){
        return isSolvable;
    }

    public int moves(){
        return isSolvable() ? moves : -1;
    }

    public Iterable<Board> solution(){
        return isSolvable ? () -> solution.iterator() : null;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private class Node{
        private Board board;
        private Node predecessor;
        private int moves;
        private int manhattan;

        Node(Board board){
            this.board = board;
            this.moves = 0;
            this.manhattan = board.manhattan();
        }

        Node(Board board, Node predecessor){
            this(board);
            this.predecessor = predecessor;
            this.moves = predecessor.moves + 1;
        }
    }
}
