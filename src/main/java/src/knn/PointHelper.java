package src.knn;

import src.knn.model.Point;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.12.14
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class PointHelper {
    public static int numberOfDistanceComputations = 0;
    private static PointHelper pointHelper;

    public synchronized static PointHelper instance() {
         if (pointHelper == null){
             pointHelper = new PointHelper();
         }
        return pointHelper;
    }

    public int randInt(int min, int max) {
        int randomNum = new Random().nextInt((max - min) + 1) + min;
        return randomNum;
    }
    public String generateArrayOfRandomPoints(int dimension, int maximum, int minimum, int numberOfPoints){
        String points = "";
        List<Point> pointList = new ArrayList<Point>();
        for (int i = 0; i < numberOfPoints; i++){
            List<Integer> coordinates = new ArrayList<Integer>();
            for (int j = 0; j < dimension; j++){
                coordinates.add(randInt(minimum, maximum));
            }
            Point point = new Point(coordinates);
            if (isListfPointsContainPoint(pointList, point)){
                i--;
            }
            else{
                for (int j = 0; j < dimension; j++){
                    points = points + coordinates.get(j);
                    if (j != dimension - 1)
                        points += ",";
                }
                points += " ";
            }

        }
        return points;
    }
    public double getDistanceBetweenPoints(Point p1, Point p2){
        numberOfDistanceComputations++;
        if (p1.getCoordinates().size() != p2.getCoordinates().size())
            return 0;
        else if(p1.getCoordinates().size() == 1)
            return Math.abs(p1.getCoordinates().get(0) - p2.getCoordinates().get(0));
        else{
            long squaresSummation = 0;
            for (int i = 0; i < p1.getCoordinates().size(); i++){
                squaresSummation += Math.pow(p1.getCoordinates().get(i) - p2.getCoordinates().get(i), 2);
            }
            return Math.sqrt(squaresSummation);
        }
    }
    public double getSummationDistanceBetweenPointAndArray(Point point, List<Point> points){
        double distance = 0;
        for (Point p:points){
            distance += getDistanceBetweenPoints(point, p);
        }
        return distance;
    }
    public <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }
    public boolean isListfPointsContainPoint(List<Point> points, Point point){
        for (Point p:points){
            if (p.equals(point)){
                return true;
            }
        }
        return false;
    }
}
