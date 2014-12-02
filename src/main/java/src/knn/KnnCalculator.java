package src.knn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.12.14
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class KnnCalculator {
    private static int numberOfPivots = 20;
    private static int k = 10;
    public KnnCalculator() {
        SparkConf sparkConf = new SparkConf().setAppName("KNN Spark").setMaster("local[2]").set("spark.executor.memory", "3000m");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaRDD<List<Point>> sourceData = parsePointsFromSource(sc, "points_array.txt");
        sc.stop();
    }
    private JavaRDD<List<Point>> parsePointsFromSource(JavaSparkContext sc, String sourceAddress){
        return sc.textFile(sourceAddress).map(l -> l.split(" ")).map(x -> {
            List<Point> points = new ArrayList<>();
            for (String pointString: x){
                String[] coordinates = pointString.split(",");
                List<Integer> coordinatesInteger = new ArrayList<Integer>();
                for(String coordinate: coordinates){
                    coordinatesInteger.add(Integer.parseInt(coordinate));
                }
                points.add(new Point(coordinatesInteger));
            }
            return points;
        });
    }
    private JavaRDD<List<Point>> getPivots(List<Point> allPoints, int numberOfPivots){
        List<Point> pivots = new ArrayList<Point>();
        Point randomPoint = allPoints.get(PointHelper.randInt(0, allPoints.size()));
        pivots.add(randomPoint);
        for (int i = 0; i < numberOfPivots - 1; i++){
            for (int j = 0; j < allPoints.size(); j++){
                if (pivots.contains(allPoints.get(j))){

                }
            }
        }
        return sc.textFile(sourceAddress).map(l -> l.split(" ")).map(x -> {
            List<Point> points = new ArrayList<>();
            for (String pointString: x){
                System.out.println(pointString);
                String[] coordinates = pointString.split(",");
                List<Integer> coordinatesInteger = new ArrayList<Integer>();
                for(String coordinate: coordinates){
                    coordinatesInteger.add(Integer.parseInt(coordinate));
                }
                points.add(new Point(coordinatesInteger));
            }
            return points;
        });
    }
}
