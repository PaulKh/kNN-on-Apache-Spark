package src.knn.model;

import src.knn.SharedMemory;

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
public class FirstPointDistributionEntity implements Serializable{
    private int pivotPointId;
    private List<PointWithDistance> rPointsAssignedToPivot = new ArrayList<PointWithDistance>();
    private List<PointWithDistance> sPointsAssignedToPivot = new ArrayList<PointWithDistance>(); //First k of the elements are sorted by the distance for the needs of algorithm

    public int getPivotPointId(){
        return pivotPointId;
    }
    public PivotPoint getPivotObject() {
        return SharedMemory.pivots[pivotPointId];
    }

    public void setPivotPointId(int pivotPointId) {
        this.pivotPointId = pivotPointId;
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


}

