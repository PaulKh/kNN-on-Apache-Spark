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
    private Point pivot = null;
    private List<Point> rPointsAssignedToPivot = new ArrayList<Point>();
    private List<Point> sPointsAssignedToPivot = new ArrayList<Point>();

    public Point getPivot() {
        return pivot;
    }

    public void setPivot(Point pivot) {
        this.pivot = pivot;
    }

    public List<Point> getrPointsAssignedToPivot() {
        return rPointsAssignedToPivot;
    }

    public List<Point> getsPointsAssignedToPivot() {
        return sPointsAssignedToPivot;
    }
    public void addPointToR(Point point){
        rPointsAssignedToPivot.add(point);
    }
    public void addPointToS(Point point){
        sPointsAssignedToPivot.add(point);
    }

    public void setrPointsAssignedToPivot(List<Point> rPointsAssignedToPivot) {
        this.rPointsAssignedToPivot = rPointsAssignedToPivot;
    }

    public void setsPointsAssignedToPivot(List<Point> sPointsAssignedToPivot) {
        this.sPointsAssignedToPivot = sPointsAssignedToPivot;
    }
}

