import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedBag;

import java.util.HashMap;

public class BaseballElimination {
    private final HashMap<String, Integer> team2num = new HashMap<>();
    private final HashMap<String, LinkedBag<String>> certificates = new HashMap<>();
    private final int n;
    private final int[] won;
    private final int[] lost;
    private final int[] remaining;
    private final int[][] against;
    private final String[] teams;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        n = in.readInt();
        won = new int[n];
        lost = new int[n];
        remaining = new int[n];
        against = new int[n][n];
        teams = new String[n];
        for (int i = 0; i < n; i++) {
            String team = in.readString();
            team2num.put(team, i);
            teams[i] = team;
            won[i] = in.readInt();
            lost[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < n; j++)
                against[i][j] = in.readInt();
        }
    }

    public int numberOfTeams() {
        return teams.length;
    }

    public Iterable<String> teams() {
        return team2num.keySet();
    }

    public int wins(String team) {
        validateTeam(team);
        return won[team2num.get(team)];
    }

    public int losses(String team) {
        validateTeam(team);
        return lost[team2num.get(team)];
    }

    public int remaining(String team) {
        validateTeam(team);
        return remaining[team2num.get(team)];
    }

    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        int i1 = team2num.get(team1);
        int i2 = team2num.get(team2);
        return against[i1][i2];
    }

    public boolean isEliminated(String team) {
        validateTeam(team);
        return !getCertificate(team).isEmpty();
    }

    private LinkedBag<String> getCertificate(String team) {
        LinkedBag<String> certificate = certificates.get(team);
        if (certificate == null) {
            int x = team2num.get(team);
            LinkedBag<String> trivial = checkTrivialElimination(x);
            certificate = !trivial.isEmpty()
                    ? trivial
                    : checkNonTrivialElimination(x);
            certificates.put(team, certificate);
        }
        return certificate;
    }

    private LinkedBag<String> checkTrivialElimination(int x) {
        LinkedBag<String> bag = new LinkedBag<>();
        for (int i = 0; i < n; i++) {
            if (won[x] + remaining[x] < won[i]) {
                bag.add(teams[i]);
            }
        }
        return bag;
    }

    private LinkedBag<String> checkNonTrivialElimination(int x) {
        LinkedBag<String> bag = new LinkedBag<>();
        if (n > 1) {
            FlowNetwork G = buildFlowNetwork(x);
            FordFulkerson FF = new FordFulkerson(G, 0, G.V() - 1);
            int offset = n * (n - 1) / 2;
            for (int v = offset; v < offset + n; v++) {
                if (FF.inCut(v)) bag.add(teams[v - offset]);
            }
            // if (!bag.isEmpty()) StdOut.printf("Team %s eliminated non-trivially!\n", teams[x]);
        }
        return bag;
    }

    /**
     *
     * @param x index of team that being checked for elimination
     * @return FlowNetwork to pass into Ford-Fulkerson algorithm
     */
    private FlowNetwork buildFlowNetwork(int x) {
        FlowNetwork G = new FlowNetwork(1 + n*(n-1)/2 + n + 1);
        int k = 1;
        for (int i = 0; i < n; i++) {
            if (i == x) continue;
            int offset = n*(n-1)/2;
            for (int j = i + 1; j < n; j++) {
                if (j == x) continue;
                G.addEdge(new FlowEdge(0, k, against[i][j]));
                G.addEdge(new FlowEdge(k, offset + i, Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(k++, offset + j, Double.POSITIVE_INFINITY));
            }
            G.addEdge(new FlowEdge(offset + i, G.V() - 1, won[x] + remaining[x] - won[i]));
        }
        return G;
    }

    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        Iterable<String> c = getCertificate(team);
        return c.iterator().hasNext() ? c: null;
    }

    private void validateTeam(String team) {
        if (!team2num.containsKey(team)) throw new IllegalArgumentException();
    }
}
