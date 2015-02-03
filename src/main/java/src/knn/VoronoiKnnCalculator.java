package src.knn;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import src.knn.model.*;
import src.knn.utilities.LimitedSizeQueue;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.12.14
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class VoronoiKnnCalculator implements Serializable{
    private static final String rArraySource = "src/main/resources/r_points_two_dimension_array.txt";
    private static final String sArraySource = "src/main/resources/s_points_two_dimension_array.txt";

    public VoronoiKnnCalculator() {
        SparkConf sparkConf = new SparkConf().setAppName("KNN Spark").setMaster("local[2]").set("spark.executor.memory", "3000m");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        List<Point> sourceRPoints = parsePointsFromSource(rArraySource);
        List<Point> sourceSPoints = parsePointsFromSource(sArraySource);
        initPivotsByRandomSelection(sourceRPoints, 10);
        JavaRDD<Point> sourceRPointsRDD = sc.parallelize(sourceRPoints);
        JavaRDD<Point> sourceSPointsRDD = sc.parallelize(sourceSPoints);
        JavaPairRDD<Integer, FirstPointDistributionEntity> pairsR = findRPointsAssignedToPivots(sourceRPointsRDD);
        JavaPairRDD<Integer, FirstPointDistributionEntity> pairsS = findSPointsAssignedToPivots(sourceSPointsRDD);
        JavaPairRDD<Integer, Iterable<FirstPointDistributionEntity>> pivotPoints = pairsR.union(pairsS).groupByKey();
        JavaRDD<FirstPointDistributionEntity> groupedPivots = groupPivotPointsAndCollectStatistics(pivotPoints);

        JavaRDD<BoundingEntity> boundingEntityJavaRDD = calculateBounds(groupedPivots);

        JavaRDD<FinalPartition> finalPartitions = reduceAndRepartition(sc, assignPointsAccordingToBounds(boundingEntityJavaRDD));
        KNNOfPartition knn = collectFinalResult(calculatingKNN(finalPartitions));

//        System.out.println("sum = " + sum);
//        JavaRDD<PivotPoint> pivotPoints = findRPointsAssignedToPivots(sourceRPointsRDD).union(findSPointsAssignedToPivots(sourceSPointsRDD));
//        pivotPoints.groupBy()
//        FirstPointDistributionEntity pivotPointsAfterReduce = groupedPivots.reduce((pivotPoint1, pivotPoint2) -> {
//                System.out.println(pivotPoint1.getPivot().getId() + " " + pivotPoint2.getPivot().getId());
//                return pivotPoint1;
//            }
//        );
//        JavaRDD<List<Point>> pivots = getPivots(sourceData, numberOfPivots);
//        pivots.reduce((p1, p2) -> PointHelper.instance().union(p1, p2));
        sc.stop();
    }
    private KNNOfPartition collectFinalResult(JavaRDD<KNNOfPartition> knn){
        return knn.reduce((partition1, partition2) ->{
            partition1.addAll(partition2.getKnn());
            return partition1;
        });
    }
    private JavaRDD<KNNOfPartition> calculatingKNN(JavaRDD<FinalPartition> finalPartitions){
        return finalPartitions.map(finalPartition -> {
            KNNOfPartition knnOfPartition = new KNNOfPartition();
            for (int i = 0; i < finalPartition.getPointsS().size(); i++){
                int currentMinimum = i;
                for (int j = i; j < finalPartition.getPointsS().size(); j++){
                    if (SharedMemory.distancesBetweenPivots[finalPartition.getPointsS().get(j).getPivotPointId()][finalPartition.getPivotPointId()]
                            < SharedMemory.distancesBetweenPivots[finalPartition.getPointsS().get(currentMinimum).getPivotPointId()][finalPartition.getPivotPointId()]){
                         currentMinimum = j;
                    }
                }
                Collections.swap(finalPartition.getPointsS(), i, currentMinimum);
            }
            for (Point rPoint: finalPartition.getPointsR()){
                double teta = SharedMemory.maximumUpperBounds[finalPartition.getPivotPointId()].getYongest();
                PointWithDistance farthestPoint = null;
                KNNOfPoint knnOfPoint = new KNNOfPoint(rPoint);
                for (int i = 0; i < finalPartition.getPointsS().size(); i++){
                    List<Point> sPoints = finalPartition.getPointsS().get(i).getsPoints();
                    for (int j = 0; j < sPoints.size(); j++){
                        double distance = PointHelper.instance().getDistanceBetweenPoints(sPoints.get(j), rPoint);
                         if(distance < teta){
                             knnOfPoint.addSPoint(new PointWithDistance(sPoints.get(j), distance));
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
                        else if(distance == teta && knnOfPoint.numberOfSPointsAdded() < SharedMemory.k){
                             knnOfPoint.addSPoint(new PointWithDistance(sPoints.get(j), distance));
                        }
                    }
                }
                knnOfPartition.addKNNOfPoint(knnOfPoint);
            }
            return knnOfPartition;
        });
    }

    private JavaRDD<FinalPartition> reduceAndRepartition(JavaSparkContext sc, JavaRDD<FinalPartition[]> finalPartitions){
        FinalPartition[] reducedData = finalPartitions.reduce((partition1, partition2) -> {
            for (int i = 0; i < partition1.length; i++){
                if (partition2[i].getPointsR().size() > 0){
                    partition1[i].setPointsR(partition2[i].getPointsR());
                    partition1[i].setPivotPointId(partition2[i].getPivotPointId());
                }
                partition1[i].addPartitionsOfS(partition2[i].getPointsS());
            }
            return partition1;
        });

        for (int i = 0; i < reducedData.length; i++){
            int sum = 0;
            for (PartitionOfSAssignedToPivot temp:reducedData[i].getPointsS()){
                sum += temp.getsPoints().size();
            }
            System.out.println("sum[" + i + "] = " + sum + "Rs.count = " + reducedData[i].getPointsR().size() + " numberOfComputations = " + reducedData[i].getPointsR().size() * sum);
        }
        return sc.parallelize(Arrays.asList(reducedData));
    }

    private JavaRDD<FinalPartition[]> assignPointsAccordingToBounds(JavaRDD<BoundingEntity> boundingEntityJavaRDD){
        return boundingEntityJavaRDD.map(boundingEntity -> {
            FinalPartition[] partitions = new FinalPartition[SharedMemory.numberOfPivots];
            for (int i = 0; i < partitions.length; i++){
                partitions[i] = new FinalPartition();
            }
            partitions[boundingEntity.getPivotPointId()].setPivotPointId(boundingEntity.getPivotPointId());
            partitions[boundingEntity.getPivotPointId()].setPointsR(boundingEntity.getrPointsAssignedToPivot());
            PartitionOfSAssignedToPivot[] partitionOfSAssignedToPivots = new PartitionOfSAssignedToPivot[SharedMemory.numberOfPivots];
            for (int i = 0; i < partitionOfSAssignedToPivots.length; i++){
                partitionOfSAssignedToPivots[i] = new PartitionOfSAssignedToPivot(boundingEntity.getPivotPointId());
            }
            for (PointWithLowerBounds pointWithLowerBounds: boundingEntity.getPointsWithLowerBounds()){
                Point pointS = pointWithLowerBounds.getP();
                for (int i = 0; i < SharedMemory.numberOfPivots; i++){
                    if (SharedMemory.maximumUpperBounds[i].getYongest() > pointWithLowerBounds.getLowerBounds()[i]){
                        partitionOfSAssignedToPivots[i].addPoint(pointS);
                    }
                }
            }
            for (int i = 0; i < partitionOfSAssignedToPivots.length; i++){
                if (partitionOfSAssignedToPivots[i].getsPoints().size() > 0)
                    partitions[i].addPartitionOfS(partitionOfSAssignedToPivots[i]);
            }
            return partitions;
        });
    }
    private JavaRDD<BoundingEntity> calculateBounds(JavaRDD<FirstPointDistributionEntity> groupedPivots){
        return groupedPivots.map(pivot -> {
            BoundingEntity boundingEntity = new BoundingEntity();
            boundingEntity.setrPointsAssignedToPivot(pivot.getrPointsAssignedToPivot());
            int amountOfPoints = Math.min(SharedMemory.k, pivot.getsPointsAssignedToPivot().size());
            for(int i = 0; i < amountOfPoints; i++){
                double dist = PointHelper.instance().getDistanceBetweenPoints(SharedMemory.pivots[pivot.getPivotPointId()].getPivot(), pivot.getsPointsAssignedToPivot().get(i).getPoint());
                for (int j = 0; j < SharedMemory.numberOfPivots; j++){
                    double upperBound = SharedMemory.distancesBetweenPivots[pivot.getPivotPointId()][j] + SharedMemory.pivots[j].getMaxDistanceR() + dist;
                    if (SharedMemory.maximumUpperBounds[j].size() < SharedMemory.k){
                        SharedMemory.maximumUpperBounds[j].add(upperBound);
                    }
                    else if(SharedMemory.maximumUpperBounds[j].getYongest() > upperBound){
                        SharedMemory.maximumUpperBounds[j].add(upperBound);
                    }
                }
            }
            for (int i = 0; i < pivot.getsPointsAssignedToPivot().size(); i++){
                PointWithLowerBounds pointWithLowerBounds = new PointWithLowerBounds(pivot.getsPointsAssignedToPivot().get(i).getPoint());
                double dist = PointHelper.instance().getDistanceBetweenPoints(SharedMemory.pivots[pivot.getPivotPointId()].getPivot(), pivot.getsPointsAssignedToPivot().get(i).getPoint());
                for (int j = 0; j < SharedMemory.numberOfPivots; j++){
                    double lowerBound = Math.max(0, SharedMemory.distancesBetweenPivots[pivot.getPivotPointId()][j] - SharedMemory.pivots[j].getMaxDistanceR() - dist);
                    pointWithLowerBounds.setLowerBound(j, lowerBound);
                }
                boundingEntity.addPointWithLowerBounds(pointWithLowerBounds);
            }
            boundingEntity.setPivotPointId(pivot.getPivotPointId());
            return boundingEntity;
        });
    }
    private JavaRDD<FirstPointDistributionEntity> groupPivotPointsAndCollectStatistics(JavaPairRDD<Integer, Iterable<FirstPointDistributionEntity>> pivotPoints){
        return pivotPoints.map(tuple -> {
            FirstPointDistributionEntity pivotPoint = new FirstPointDistributionEntity();
            double maxDistanceR = 0;
            double maxDistanceS = 0;
            for (FirstPointDistributionEntity p:tuple._2){
                pivotPoint.setPivotPointId(p.getPivotPointId());
                pivotPoint.setrPointsAssignedToPivot(PointHelper.instance().union(pivotPoint.getrPointsAssignedToPivot(), p.getrPointsAssignedToPivot()));
                pivotPoint.setsPointsAssignedToPivot(PointHelper.instance().union(pivotPoint.getsPointsAssignedToPivot(), p.getsPointsAssignedToPivot()));
            }
            for (PointWithDistance pointWithDistance:pivotPoint.getrPointsAssignedToPivot()){
                if (pointWithDistance.getDistance() > maxDistanceR){
                    maxDistanceR = pointWithDistance.getDistance();
                }
            }
            for (PointWithDistance pointWithDistance:pivotPoint.getsPointsAssignedToPivot()){
                if (pointWithDistance.getDistance() > maxDistanceS){
                    maxDistanceS = pointWithDistance.getDistance();
                }
            }

            pivotPoint.getPivotObject().setMaxDistanceR(maxDistanceR);
            pivotPoint.getPivotObject().setMaxDistanceS(maxDistanceS);
            calculateKNNInSForPivot(pivotPoint.getsPointsAssignedToPivot());
            return pivotPoint;
        });
    }
    private List<PointWithDistance> calculateKNNInSForPivot(List<PointWithDistance> pointsWithDistance){
        int numberOfLoops = Math.min(pointsWithDistance.size(), SharedMemory.k);
        for(int i = 0; i < numberOfLoops; i++){
            int tempNearestPointIndex = i;
            for (int j = i; j < pointsWithDistance.size() - 1; j++){
                if (pointsWithDistance.get(j).getDistance() < pointsWithDistance.get(tempNearestPointIndex).getDistance())
                    tempNearestPointIndex = j;
            }
            Collections.swap(pointsWithDistance, i, tempNearestPointIndex);
        }
        return pointsWithDistance;
    }
    //Decide to which pivot point we should add each point from S
    private JavaPairRDD<Integer, FirstPointDistributionEntity> findSPointsAssignedToPivots(JavaRDD<Point> sourceSPointsRDD){
        return sourceSPointsRDD.mapToPair(keyValueSSet);
    }

    PairFunction<Point, Integer, FirstPointDistributionEntity> keyValueSSet =
            new PairFunction<Point, Integer, FirstPointDistributionEntity>() {
                public Tuple2<Integer, FirstPointDistributionEntity> call(Point point) throws Exception {
                    FirstPointDistributionEntity firstPointAttachementEntity = new FirstPointDistributionEntity();
                    double tempDistanceToPivot = 10000000;
                    int tempPivotId = -1;
                    for (int i = 0; i < SharedMemory.pivots.length; i++){
                        PivotPoint pivot = SharedMemory.pivots[i];
                        double dist = PointHelper.instance().getDistanceBetweenPoints(pivot.getPivot(), point);
                        if (dist < tempDistanceToPivot){
                            tempDistanceToPivot = dist;
                            tempPivotId = i;
                        }
                    }
                    if (tempPivotId != -1){
                         firstPointAttachementEntity.setPivotPointId(tempPivotId);
                         firstPointAttachementEntity.addPointToS(new PointWithDistance(point, tempDistanceToPivot));
                        return new Tuple2(tempPivotId, firstPointAttachementEntity);
                    }
                    return null;
                }
            };
    //Decide to which pivot point we should add each point from R
    private JavaPairRDD<Integer, FirstPointDistributionEntity> findRPointsAssignedToPivots(JavaRDD<Point> sourceRPointsRDD){
        return sourceRPointsRDD.mapToPair(keyValueRSet);
    }

    PairFunction<Point, Integer, FirstPointDistributionEntity> keyValueRSet =
            new PairFunction<Point, Integer, FirstPointDistributionEntity>() {
                public Tuple2<Integer, FirstPointDistributionEntity> call(Point point) throws Exception {
                    FirstPointDistributionEntity pivotPoint = new FirstPointDistributionEntity();
                    int tempPivotId = -1;
                    double tempPivotValue = 10000000;
                    for (int i = 0; i < SharedMemory.pivots.length; i++){
                        PivotPoint pivot = SharedMemory.pivots[i];
                        double dist = PointHelper.instance().getDistanceBetweenPoints(pivot.getPivot(), point);
                        if (dist < tempPivotValue){
                            tempPivotValue = dist;
                            tempPivotId = i;
                        }
                    }
                    if (tempPivotId != -1){
                        pivotPoint.setPivotPointId(tempPivotId);
                        pivotPoint.addPointToR(new PointWithDistance(point, tempPivotValue));
                        return new Tuple2(tempPivotId, pivotPoint);
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
    private List<Point> getPivotsFarthestSelection(List<Point> allPoints){      //farthest partitioning method, doesn't work
        List<Point> pivots = new ArrayList<Point>();
        Point randomPoint = allPoints.get(PointHelper.instance().randInt(0, allPoints.size()));
        pivots.add(randomPoint);
        randomPoint.printPoint();
        for (int i = 0; i < SharedMemory.numberOfPivots - 1; i++){
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
    private void initPivotsByRandomSelection(List<Point> allPoints, int numberOfTries){
        List<Point> pivotPoints = new ArrayList<Point>();
        double maxDistance = 0;
        for (int i = 0; i < numberOfTries; i++){
            List<Point> tempPoints = new ArrayList<Point>();
            for (int j = 0; j < SharedMemory.numberOfPivots; j++){
                int k = PointHelper.instance().randInt(0, allPoints.size() - 1);
                if (tempPoints.contains(allPoints.get(k)))
                    j--;
                else
                    tempPoints.add(allPoints.get(k));
            }
            double tempDistance = getSummationOfDistancesBetweenAllPoints(tempPoints);
            if (tempDistance > maxDistance){
                maxDistance = tempDistance;
                pivotPoints = tempPoints;
            }
        }
        initDistancesBetweenPoints(pivotPoints);
        SharedMemory.initPoints(pivotPoints);
    }
    private void initDistancesBetweenPoints(List<Point> points){
        SharedMemory.distancesBetweenPivots = new double[points.size()][points.size()];
        for (int i = 0; i < points.size(); i++){
            for (int j = 0; j < i; j++){
                SharedMemory.distancesBetweenPivots[i][j] = PointHelper.instance().getDistanceBetweenPoints(points.get(i), points.get(j));
                SharedMemory.distancesBetweenPivots[j][i] = SharedMemory.distancesBetweenPivots[i][j];
            }
        }
    }
    private double getSummationOfDistancesBetweenAllPoints(List<Point> points){
        double distance = 0;
        for (int i = 0; i < points.size(); i++){
            for (int j = 0; j < points.size(); j++){
                distance += PointHelper.instance().getDistanceBetweenPoints(points.get(i), points.get(j));
            }
        }
        return distance;
    }

}
