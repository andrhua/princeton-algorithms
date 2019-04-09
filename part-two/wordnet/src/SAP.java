import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class SAP {
    private final HashMap<Integer, Boolean> marked = new HashMap<>();
    private final HashMap<Integer, Integer> distTo = new HashMap<>();
    private final LRUCache<String, Result> cache;
    private final Digraph G;

    public SAP(Digraph G) {
        validateArg(G);
        this.G = new Digraph(G);
        cache = new LRUCache<>(1024);
    }

    public int length(int v, int w) {
        return getFromCache(v, w).length;
    }

    public int ancestor(int v, int w) {
        return getFromCache(v, w).ancestor;
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return getFromCache(v, w).length;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return getFromCache(v, w).ancestor;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private Result getFromCache(int v, int w) {
        validateVertices(v, w);
        String key = getKey(v, w);
        Result res = cache.get(key);
        if (res == null) {
            if (v == w) {
                res = new Result(0, v);
            } else {
                initHelperDS();
                res = bfs(v, w);
                cache.put(key, res);
            }
        }
        return res;
    }

    private Result getFromCache(Iterable<Integer> v, Iterable<Integer> w) {
        validateIterables(v, w);
        String key = getKey(v, w);
        Result res = cache.get(key);
        if (res == null) {
            initHelperDS();
            res = bfs(v, w);
            cache.put(key, res);
        }
        return res;
    }

    private String getKey(int v, int w) {
        return Math.min(v, w) + " " + Math.max(v, w);
    }

    private String getKey(Iterable<Integer> v, Iterable<Integer> w) {
        StringBuilder sb = new StringBuilder();
        for (int i: v) sb.append(i);
        sb.append(' ');
        for (int i: w) sb.append(i);
        return sb.toString();
    }

    private void initHelperDS() {
        marked.clear();
        distTo.clear();
    }

    private Result bfs(int v, int w) {
        Queue<Integer> q = new Queue<>();
        q.enqueue(v);
        marked.put(v, true);
        distTo.put(v, 0);
        w = toB(w);
        q.enqueue(w);
        marked.put(w, true);
        distTo.put(w, 0);
        return bfsHelper(q);
    }

    private Result bfs(Iterable<Integer> vA, Iterable<Integer> vB) {
        Queue<Integer> q = new Queue<>();
        for (int v: vA) {
            q.enqueue(v);
            marked.put(v, true);
            distTo.put(v, 0);
        }
        for (int v: vB) {
            if (marked.get(v) != null) return new Result(0, v);
            v = toB(v);
            q.enqueue(v);
            marked.put(v, true);
            distTo.put(v, 0);
        }
        return bfsHelper(q);
    }

    private Result bfsHelper(Queue<Integer> q) {
        int minDist = Integer.MAX_VALUE;
        int ancestor = -1;
        while (!q.isEmpty()) {
            int v = q.dequeue();
            // StdOut.println("Dequeued: " + v);
            for (int w : G.adj(v < 0 ? -v - 1 : v)) {
                if (v < 0) w = toB(w);
                if (marked.get(w) == null) {
                    marked.put(w, true);
                    distTo.put(w, distTo.get(v) + 1);
                    q.enqueue(w);
                    int wB = toB(w);
                    if (marked.get(wB) != null) {
                        // StdOut.println(distTo.get(w) + " " + distTo.get(wB));
                        int dist = distTo.get(w) + distTo.get(wB);
                        if (dist < minDist) {
                            minDist = dist;
                            ancestor = w < 0 ? wB : w;
                        }
                    }
                }
            }
        }
        return new Result(ancestor == -1 ? -1 : minDist, ancestor);
    }

    private void validateVertices(int v, int w) {
        validateArg(v);
        validateArg(w);
        if (outOfRange(v) || outOfRange(w)) throw new IllegalArgumentException();
    }

    private void validateIterables(Iterable<Integer> v, Iterable<Integer> w) {
        validateArg(v);
        validateArg(w);
        validateIterable(v);
        validateIterable(w);
    }

    private void validateIterable(Iterable<Integer> it) {
        for (Integer i: it) if (i == null || outOfRange(i)) throw new IllegalArgumentException();
    }

    private boolean outOfRange(int v) {
        return 0 > v || v >= G.V();
    }

    private void validateArg(Object arg) {
        if (arg == null) throw new IllegalArgumentException();
    }

    private int toB(int x) {
        return -x - 1;
    }

    private class Result {
        int length;
        int ancestor;

        Result(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }
    }
}
