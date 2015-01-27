package src.knn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 26.01.15
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public class FinalPartition implements Serializable {
    private int pivotPointId;
    private List<PartitionOfSAssignedToPivot> pointsS;
    private List<Point> pointsR;

    public FinalPartition() {
        pointsR = new ArrayList<Point>();
        pointsS = new ArrayList<PartitionOfSAssignedToPivot>();
    }

    public int getPivotPointId() {
        return pivotPointId;
    }

    public void setPivotPointId(int pivotPointId) {
        this.pivotPointId = pivotPointId;
    }

    public List<Point> getPointsR() {
        return pointsR;
    }

    public void setPointsR(List<Point> pointsR) {
        this.pointsR = pointsR;
    }
    public void addPartitionOfS(PartitionOfSAssignedToPivot partitionOfSAssignedToPivot){
        pointsS.add(partitionOfSAssignedToPivot);
    }
    public void addPartitionsOfS(List<PartitionOfSAssignedToPivot> partitionsOfSAssignedToPivot){
        pointsS.addAll(partitionsOfSAssignedToPivot);
    }

    public List<PartitionOfSAssignedToPivot> getPointsS() {
        return pointsS;
    }
}
