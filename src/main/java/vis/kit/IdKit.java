package vis.kit;

import vis.kit.IdGenerator.contract.IIdGenerator;
import vis.kit.IdGenerator.contract.IdGeneratorOptions;
import vis.kit.IdGenerator.idgen.DefaultIdGenerator;

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

    public static void main(String[] args) {
        System.out.println(nextId());
    }
}
