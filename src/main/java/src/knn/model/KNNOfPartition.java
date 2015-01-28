package src.knn.model;

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
public class KNNOfPartition implements Serializable{
    private List<KNNOfPoint> knn;

    public KNNOfPartition() {
        knn = new ArrayList<>();
    }
    public void addKNNOfPoint(KNNOfPoint knnOfPoint){
        knn.add(knnOfPoint);
    }

    public List<KNNOfPoint> getKnn() {
        return knn;
    }
    public void addAll(List<KNNOfPoint> knn){
        this.knn.addAll(knn);
    }
}
