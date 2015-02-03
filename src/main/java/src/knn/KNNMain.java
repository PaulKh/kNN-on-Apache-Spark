package src.knn;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 25.11.14
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */


public class KNNMain {
    public static void main(String[] args){
//        String temp = PointHelper.instance().generateArrayOfRandomPoints(2, 1000000, 1, 1000);
//        VoronoiKnnCalculator voronoiKnnCalculator = new VoronoiKnnCalculator();
        BlockNestedLoopKnnCalculator blockNestedLoopKnnCalculator = new BlockNestedLoopKnnCalculator();
        return;
    }
}
