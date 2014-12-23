package src.knn.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 04.12.14
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class PointWithDistance implements Serializable{
    private Point point;
    private double distance;

    public PointWithDistance(Point point, double distance) {
        this.point = point;
        this.distance = distance;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
