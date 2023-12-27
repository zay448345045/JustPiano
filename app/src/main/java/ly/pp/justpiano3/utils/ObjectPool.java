package ly.pp.justpiano3.utils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个对象池
 */
public class ObjectPool<T> {
    private final ConcurrentHashMap<T, Boolean> pool;
    private final ObjectFactory<T> factory;
    private final int maxPoolSize;

    public ObjectPool(ObjectFactory<T> factory, int maxPoolSize) {
        this.pool = new ConcurrentHashMap<>(maxPoolSize);
        this.factory = factory;
        this.maxPoolSize = maxPoolSize;
    }

    public T acquire() {
        T instance;
        if (pool.isEmpty()) {
            instance = factory.createObject();
        } else {
            Iterator<T> iterator = pool.keySet().iterator();
            if (iterator.hasNext()) {
                instance = iterator.next();
                pool.remove(instance);
            } else {
                instance = factory.createObject();
            }
        }
        return instance;
    }


    public void release(T instance) {
        if (pool.size() < maxPoolSize) {
            pool.put(instance, true);
        }
    }

    public interface ObjectFactory<T> {
        T createObject();
    }
}
