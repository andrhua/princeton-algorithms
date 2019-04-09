import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedBag;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WordNet {
    private final HashMap<String, LinkedBag<Integer>> nounToIdMap = new HashMap<>();
    private final ArrayList<String> synsets = new ArrayList<>();
    private final SAP sap;

    public WordNet(String synsets, String hypernyms) {
        validateArg(synsets);
        validateArg(hypernyms);
        In synsetsInput = new In(synsets);
        while (synsetsInput.hasNextLine()) {
            String[] tokens = synsetsInput.readLine().split(",");
            int id = Integer.parseInt(tokens[0]);
            String synset = tokens[1];
            this.synsets.add(synset);
            String[] nouns = synset.split(" ");
            for (String noun: nouns) {
                if (!nounToIdMap.containsKey(noun)) {
                    LinkedBag<Integer> bag = new LinkedBag<>();
                    bag.add(id);
                    nounToIdMap.put(noun, bag);
                } else {
                    nounToIdMap.get(noun).add(id);
                }
            }
        }
        Digraph G = new Digraph(this.synsets.size());
        In hypernymsInput = new In(hypernyms);
        while (hypernymsInput.hasNextLine()) {
            int[] ids = Arrays.stream(hypernymsInput.readLine().split(","))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            for (int i = 1; i < ids.length; i++)
                G.addEdge(ids[0], ids[i]);
        }
        validateSingleRooted(G);
        validateAcyclic(G);
        sap = new SAP(G);
    }

    public Iterable<String> nouns() {
        return nounToIdMap.keySet();
    }

    public boolean isNoun(String word) {
        validateArg(word);
        return nounToIdMap.get(word) != null;
    }

    public int distance(String nounA, String nounB) {
        validateNouns(nounA, nounB);
        return sap.length(nounToIdMap.get(nounA), nounToIdMap.get(nounB));
    }

    public String sap(String nounA, String nounB) {
        validateNouns(nounA, nounB);
        return synsets.get(sap.ancestor(nounToIdMap.get(nounA), nounToIdMap.get(nounB)));
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        String a = "X", b = "free-liver";
        StdOut.println(wordnet.distance(a, b));
        StdOut.println(wordnet.sap(a, b));
    }

    private void validateAcyclic(Digraph digraph) {
        DirectedCycle check = new DirectedCycle(digraph);
        if (check.hasCycle()) throw new IllegalArgumentException("Digraph has a cycle");
    }

    private void validateSingleRooted(Digraph G) {
        int roots = 0;
        for (int i = 0; i < G.V(); i++) {
            if (G.outdegree(i) == 0) roots++;
            if (roots > 1) throw new IllegalArgumentException("Digraph has multiple roots");
        }
    }

    private void validateArg(Object arg) {
        if (arg == null) throw new IllegalArgumentException();
    }

    private void validateNouns(String a, String b) {
        validateArg(a);
        validateArg(b);
        if (!isNoun(a) || !isNoun(b)) throw new IllegalArgumentException();
    }
}
