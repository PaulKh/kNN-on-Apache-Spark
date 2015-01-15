package src.knn.model;

import java.util.List;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.12.14
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class Point implements Serializable{
    private static int idCounter = 1;
    private int id;
    private List<Integer> coordinates;

    public Point(List<Integer> coordinates) {
        id = idCounter;
        idCounter++;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getCoordinates() {
        return coordinates;
    }
    public void printPoint(){
        System.out.println();
        System.out.print("p = ");
        for (Integer i:coordinates){
            System.out.print(i + ",");
        }
    }
    @Override
    public boolean equals(Object point){
        if (point instanceof Point){
            Point tempPoint = (Point) point;
            if (tempPoint.getCoordinates().size() != this.getCoordinates().size())
                return false;
            for (int i = 0; i < tempPoint.getCoordinates().size(); i++){
                if (tempPoint.getCoordinates().get(i) != this.getCoordinates().get(i)){
                     return false;
                }
            }
            return true;
        }
        else
            return false;
    }
}
