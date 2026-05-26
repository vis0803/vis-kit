package vis.kit;

import vis.kit.IdGenerator.contract.IIdGenerator;
import vis.kit.IdGenerator.contract.IdGeneratorOptions;
import vis.kit.IdGenerator.idgen.DefaultIdGenerator;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 预分配64个，workId取值 0-63 ，默认使用 0
 */
public class IdKit {

    private static final IIdGenerator[] WORKERS = new IIdGenerator[64];

    static {
        for (short i = 0; i < 64; i++) {
            IdGeneratorOptions options = new IdGeneratorOptions(i);
            WORKERS[i] = new DefaultIdGenerator(options);
        }
    }

    public static long nextId() {
        return nextId(0);
    }

    public static String nextIdStr() {
        return nextId(0) + "";
    }

    public static long nextId(int workId) {
        TOTAL_GENERATED.incrementAndGet();
        return WORKERS[workId].newLong();
    }

    public static String nextIdStr(int workId) {
        return nextId(workId) + "";
    }

    private static final AtomicLong TOTAL_GENERATED = new AtomicLong();
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger();
    private static final Set<Long> ALL_IDS = ConcurrentHashMap.newKeySet();
    private static final Set<Long> DUPLICATES = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        for (int k = 0; k < 20000; k++) {
            for (int i = 0; i < 50; i++) {
                final int workerId = i;
                THREAD_COUNT.incrementAndGet();
                Thread vt1 = Thread.startVirtualThread(() -> {
                    long l = nextId(workerId);
                    if (!ALL_IDS.add(l)) {
                        System.out.println(l);
                        DUPLICATES.add(l);
                    }
                });
            }
        }
        System.out.println("100W条 多线程同时执行，分配给50个生成器 独自生成ID");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("累计启动线程：" + THREAD_COUNT.get() + "条");
        System.out.println("累计生成数据：" + TOTAL_GENERATED.get() + "条");
        System.out.println("重复数据：" + DUPLICATES.size() + "条");
        System.out.println("不重复数据：" + ALL_IDS.size() + "条");
        long end = System.currentTimeMillis();
        System.out.println("累计执行时间：" + ((end - start) / 1000.0) + "秒");

    }
}
