import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.NoSuchElementException;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        while(true){
            try {
                rq.enqueue(StdIn.readString());
            } catch (NoSuchElementException e){
                break;
            }
        }
        for (int i = 0; i < k; i++){
            StdOut.println(rq.dequeue());
        }
    }
}
