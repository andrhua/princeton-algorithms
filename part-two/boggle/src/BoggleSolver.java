import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.HashSet;

public class BoggleSolver {
    private final MyTST dict;
    private boolean[] marked;
    private HashSet<String> words;

    public BoggleSolver(String[] dictionary) {
        dict = new MyTST();
        for (String word: dictionary) {
            dict.put(word);
        }
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        final int rows = board.rows();
        final int cols = board.cols();
        final Graph G = new Graph(rows * cols);
        this.words = new HashSet<>();
        this.marked = new boolean[G.V()];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int source = i * cols + j;
                if (j < cols - 1) {
                    if (i > 0) G.addEdge(source, source - cols + 1);
                    G.addEdge(source, source + 1);
                    if (i < rows - 1) G.addEdge(source, source + cols + 1);
                }
                if (i < rows - 1) G.addEdge(source, source + cols);
            }
        }

        LinkedString key = new LinkedString();
        for (int u = 0; u < G.V(); u++) {
            dfs(G, u, key, board, dict.getRoot());
            key.clear();
        }
        return words;
    }
    
    private void dfs(Graph G, int s, LinkedString key, BoggleBoard board, MyTST.Node root) {
        MyTST.Node node = dict.get(root, vertexToLetter(board, s));
        if (node != null) {
            boolean quFlag = false;
            if (node.c == 'Q') {
                MyTST.Node uNode = dict.get(node.mid, 'U');
                if (uNode != null) {
                    quFlag = true;
                    addWord(key.append('Q').append('U'), uNode.flag);
                    node = uNode;
                } else {
                    return;
                }
            } else {
                addWord(key.append(node.c), node.flag);
            }
            marked[s] = true;
            for (int v : G.adj(s)) {
                if (!marked[v]) dfs(G, v, key, board, node.mid);
            }
            if (quFlag) key.deleteLast();
            key.deleteLast();
            marked[s] = false;
        }
    }

    private void addWord(LinkedString word, boolean flag) {
        if (word.length() > 2 && flag) {
            words.add(word.toString());
        }
    }

    private char vertexToLetter(BoggleBoard board, int v) {
        return board.getLetter(v / board.cols(), v % board.cols());
    }

    public int scoreOf(String word) {
        if (word.length() > 2 && dict.contains(word)) {
            switch (word.length()) {
                case 3:
                case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                default:
                    return 11;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        for (int i = 0; i < 10000; i++) {
            BoggleBoard board = new BoggleBoard(StdRandom.uniform(10) + 1, StdRandom.uniform(10) + 1);
            StdOut.println(board.toString());
            int score = 0;
            for (String word : solver.getAllValidWords(board)) {
                StdOut.println(word);
                score += solver.scoreOf(word);
            }
            StdOut.println("Score = " + score);
        }
    }
}
