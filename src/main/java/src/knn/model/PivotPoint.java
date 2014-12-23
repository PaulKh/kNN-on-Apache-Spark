package src.knn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 03.12.14
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class PivotPoint implements Serializable{
    private double maxDistanceR = 0;
    private double maxDistanceS = 0;
    private Point pivot = null;
    private List<PointWithDistance> rPointsAssignedToPivot = new ArrayList<PointWithDistance>();
    private List<PointWithDistance> sPointsAssignedToPivot = new ArrayList<PointWithDistance>();

    public Point getPivot() {
        return pivot;
    }

    public void setPivot(Point pivot) {
        this.pivot = pivot;
    }

    public List<PointWithDistance> getrPointsAssignedToPivot() {
        return rPointsAssignedToPivot;
    }

    public List<PointWithDistance> getsPointsAssignedToPivot() {
        return sPointsAssignedToPivot;
    }
    public void addPointToR(PointWithDistance point){
        rPointsAssignedToPivot.add(point);
    }
    public void addPointToS(PointWithDistance point){
        sPointsAssignedToPivot.add(point);
    }

    public void setrPointsAssignedToPivot(List<PointWithDistance> rPointsAssignedToPivot) {
        this.rPointsAssignedToPivot = rPointsAssignedToPivot;
    }

    public void setsPointsAssignedToPivot(List<PointWithDistance> sPointsAssignedToPivot) {
        this.sPointsAssignedToPivot = sPointsAssignedToPivot;
    }

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
}

