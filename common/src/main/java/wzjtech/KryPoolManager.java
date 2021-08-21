package wzjtech;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.Pool;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * Kryo instances pool
 */
public class KryPoolManager {
    private final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
        protected Kryo create() {
            Kryo kryo = new Kryo();

           /* By default, each appearance of an object in the graph after the first is stored
            as an integer ordinal. This allows multiple references to the same object and cyclic
            graphs to be serialized.
            This has a small amount of overhead and can be disabled to save space if it is not needed*/
            kryo.setReferences(false);

            /*
             * Kryo#setRegistrationRequired can be set to true to throw an exception when any unregistered
             * class is encountered.
             * This prevents an application from accidentally using class name strings
             */
            kryo.setRegistrationRequired(false);

            /*
             *When ReflectASM or reflection cannot be used, Kryo can be configured to use an InstantiatorStrategy to handle
             * creating instances of a class. Objenesis provides StdInstantiatorStrategy which uses JVM specific APIs to create
             * an instance of a class without calling any constructor at all. While this works on many JVMs,
             * a zero argument is generally more portable.
             */
            kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    public Kryo obtain() {
        return kryoPool.obtain();
    }

    public void free(Kryo kryoObj) {
        kryoPool.free(kryoObj);
    }
}
