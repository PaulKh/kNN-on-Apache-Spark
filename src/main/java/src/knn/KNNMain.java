package src.knn;

import src.knn.utilities.GeographicLocationSaxParser;

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
        VoronoiKnnCalculator voronoiKnnCalculator = new VoronoiKnnCalculator();
//          testVoronoi();
//        BlockNestedLoopKnnCalculator blockNestedLoopKnnCalculator = new BlockNestedLoopKnnCalculator();
//        GeographicLocationSaxParser.saxParser();
        return;
    }
    private static void testVoronoi(){
        int numberOfTests = 1;
        double times[] = new double[numberOfTests];
        double numberOfComputations[] = new double[numberOfTests];
        for (int i = 1; i <= numberOfTests; i++) {
            long time = System.currentTimeMillis();
            SharedMemory.numberOfPivots = 50;
            VoronoiKnnCalculator voronoiKnnCalculator = new VoronoiKnnCalculator();
            times[i-1] = System.currentTimeMillis() - time;
            numberOfComputations[i-1] = PointHelper.numberOfDistanceComputations;
        }
        for (int i = 0; i < numberOfTests; i++){
            System.out.println(times[i] + " " + numberOfComputations[i]);
        }
    }
}
