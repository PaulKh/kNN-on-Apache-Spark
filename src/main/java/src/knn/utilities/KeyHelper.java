package src.knn.utilities;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 01.02.15
 * Time: 19:39
 * To change this template use File | Settings | File Templates.
 */
public class KeyHelper {
    public static int generateKey(int numberOfPartitions, int rKey, int sKey){
        return rKey * numberOfPartitions + sKey;
    }
    public static int getRKey(int numberOfPartitions, int key){
        return key/numberOfPartitions;
    }
    public static int getSKey(int numberOfPartitions, int key){
        return key%numberOfPartitions;
    }
}
