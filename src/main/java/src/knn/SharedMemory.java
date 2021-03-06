package src.knn;

import src.knn.model.PivotPoint;
import src.knn.model.Point;
import src.knn.utilities.LimitedSizeQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 21.01.15
 * Time: 19:25
 * To change this template use File | Settings | File Templates.
 */
public class SharedMemory {
    public static PivotPoint pivots[];
    public static int numberOfPivots = 50;
    public static int k = 50;
    public static LimitedSizeQueue<Double> maximumUpperBounds[];
    public static double distancesBetweenPivots[][];
    public static void initPoints(List<Point> points){
        if (points.size() != numberOfPivots){
            System.out.println("Wrong number of pivots");
            return;
        }
        pivots = new PivotPoint[points.size()];
        for (int i = 0; i < points.size(); i++){
            Point point = points.get(i);
            PivotPoint pivotObject = new PivotPoint();
            pivotObject.setPivot(point);
            pivots[i] = pivotObject;
        }
        maximumUpperBounds = new LimitedSizeQueue[numberOfPivots];
        for (int i = 0; i < numberOfPivots; i++){
            maximumUpperBounds[i] = new LimitedSizeQueue<Double>(k);
        }
    }
    public static void setUpperBound(int index, double value){
        if (SharedMemory.maximumUpperBounds[index].size() < SharedMemory.k){
            SharedMemory.maximumUpperBounds[index].add(value);
        }
        else if(SharedMemory.maximumUpperBounds[index].getYongest() > value){
            SharedMemory.maximumUpperBounds[index].add(value);
        }
    }
}
