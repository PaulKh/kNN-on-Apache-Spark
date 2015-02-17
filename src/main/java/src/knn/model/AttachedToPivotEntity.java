package src.knn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 17.02.15
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
public class AttachedToPivotEntity implements Serializable{
    private int pivotPointId;
    private List<Point> sPointsAssignedToPivot = new ArrayList<>();
    private List<Point> rPointsAssignedToPivot = new ArrayList<Point>();

    public int getPivotPointId() {
        return pivotPointId;
    }

    public AttachedToPivotEntity(FirstPointDistributionEntity distributionEntity) {
        this.pivotPointId = distributionEntity.getPivotPointId();
        setrPointsAssignedToPivot(distributionEntity.getrPointsAssignedToPivot());
        setsPointsAssignedToPivot(distributionEntity.getsPointsAssignedToPivot());
    }

    public List<Point> getrPointsAssignedToPivot() {
        return rPointsAssignedToPivot;
    }

    public List<Point> getsPointsAssignedToPivot() {
        return sPointsAssignedToPivot;
    }

    public void setsPointsAssignedToPivot(List<PointWithDistance> sPointsAssignedToPivot) {
        for (PointWithDistance pointWithDistance: sPointsAssignedToPivot){
            this.sPointsAssignedToPivot.add(pointWithDistance.getPoint());
        }
    }
    public void setrPointsAssignedToPivot(List<PointWithDistance> rPointsAssignedToPivot) {
        for (PointWithDistance pointWithDistance: rPointsAssignedToPivot){
            this.rPointsAssignedToPivot.add(pointWithDistance.getPoint());
        }
    }
}
