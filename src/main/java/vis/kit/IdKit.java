package vis.kit;

import vis.kit.IdGenerator.contract.IIdGenerator;
import vis.kit.IdGenerator.contract.IdGeneratorOptions;
import vis.kit.IdGenerator.idgen.DefaultIdGenerator;

import java.util.HashSet;

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
        return WORKERS[workId].newLong();
    }

    public static String nextIdStr(int workId) {
        return nextId(workId) + "";
    }

    private static final HashSet<Long> nowR = new HashSet<>();

    private static final HashSet<Long> R = new HashSet<>();

    public static void main(String[] args) throws InterruptedException {
        int count = 0;

        for (int k = 0; k < 2000; k++) {
            for (int i = 0; i < 60; i++) {
                final int workerId = i;
                Thread vt1 = Thread.startVirtualThread(() -> {
                    long l = nextId(workerId);
                    if (nowR.contains(l)) {
                        System.out.println(l);
                        R.add(l);
                    }
                    nowR.add(l);
                });
                count++;
            }
        }
        System.out.println("12W 条 多线程同时执行，独自生成ID");
        System.out.println("添加5秒延迟，等待数据结束");
        Thread.sleep(5000);
        System.out.println("累计生成：" + count + "条");
        System.out.println("重复数据：" + R.size() + "条");
        System.out.println("不重复数据：" + nowR.size() + "条");

    }
}
