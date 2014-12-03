package src.knn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 25.11.14
 * Time: 22:34
 * To change this template use File | Settings | File Templates.
 */


public class KNNMain {
    public static void main(String[] args){
//        String temp = PointHelper.instance().generateArrayOfRandomPoints(2, 30000, 1, 1000);
        KnnCalculator knnCalculator = new KnnCalculator();
        return;
     }
}
