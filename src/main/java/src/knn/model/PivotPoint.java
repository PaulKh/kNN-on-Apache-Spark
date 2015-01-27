package src.knn.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 23.01.15
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class PivotPoint implements Serializable{
    private Point pivot = null;
    private double maxDistanceR = 0;
    private double maxDistanceS = 0;

    public double getMaxDistanceR() {
        return maxDistanceR;
    }

    public void setMaxDistanceR(double maxDistanceR) {
        this.maxDistanceR = maxDistanceR;
    }

    public double getMaxDistanceS() {
        return maxDistanceS;
    }

    public void setMaxDistanceS(double maxDistanceS) {
        this.maxDistanceS = maxDistanceS;
    }
    public Point getPivot() {
        return pivot;
    }

    public void setPivot(Point pivot) {
        this.pivot = pivot;
    }
}
