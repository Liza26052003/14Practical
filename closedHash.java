import java.util.*;
public class closedHash{

    private LinkedList<Node>[] Keys;
    private int size;

    static class Node {
        String key;
        String value;

        public Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public closedHash(int size) {
        this.size = size;
        Keys = new LinkedList[size+1];
        for (int i = 0; i < size; i++) {
            Keys[i] = new LinkedList<>();
        }
    }

    public int hash(String key) {
        return ((Math.abs(key.hashCode())) % size)+1;}

    public void put(String key, String value){
        index = hash(key);
        for (Node node : Keys[index]){
            if (node.key.equals(key))
            node.value = value;
        }
    Keys[index].add(new Node(key, value));
    }
    public String get(String key){
        index = hash(key);
        for (Node node : Keys[index]){
            if (node.key.equals(key))
            return node.value;
        }
        return null;
    }

    public String remove(String key) {
        int index = hash(key);
        for (Node node : Keys[index]) {
            if (node.key.equals(key)) {
                String val = node.value;
                Keys[index].remove(node);
                return val;
            }
        }
        return null;
    }

    public int getSize() {
        int count = 0;
        for (LinkedList<Node> chain : Keys) {
            count += chain.size();
        }
        return count;
    }

    public static void main(String[] args) {
        int N = 1 << 20; // 1,048,576 total keys available
        int n = 950_000; // number of items to insert
        int repetitions = 5; // repetitions for timing
        int warmups = 2; // warm-up runs for JIT

        // Generate shuffled dataset
        String[] keys = new String[N];
        for (int i = 0; i < N; i++)
            keys[i] = "K" + i;
        List<String> keyList = Arrays.asList(keys);
        Collections.shuffle(keyList);

        String[] dataset = new String[n];
        String[] values = new String[n];
        for (int i = 0; i < n; i++) {
            dataset[i] = keyList.get(i);
            values[i] = String.valueOf(i + 1);
        }

        System.out.println("=== Closed Hash (Separate Chaining) ===");
        System.out.printf("n = %d, repetitions = %d, warmups = %d\n", n, repetitions, warmups);

        closedHash table = new closedHash(n / 2); // target ~0.5 load factor

        // === WARM-UP PHASE ===
        for (int w = 0; w < warmups; w++) {
            closedHash temp = new closedHash(n / 2);
            for (int i = 0; i < n; i++) {
                temp.put(dataset[i], values[i]);
            }
            for (int i = 0; i < n; i++) {
                temp.get(dataset[i]);
            }
        }

        // === INSERT TIMING ===
        long[] insertTimes = new long[repetitions];
        for (int rep = 0; rep < repetitions; rep++) {
            closedHash t = new closedHash(n / 2);
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                t.put(dataset[i], values[i]);
            }
            long t1 = System.nanoTime();
            insertTimes[rep] = t1 - t0;
        }
        printStats("INSERT", insertTimes, n);

        // Prepare table for GET/REMOVE tests
        for (int i = 0; i < n; i++) {
            table.put(dataset[i], values[i]);
        }

        // === GET TIMING ===
        long[] getTimes = new long[repetitions];
        for (int rep = 0; rep < repetitions; rep++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                table.get(dataset[i]);
            }
            long t1 = System.nanoTime();
            getTimes[rep] = t1 - t0;
        }
        printStats("GET", getTimes, n);

        // === REMOVE TIMING ===
        long[] removeTimes = new long[repetitions];
        for (int rep = 0; rep < repetitions; rep++) {
            closedHash t = new closedHash(n / 2);
            for (int i = 0; i < n; i++) {
                t.put(dataset[i], values[i]);
            }
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i += 2) {
                t.remove(dataset[i]);
            }
            long t1 = System.nanoTime();
            removeTimes[rep] = t1 - t0;
        }
        printStats("REMOVE (n/2)", removeTimes, n / 2);

        System.out.printf("Final table size: %d\n", table.getSize());
    }

    static void printStats(String op, long[] times, long ops) {
        Arrays.sort(times);
        long min = times[0];
        long max = times[times.length - 1];
        long sum = 0;
        for (long t : times) sum += t;
        long avg = sum / times.length;

        double minMs = min / 1e6;
        double maxMs = max / 1e6;
        double avgMs = avg / 1e6;
        double avgNsPerOp = avg / (double) ops;

        System.out.printf("%s: min=%.2f ms, avg=%.2f ms, max=%.2f ms, per-op=%.2f ns\n",
                op, minMs, avgMs, maxMs, avgNsPerOp);
    }


}