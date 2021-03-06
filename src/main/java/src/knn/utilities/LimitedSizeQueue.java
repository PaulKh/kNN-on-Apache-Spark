package src.knn.utilities;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Paul
 * Date: 25.01.15
 * Time: 19:16
 * To change this template use File | Settings | File Templates.
 */
public class LimitedSizeQueue<K> extends ArrayList<K> {

    private int maxSize;

    public LimitedSizeQueue(int size){
        this.maxSize = size;
    }

    public boolean add(K k){
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize - 1);
        }
        return r;
    }

    public K getYongest() {
        if (size() != 0)
            return get(size() - 1);
        else
            return null;
    }

    public K getOldest() {
        return get(0);
    }
}