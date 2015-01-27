package src.knn.model;

import src.knn.SharedMemory;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 25.01.15
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public class PointWithLowerBounds implements Serializable {
    private Point p;
    private double lowerBounds[];

    public PointWithLowerBounds(Point p) {
        this.p = p;
        lowerBounds = new double[SharedMemory.numberOfPivots];
    }
    public void setLowerBound(int id, double bound){
        lowerBounds[id] = bound;
    }

    public Point getP() {
        return p;
    }

    public double[] getLowerBounds() {
        return lowerBounds;
    }
}
