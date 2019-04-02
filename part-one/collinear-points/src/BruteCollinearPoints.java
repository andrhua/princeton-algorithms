import java.util.Arrays;
import java.util.LinkedList;

public class BruteCollinearPoints {
    private LineSegment[] segments;

    public BruteCollinearPoints(Point[] points){
        checkNull(points);
        Point[] ps = points.clone();
        checkRepeat(ps);
        int n = ps.length;
        LinkedList<LineSegment> segments = new LinkedList<>();
        for (int i = 0; i < n - 3; i++){
            Point p = ps[i];
            for (int j = i + 1; j < n - 2; j++){
                Point q = ps[j];
                double slopePQ = p.slopeTo(q);
                for (int k = j + 1; k < n - 1; k++){
                    Point r = ps[k];
                    double slopPR = p.slopeTo(r);
                    if (slopePQ != slopPR) continue;
                    for (int l = k + 1; l < n; l++){
                        Point s = ps[l];
                        double slopePS = p.slopeTo(s);
                        if (slopePQ == slopPR && slopPR == slopePS){
                            segments.add(new LineSegment(p, s));
                        }
                    }
                }
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
