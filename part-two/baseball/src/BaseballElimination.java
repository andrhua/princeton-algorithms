import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinkedBag;

import java.util.HashMap;

/**
 * The {@code BaseballElimination} class represent a solution to a
 * <a href=http://coursera.cs.princeton.edu/algs4/assignments/baseball.html>Baseball elimination problem</a>.
 * Given a standings in a baseball division, program determines
 * which teams are eliminated from winning the top place.
 * <p>
 * Score: 100/100
 *
 * @author andrhua
 * @version Collector's edition
 */

public class BaseballElimination {
    private final HashMap<String, Integer> indices = new HashMap<>();
    private final HashMap<String, LinkedBag<String>> certificates = new HashMap<>();
    private final int n;
    private final int[] won;
    private final int[] lost;
    private final int[] remaining;
    private final int[][] against;
    private final String[] teams;

    /**
     * Constructs standings records according to {@code filename}.
     *
     * @param filename path to file containing correct teams standings.
     */
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
            indices.put(team, i);
            teams[i] = team;
            won[i] = in.readInt();
            lost[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < n; j++)
                against[i][j] = in.readInt();
        }
    }

    /**
     * Returns the number of teams in division.
     * @return the number of teams in division.
     */
    public int numberOfTeams() {
        return teams.length;
    }

    /**
     * Returns names of all teams in division.
     * @return names of all teams in division as an Iterable
     */
    public Iterable<String> teams() {
        return indices.keySet();
    }

    /**
     * Returns {@code team}'s number of wins
     * @param team name of the team
     * @return {@code team}'s number of wins
     * @throws IllegalArgumentException if team is not present in the standings
     */
    public int wins(String team) {
        validateTeam(team);
        return won[indices.get(team)];
    }

    /**
     * Returns {@code team}'s number of losses
     * @param team name of the team
     * @return {@code team}'s number of losses
     * @throws IllegalArgumentException if team is not present in the standings
     */
    public int losses(String team) {
        validateTeam(team);
        return lost[indices.get(team)];
    }

    /**
     * Returns {@code team}'s number of remaining games
     * @param team name of the team
     * @return {@code team}'s number of remaining games
     * @throws IllegalArgumentException if team is not present in the standings
     */
    public int remaining(String team) {
        validateTeam(team);
        return remaining[indices.get(team)];
    }

    /**
     * Returns number of remaining games between {@code team1} and {@code team2}
     * @param team1 name of the first team
     * @param team2 name of the second team
     * @return number of remaining games between {@code team1} and {@code team2}
     * @throws IllegalArgumentException if any team is not present in standings
     */
    public int against(String team1, String team2) {
        validateTeam(team1);
        validateTeam(team2);
        int i1 = indices.get(team1);
        int i2 = indices.get(team2);
        return against[i1][i2];
    }

    /**
     * Checks whether {@code team} is eliminated from winning the division
     * by obtaining the elimination certificate. If it is empty, then there is
     * possibility for {@code team} to win the division.
     * @param team name of the team
     * @return true if team is eliminated and false otherwise
     * @throws IllegalArgumentException if team is not present in standings
     */
    public boolean isEliminated(String team) {
        validateTeam(team);
        return !getCertificate(team).isEmpty();
    }

    /**
     * Gets an elimination certificate for {@code team} from cache.
     * If it does not present there, calculate one and add it to cache.
     * @return bag of teams that can win division, thus eliminating {@code team} from winning.
     */
    private LinkedBag<String> getCertificate(String team) {
        LinkedBag<String> certificate = certificates.get(team);
        if (certificate == null) {
            int x = indices.get(team);
            LinkedBag<String> trivial = checkTrivialElimination(x);
            certificate = !trivial.isEmpty()
                    ? trivial
                    : checkNonTrivialElimination(x);
            certificates.put(team, certificate);
        }
        return certificate;
    }

    /**
     * Checks if any other team has more wins than sum of wins
     * and remaining games of given one {@code t}.
     * @param t index of team in division
     * @return bag of teams that trivially eliminate {@code t}
     */
    private LinkedBag<String> checkTrivialElimination(int t) {
        LinkedBag<String> bag = new LinkedBag<>();
        for (int i = 0; i < n; i++) {
            if (won[t] + remaining[t] < won[i]) {
                bag.add(teams[i]);
            }
        }
        return bag;
    }

    /**
     * Creates a flow network and solves maxflow problem for given team {@code t}.
     * Consider set T of all division teams without {@code t}. If they can
     * distribute wins in games between themselves in such way that {@code t}
     * has more possible wins then each of them, then {@code t} is not eliminated.
     * @param t index of team in division
     * @return bag of teams eliminating {@code t}
     */
    private LinkedBag<String> checkNonTrivialElimination(int t) {
        LinkedBag<String> bag = new LinkedBag<>();
        if (n > 1) {
            FlowNetwork G = buildFlowNetwork(t);
            FordFulkerson FF = new FordFulkerson(G, 0, G.V() - 1);
            int offset = n * (n - 1) / 2;
            for (int v = offset; v < offset + n; v++) {
                if (FF.inCut(v)) bag.add(teams[v - offset]);
            }
        }
        return bag;
    }

    /**
     * Builds a flow network to solve the maxflow problem.
     * @param t index of team in division
     * @return {@code FlowNetwork} to pass into Ford-Fulkerson algorithm
     */
    private FlowNetwork buildFlowNetwork(int t) {
        FlowNetwork G = new FlowNetwork(1 + n*(n-1)/2 + n + 1);
        int k = 1;
        for (int i = 0; i < n; i++) {
            if (i == t) continue;
            int offset = n*(n-1)/2;
            for (int j = i + 1; j < n; j++) {
                if (j == t) continue;
                G.addEdge(new FlowEdge(0, k, against[i][j]));
                G.addEdge(new FlowEdge(k, offset + i, Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(k++, offset + j, Double.POSITIVE_INFINITY));
            }
            G.addEdge(new FlowEdge(offset + i, G.V() - 1, won[t] + remaining[t] - won[i]));
        }
        return G;
    }

    /**
     * Returns certificate of elimination. In other words, a list of teams
     * that can win division no matter how many wins {@code team} would have.
     * @param team name of the team
     * @return teams eliminating {@code team} as an Iterable
     * @throws IllegalArgumentException if {@code team} is not present in standings
     */
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        Iterable<String> c = getCertificate(team);
        return c.iterator().hasNext() ? c : null;
    }

    // check whether given team is present in standings
    private void validateTeam(String team) {
        if (!indices.containsKey(team)) throw new IllegalArgumentException();
    }
}
