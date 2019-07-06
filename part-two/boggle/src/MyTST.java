public class MyTST {
    private Node root;

    static class Node {
        public char c;
        public boolean flag;
        public Node left, mid, right;
    }

    public Node get(Node x, char key) {
        if (x == null) return null;
        if      (key < x.c) return get(x.left, key);
        else if (key > x.c) return get(x.right, key);
        else                return x;
    }

    private Node get(Node x, String key, int i) {
        if (x == null) return null;
        char c = key.charAt(i);
        if      (c < x.c)              return get(x.left, key, i);
        else if (c > x.c)              return get(x.right, key, i);
        else if (i < key.length() - 1) return get(x.mid, key, i + 1);
        else                           return x;
    }

    public boolean contains(String key) {
        Node node = get(root, key, 0);
        return node != null && node.flag;
    }

    public void put(String key) {
        if (key.length() > 2) {
            root = put(root, key, 0);
        }
    }

    private Node put(Node x, String key, int i) {
        char c = key.charAt(i);
        if (x == null) {
            x = new Node();
            x.c = c;
        }
        if      (c < x.c)               x.left  = put(x.left,  key, i);
        else if (c > x.c)               x.right = put(x.right, key, i);
        else if (i < key.length() - 1)  x.mid   = put(x.mid,   key, i + 1);
        else                            x.flag  = true;
        return x;
    }

    public Node getRoot() {
        return root;
    }
}
