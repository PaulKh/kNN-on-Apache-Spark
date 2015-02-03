package src.knn.model.blockNestedLoopModel;

import org.apache.spark.api.java.JavaRDDLike;
import src.knn.model.Point;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.02.15
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
public class Partition implements Serializable {
    private List<Point> rPoints = new ArrayList<Point>();
    private List<Point> sPoints = new ArrayList<Point>();
    public void addRPoint(Point point) {
        rPoints.add(point);
    }
    public void addSPoint(Point point) {
        sPoints.add(point);
    }
    public void addAllSPoint(List<Point> points) {
        sPoints.addAll(points);
    }
    public void addAllRPoint(List<Point> points) {
        rPoints.addAll(points);
    }
    public List<Point> getrPoints() {
        return rPoints;
    }

    public List<Point> getsPoints() {
        return sPoints;
    }

}
