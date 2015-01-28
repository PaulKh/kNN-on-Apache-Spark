package src.knn.model;

import src.knn.SharedMemory;
import src.knn.utilities.LimitedSizeQueue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 27.01.15
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class KNNOfPoint implements Serializable{
    private Point rPoint;
    private List<PointWithDistance> sPoints = new ArrayList<>();

    public KNNOfPoint(Point rPoint) {
        this.rPoint = rPoint;
        sPoints = new ArrayList<PointWithDistance>();
    }

    public Point getrPoint() {
        return rPoint;
    }
    public void addSPoint(PointWithDistance s){
         sPoints.add(s);
    }

    public List<PointWithDistance> getsPoints() {
        return sPoints;
    }
    public PointWithDistance getFarthestPoint(){
        PointWithDistance currentPoint = sPoints.get(0);
        for (PointWithDistance pointWithDistance: sPoints){
             if (currentPoint.getDistance() < pointWithDistance.getDistance()){
                 currentPoint = pointWithDistance;
             }
        }
        return currentPoint;
    }

    public int numberOfSPointsAdded(){
        return sPoints.size();
    }
    public void removePoint(PointWithDistance point){
        this.sPoints.remove(point);
    }
}
