import edu.princeton.cs.algs4.LinkedBag;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {
    private SET<Point2D> set = new SET<>();

    public PointSET(){}

    public boolean isEmpty(){
        return set.isEmpty();
    }

    public int size(){
        return set.size();
    }

    public void insert(Point2D p){
        checkNull(p);
        set.add(p);
    }

    public boolean contains(Point2D p){
        checkNull(p);
        return set.contains(p);
    }

    public void draw(){
        for (Point2D p:set){
           StdDraw.filledCircle(p.x(), p.y(), .01);
        }
    }

    public Iterable<Point2D> range(RectHV rect){
        checkNull(rect);
        LinkedBag<Point2D> bag = new LinkedBag<>();
        for (Point2D p:set){
            if (rect.contains(p)) bag.add(p);
        }
        return bag;
    }

    public Point2D nearest(Point2D p){
        checkNull(p);
        Point2D nearest = null;
        double minSquaredDist = 1.01;
        for (Point2D point:set){
            double temp = p.distanceSquaredTo(point);
            if (temp < minSquaredDist){
                nearest = point;
                minSquaredDist = temp;
            }
        }
        return nearest;
    }

    public static void main(String[] args) {

    }

    private void checkNull(Object obj){
        if (obj == null) throw new IllegalArgumentException();
    }
}
