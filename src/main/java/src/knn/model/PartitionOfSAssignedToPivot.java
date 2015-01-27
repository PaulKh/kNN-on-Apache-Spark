package src.knn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 26.01.15
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public class PartitionOfSAssignedToPivot implements Serializable {
    private int pivotPointId;//source of points
    List<Point> sPoints = new ArrayList<>();

    public PartitionOfSAssignedToPivot(int pivotPointId) {
        this.pivotPointId = pivotPointId;
        sPoints = new ArrayList<>();
    }

    public int getPivotPointId() {
        return pivotPointId;
    }
    public void addPoint(Point point){
        sPoints.add(point);
    }
    public List<Point> getsPoints() {
        return sPoints;
    }
}
