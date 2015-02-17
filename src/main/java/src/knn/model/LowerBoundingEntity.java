package src.knn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 25.01.15
 * Time: 11:56
 * To change this template use File | Settings | File Templates.
 */
public class LowerBoundingEntity implements Serializable{
    private int pivotPointId;
    private List<PointWithLowerBounds> sPointsWithLowerBounds = new ArrayList<PointWithLowerBounds>();
    private List<Point> rPointsAssignedToPivot = new ArrayList<Point>();

    public int getPivotPointId() {
        return pivotPointId;
    }

    public void setPivotPointId(int pivotPointId) {
        this.pivotPointId = pivotPointId;
    }

    public List<PointWithLowerBounds> getPointsWithLowerBounds() {
        return sPointsWithLowerBounds;
    }

    public void addPointWithLowerBounds(PointWithLowerBounds pointWithLowerBounds) {
        this.sPointsWithLowerBounds.add(pointWithLowerBounds);
    }

    public List<Point> getrPointsAssignedToPivot() {
        return rPointsAssignedToPivot;
    }

    public void setrPointsAssignedToPivot(List<Point> rPointsAssignedToPivot) {
        this.rPointsAssignedToPivot = rPointsAssignedToPivot;
    }
}
