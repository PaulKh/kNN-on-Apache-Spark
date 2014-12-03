package src.knn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import src.knn.model.PivotPoint;
import src.knn.model.Point;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.12.14
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class KnnCalculator implements Serializable{
    private static final int numberOfPivots = 20;
    private static final int k = 10;
    private static final String rArraySource = "r_points_two_dimension_array.txt";
    private static final String sArraySource = "s_points_two_dimension_array.txt";
    private List<Point> pivots;

    public KnnCalculator() {
        SparkConf sparkConf = new SparkConf().setAppName("KNN Spark").setMaster("local[2]").set("spark.executor.memory", "3000m");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        List<Point> sourceRPoints = parsePointsFromSource(rArraySource);
        List<Point> sourceSPoints = parsePointsFromSource(sArraySource);
        pivots = getPivots(sourceRPoints);
        JavaRDD<Point> sourceRPointsRDD = sc.parallelize(sourceRPoints);
        JavaRDD<Point> sourceSPointsRDD = sc.parallelize(sourceSPoints);
        JavaPairRDD<Integer, PivotPoint> pairsR = findRPointsAssignedToPivots(sourceRPointsRDD);
        JavaPairRDD<Integer, PivotPoint> pairsS = findSPointsAssignedToPivots(sourceSPointsRDD);
        JavaPairRDD<Integer, Iterable<PivotPoint>> pivotPoints = pairsR.union(pairsS).groupByKey();
        JavaRDD<PivotPoint> groupedPivots = pivotPoints.map(tuple -> {
            PivotPoint pivotPoint = new PivotPoint();
            for (PivotPoint p:tuple._2){
                pivotPoint.setPivot(p.getPivot());
                pivotPoint.setrPointsAssignedToPivot(PointHelper.instance().union(pivotPoint.getrPointsAssignedToPivot(), p.getrPointsAssignedToPivot()));
                pivotPoint.setsPointsAssignedToPivot(PointHelper.instance().union(pivotPoint.getsPointsAssignedToPivot(), p.getsPointsAssignedToPivot()));
            }
            return pivotPoint;
        });
        List<PivotPoint> pivotPointList = groupedPivots.collect();
//        JavaRDD<PivotPoint> pivotPoints = findRPointsAssignedToPivots(sourceRPointsRDD).union(findSPointsAssignedToPivots(sourceSPointsRDD));
//        pivotPoints.groupBy()
//        PivotPoint pivotPointsAfterReduce = pivotPoints.reduce((pivotPoint1, pivotPoint2) -> {
//            if(pivotPoint1.getPivot() == pivotPoint2.getPivot()){
//                pivotPoint1.setrPointsAssignedToPivot(PointHelper.instance().union(pivotPoint1.getrPointsAssignedToPivot(), pivotPoint2.getrPointsAssignedToPivot()));
//                pivotPoint1.setsPointsAssignedToPivot(PointHelper.instance().union(pivotPoint1.getsPointsAssignedToPivot(), pivotPoint2.getsPointsAssignedToPivot()));
//                return pivotPoint1;
//            }
//        });
//        JavaRDD<List<Point>> pivots = getPivots(sourceData, numberOfPivots);
//        pivots.reduce((p1, p2) -> PointHelper.instance().union(p1, p2));
        sc.stop();
    }
    private JavaPairRDD<Integer, PivotPoint> findRPointsAssignedToPivots(JavaRDD<Point> sourceRPointsRDD){
        return sourceRPointsRDD.mapToPair(keyValueRSet);
    }

    //Decide to which pivot point we should add each point from S
    private JavaPairRDD<Integer, PivotPoint> findSPointsAssignedToPivots(JavaRDD<Point> sourceSPointsRDD){
        return sourceSPointsRDD.mapToPair(keyValueSSet);
    }

    PairFunction<Point, Integer, PivotPoint> keyValueSSet =
            new PairFunction<Point, Integer, PivotPoint>() {
                public Tuple2<Integer, PivotPoint> call(Point point) throws Exception {
                    PivotPoint pivotPoint = new PivotPoint();
                    Point tempPivot = null;
                    double tempPivotValue = 10000000;
                    for (Point pivot:pivots){
                        double dist = PointHelper.instance().getDistanceBetweenPoints(pivot, point);
                        if (dist < tempPivotValue){
                            tempPivotValue = dist;
                            tempPivot = pivot;
                        }
                    }
                    if (tempPivot != null){
                        pivotPoint.setPivot(tempPivot);
                        pivotPoint.addPointToS(point);
                        return new Tuple2(pivotPoint.getPivot().getId(), pivotPoint);
                    }
                    return null;
                }
            };
    //Decide to which pivot point we should add each point from R
    PairFunction<Point, Integer, PivotPoint> keyValueRSet =
            new PairFunction<Point, Integer, PivotPoint>() {
                public Tuple2<Integer, PivotPoint> call(Point point) throws Exception {
                    PivotPoint pivotPoint = new PivotPoint();
                    Point tempPivot = null;
                    double tempPivotValue = 10000000;
                    for (Point pivot:pivots){
                        double dist = PointHelper.instance().getDistanceBetweenPoints(pivot, point);
                        if (dist < tempPivotValue){
                            tempPivotValue = dist;
                            tempPivot = pivot;
                        }
                    }
                    if (tempPivot != null){
                        pivotPoint.setPivot(tempPivot);
                        pivotPoint.addPointToR(point);
                        return new Tuple2(pivotPoint.getPivot().getId(), pivotPoint);
                    }
                    return null;
                }
            };


    private List<Point> parsePointsFromSource(String sourceAddress){
        String sourceData = null;
        try {
            sourceData = readFile(sourceAddress, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (sourceData == null){
            return null;
        }
        String pointsStringArray[] = sourceData.split(" ");
        List<Point> points = new ArrayList<>();

        for (String pointString: pointsStringArray){
            String[] coordinates = pointString.split(",");
            List<Integer> coordinatesInteger = new ArrayList<Integer>();
            for(String coordinate: coordinates){
                coordinatesInteger.add(Integer.parseInt(coordinate));
            }
            points.add(new Point(coordinatesInteger));
        }
        return points;
    }
    private String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    private List<Point> getPivots(List<Point> allPoints){      //farthest partitioning method, doesn't work
        List<Point> pivots = new ArrayList<Point>();
        Point randomPoint = allPoints.get(PointHelper.instance().randInt(0, allPoints.size()));
        pivots.add(randomPoint);
        randomPoint.printPoint();
        for (int i = 0; i < numberOfPivots - 1; i++){
            Point tempMaximumPoint = null;
            double tempMaximumValue = 0;

            for (int j = 0; j < allPoints.size(); j++){
                if (pivots.contains(allPoints.get(j))){
                    continue;
                }
                double distance = PointHelper.instance().getSummationDistanceBetweenPointAndArray(allPoints.get(j), pivots);
                if (distance > tempMaximumValue){
                    tempMaximumPoint = allPoints.get(j);
                    tempMaximumValue = distance;
                }
            }
            if (tempMaximumPoint != null){
                pivots.add(tempMaximumPoint);
                tempMaximumPoint.printPoint();
            }
        }
        return pivots;
    }

}
