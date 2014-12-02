package src.knn;

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
    private List<Integer> coordinates;

    public Point(List<Integer> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Integer> getCoordinates() {
        return coordinates;
    }
}
