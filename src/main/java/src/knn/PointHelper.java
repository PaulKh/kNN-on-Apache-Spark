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
        for (int i = 0; i < numberOfPoints; i++){
            for (int j = 0; j < dimension; j++){
                points = points + randInt(minimum, maximum);
                if (j != dimension - 1)
                    points += ",";
            }
            points += " ";
        }
        return points;
    }
    public double getDistanceBetweenPoints(Point p1, Point p2){
        if (p1.getCoordinates().size() != p2.getCoordinates().size())
            return 0;
        else if(p1.getCoordinates().size() == 1)
            return Math.abs(p1.getCoordinates().get(0) - p2.getCoordinates().get(0));
        else{
            int squaresSummation = 0;
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
    public List<Point> union(List<Point> list1, List<Point> list2) {
        Set<Point> set = new HashSet<Point>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<Point>(set);
    }
}