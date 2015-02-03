package src.knn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import src.knn.model.KNNOfPoint;
import src.knn.model.Point;
import src.knn.model.PointWithDistance;
import src.knn.model.blockNestedLoopModel.DistanceComputationEntity;
import src.knn.model.blockNestedLoopModel.Partition;
import src.knn.utilities.KeyHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.02.15
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class BlockNestedLoopKnnCalculator implements Serializable{
    private static final int numberOfPartitions = 20;
    private static final String rArraySource = "src/main/resources/r_points_small_two_dimension.txt";
    private static final String sArraySource = "src/main/resources/s_points_small_two_dimension.txt";

    public BlockNestedLoopKnnCalculator() {
        SparkConf sparkConf = new SparkConf().setAppName("KNN Spark").setMaster("local[2]").set("spark.executor.memory", "3000m");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaRDD<Point> sourceRPointsRDD = getPoints(sc.textFile(rArraySource).flatMap(s -> Arrays.asList(s.split(" "))));
        JavaRDD<Point> sourceSPointsRDD = getPoints(sc.textFile(sArraySource).flatMap(s -> Arrays.asList(s.split(" "))));
        JavaPairRDD<Integer, Partition> groupedRPoints = parseRPointsToPartition(assignRPointToKey(sourceRPointsRDD).groupByKey());
        JavaPairRDD<Integer, Partition> groupedSPoints = parseSPointsToPartition(assignSPointToKey(sourceSPointsRDD).groupByKey());
        JavaRDD<Partition> groupedPartition = firstGrouping(groupedRPoints.union(groupedSPoints).groupByKey());
        JavaPairRDD<Integer, Iterable<DistanceComputationEntity>> rPointsWithAllTheDistances = distanceComputation(groupedPartition).groupByKey();
        List<KNNOfPoint> distances = knnComputation(rPointsWithAllTheDistances).collect();
//        List<Tuple2<Integer, Iterable<DistanceComputationEntity>>> partitions = rPointsWithAllTheDistances.collect();
        sc.stop();
    }
    private JavaRDD<KNNOfPoint> knnComputation(JavaPairRDD<Integer, Iterable<DistanceComputationEntity>> rPointsWithAllTheDistances){
        return rPointsWithAllTheDistances.map(tuple -> {
            Point rPoint = null;
            for (DistanceComputationEntity tempDistanceComputationEntity:tuple._2){
                rPoint = tempDistanceComputationEntity.getrPoint();
            }
            KNNOfPoint knnOfPoint = new KNNOfPoint(rPoint);
            double teta = Double.MAX_VALUE;
            PointWithDistance farthestPoint = null;
            for (DistanceComputationEntity tempDistanceComputationEntity:tuple._2){
                for (PointWithDistance pointWithDistance:tempDistanceComputationEntity.getsPoints()){
                    if(pointWithDistance.getDistance() < teta){
                        knnOfPoint.addSPoint(pointWithDistance);
                        if (knnOfPoint.numberOfSPointsAdded() == SharedMemory.k){
                            farthestPoint = knnOfPoint.getFarthestPoint();
                            teta = farthestPoint.getDistance();
                        }
                        else if (knnOfPoint.numberOfSPointsAdded() > SharedMemory.k){
                            knnOfPoint.removePoint(farthestPoint);
                            farthestPoint = knnOfPoint.getFarthestPoint();
                            teta = farthestPoint.getDistance();
                        }
                    }
                    else if(pointWithDistance.getDistance() == teta && knnOfPoint.numberOfSPointsAdded() < SharedMemory.k){
                        knnOfPoint.addSPoint(pointWithDistance);
                    }
                }
            }
            return knnOfPoint;
        });
    }
    private JavaPairRDD<Integer, DistanceComputationEntity> distanceComputation(JavaRDD<Partition> groupedPartition){
        return groupedPartition.flatMapToPair(new PairFlatMapFunction<Partition, Integer, DistanceComputationEntity>(){

            @Override
            public Iterable<Tuple2<Integer, DistanceComputationEntity>> call(Partition partition) throws Exception {
                List<Tuple2<Integer, DistanceComputationEntity>> computedDistances = new ArrayList<>();
                for (Point rPoint:partition.getrPoints()){
                    DistanceComputationEntity distanceComputationEntity = new DistanceComputationEntity(rPoint);
                    for (Point sPoint:partition.getsPoints()){
                        distanceComputationEntity.addSPoint(new PointWithDistance(sPoint, PointHelper.instance().getDistanceBetweenPoints(sPoint, rPoint)));
                    }
                    computedDistances.add(new Tuple2<Integer, DistanceComputationEntity>(rPoint.getId(), distanceComputationEntity));
                }
                return computedDistances;
            }
        });
    }
    private JavaRDD<Partition> firstGrouping(JavaPairRDD<Integer, Iterable<Partition>> partitionWithKeys){
        return partitionWithKeys.map(tuple ->{
             Partition partition = new Partition();
            for (Partition tempPartition:tuple._2){
                partition.addAllRPoint(tempPartition.getrPoints());
                partition.addAllSPoint(tempPartition.getsPoints());
            }
            return partition;
        });
    }
    private JavaPairRDD<Integer, Partition> parseRPointsToPartition(JavaPairRDD<Integer, Iterable<Point>> groupedPoints){
        return groupedPoints.mapToPair(tuple -> {
            Partition partition = new Partition();
            for (Point point : tuple._2) {
                partition.addRPoint(point);
            }
            return new Tuple2<Integer, Partition> (tuple._1, partition);
        });
    }
    private JavaPairRDD<Integer, Partition> parseSPointsToPartition(JavaPairRDD<Integer, Iterable<Point>> groupedPoints){
        return groupedPoints.mapToPair(tuple -> {
            Partition partition = new Partition();
            for (Point point : tuple._2) {
                partition.addSPoint(point);
            }
            return new Tuple2<Integer, Partition> (tuple._1, partition);
        });
    }
    private JavaPairRDD<Integer, Point> assignRPointToKey(JavaRDD<Point> points){
        return points.flatMapToPair(new PairFlatMapFunction<Point, Integer, Point>() {
            @Override
            public Iterable<Tuple2<Integer, Point>> call(Point point) throws Exception {
                List<Tuple2<Integer, Point>> tuples = new ArrayList<>();
                for (int i = 0; i < numberOfPartitions; i++) {
                    tuples.add(new Tuple2<Integer, Point>(KeyHelper.generateKey(numberOfPartitions, point.getId() % numberOfPartitions, i), point));
                }
                return tuples;
            }
        });
    }
    private JavaPairRDD<Integer, Point> assignSPointToKey(JavaRDD<Point> points){
        return points.flatMapToPair(new PairFlatMapFunction<Point, Integer, Point>() {
            @Override
            public Iterable<Tuple2<Integer, Point>> call(Point point) throws Exception {
                List<Tuple2<Integer, Point>> tuples = new ArrayList<>();
                for (int i = 0; i < numberOfPartitions; i++){
                    tuples.add(new Tuple2<Integer, Point>(KeyHelper.generateKey(numberOfPartitions, i, point.getId() % numberOfPartitions), point));
                }
                return tuples;
            }
        });
    }
    private JavaRDD<Point> getPoints(JavaRDD<String> points){
        return points.map(pointString -> {
            String[] coordinates = pointString.split(",");
            List<Integer> coordinatesInteger = new ArrayList<Integer>();
            for(String coordinate: coordinates){
                coordinatesInteger.add(Integer.parseInt(coordinate));
            }
            return new Point(coordinatesInteger);
        });
    }
}
