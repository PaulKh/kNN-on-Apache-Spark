package src.knn.model.blockNestedLoopModel;

import src.knn.model.Point;
import src.knn.model.PointWithDistance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.02.15
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class DistanceComputationEntity implements Serializable{
    Point rPoint;
    List<PointWithDistance> sPoints = new ArrayList<>();

    public DistanceComputationEntity(Point rPoint) {
        this.rPoint = rPoint;
    }

    public void addSPoint(PointWithDistance point) {
        sPoints.add(point);
    }
    public void addAllSPoint(List<PointWithDistance> points) {
        sPoints.addAll(points);
    }

    public Point getrPoint() {
        return rPoint;
    }

    public List<PointWithDistance> getsPoints() {
        return sPoints;
    }
}
