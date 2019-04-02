import java.util.Arrays;
import java.util.LinkedList;

public class FastCollinearPoints {
    private LineSegment[] segments;

    public FastCollinearPoints(Point[] points){
        checkNull(points);
        Point[] ps = points.clone();
        checkRepeat(ps);
        LinkedList<LineSegment> segments = new LinkedList<>();
        LinkedList<Point> candidates = new LinkedList<>();
        int n = ps.length;
        for (Point p : ps) {
            Point[] tmp = ps.clone();
            Arrays.sort(tmp, p.slopeOrder());
            int j = 1;
            while (j < n) {
                double targetSlope = p.slopeTo(tmp[j]);
                do {
                    candidates.add(tmp[j++]);
                } while (j < n && p.slopeTo(tmp[j]) == targetSlope);
                if (candidates.size() >= 3 && p.compareTo(candidates.peek()) < 0) {
                    segments.add(new LineSegment(p, candidates.peekLast()));
                }
                candidates.clear();
            }
        }
        this.segments = segments.toArray(new LineSegment[0]);
    }

    private void checkNull(Point[] points){
        if (points == null) throw new IllegalArgumentException();
        for (Point point : points) {
            if (point == null) throw new IllegalArgumentException();
        }
    }

    private void checkRepeat(Point[] points){
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++)
            if (points[i].compareTo(points[i + 1]) == 0) throw new IllegalArgumentException();
    }

    public int numberOfSegments(){
        return segments.length;
    }

    public LineSegment[] segments(){
        return segments.clone();
    }
 }
