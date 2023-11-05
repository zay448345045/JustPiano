package ly.pp.justpiano3.utils;

import android.util.ArrayMap;

/**
 * 一个对象池
 */
public class ObjectPool<T> {
    private final ArrayMap<T, Boolean> pool;
    private final ObjectFactory<T> factory;
    private final int maxPoolSize;

    public ObjectPool(ObjectFactory<T> factory, int maxPoolSize) {
        this.pool = new ArrayMap<>();
        this.factory = factory;
        this.maxPoolSize = maxPoolSize;
    }

    public T acquire() {
        T instance;
        if (pool.isEmpty()) {
            instance = factory.createObject();
        } else {
            instance = pool.keySet().iterator().next();
            pool.remove(instance);
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
