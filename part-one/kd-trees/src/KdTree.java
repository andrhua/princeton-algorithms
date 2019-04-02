import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.LinkedList;

public class KdTree {
    private int size = 0;
    private Node root;

    public KdTree(){
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public int size(){
        return size;
    }

    public void insert(Point2D p){
        checkNull(p);
        root = insert(root, p, true, 0, 0, 1, 1);
    }

    private Node insert(Node node, Point2D point, boolean isVertical, double xmin, double ymin, double xmax, double ymax){
        if (node == null) {
            size++;
            return new Node(point, new RectHV(xmin, ymin, xmax, ymax));
        }
        if (node.p.equals(point)) return node;
        if (compare(point, node.p, isVertical) < 0){
            if (isVertical) xmax = node.p.x(); else ymax = node.p.y();
            node.left = insert(node.left, point, !isVertical, xmin, ymin, xmax, ymax);
        } else {
            if (isVertical) xmin = node.p.x(); else ymin = node.p.y();
            node.right = insert(node.right, point, !isVertical, xmin, ymin, xmax, ymax);
        }
        return node;
    }

    private double compare(Point2D a, Point2D b, boolean isVertical){
        return isVertical ? a.x() - b.x() : a.y() - b.y();
    }

    public boolean contains(Point2D p){
        checkNull(p);
        return contains(root, p, true);
    }

    private boolean contains(Node node, Point2D point, boolean isVertical){
        if (node == null || !node.rect.contains(point)) return false;
        if (node.p.equals(point)) return true;
        if (compare(point, node.p, isVertical) < 0)
            return contains(node.left, point, !isVertical);
        else
            return contains(node.right, point, !isVertical);
    }

    public void draw(){
        draw(root, true);
    }

    private void draw(Node node, boolean isVertical){
        if (node != null){
            /*StdDraw.setPenColor(!isVertical ? Color.RED : Color.BLUE);
            StdDraw.filledRectangle(node.rect.xmin() + node.rect.width() / 2, node.rect.ymin() + node.rect.height() / 2, node.rect.width() / 2, node.rect.height()/ 2);*/
            StdDraw.setPenColor(isVertical ? Color.RED : Color.BLUE);
            if (isVertical) {
                StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
            } else {
                StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
            }
            StdDraw.setPenColor(Color.BLACK);
            StdDraw.filledCircle(node.p.x(), node.p.y(), .01);
            draw(node.left, !isVertical);
            draw(node.right, !isVertical);
        }
    }

    public Iterable<Point2D> range(RectHV rect){
        checkNull(rect);
        return range(root, rect, new LinkedList<>());
    }

    private LinkedList<Point2D> range(Node node, RectHV rect, LinkedList<Point2D> list){
        if (node == null || !rect.intersects(node.rect)) return list;
        if (rect.contains(node.p)) list.add(node.p);
        list = range(node.left, rect, list);
        list = range(node.right, rect, list);
        return list;
    }

    public Point2D nearest(Point2D p){
        checkNull(p);
        if (isEmpty()) return null;
        return nearest(root, p, true, root.p);
    }

    private Point2D nearest(Node node, Point2D query, boolean isVertical, Point2D minPoint){
        double min = query.distanceSquaredTo(minPoint);
        if (node == null || min < node.rect.distanceSquaredTo(query)) return minPoint;
        if (node.p.distanceSquaredTo(query) < min) minPoint = node.p;
        if (compare(query, node.p, isVertical) < 0){
            minPoint = nearest(node.left, query, !isVertical, minPoint);
            minPoint = nearest(node.right, query, !isVertical, minPoint);
        } else {
            minPoint = nearest(node.right, query, !isVertical, minPoint);
            minPoint = nearest(node.left, query, !isVertical, minPoint);
        }
        return minPoint;
    }


    public static void main(String[] args) {

    }

    private void checkNull(Object obj){
        if (obj == null) throw new IllegalArgumentException();
    }

    private static class Node{
        private Point2D p;
        private RectHV rect;
        private Node left;
        private Node right;

        Node(Point2D p, RectHV rect){
            this.p = p;
            this.rect = rect;
        }
    }
}
