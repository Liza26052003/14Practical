import java.util.*;

public class OpenHash {
    static int N = 1 << 20;
    static String[] tableK = new String[N * 2]; // Pre-sized for 0.5 load factor
    static String[] tableV = new String[N * 2];
    static final String DELETED = "##DELETED##";

    public static void main(String[] args) {
        String[] keys = new String[N];
        for (int i = 0; i < N; i++) keys[i] = "K" + i;
        List<String> list = Arrays.asList(keys);
        Collections.shuffle(list);

        int idx = hash(key, table.length);
        int firstTombstone = -1;
        for (int i = 0; i < table.length; i++) {
            int p = (idx + i) % table.length;
            Entry e = table[p];
            if (e == null) {
                if (firstTombstone != -1) p = firstTombstone;
                table[p] = new Entry(key, value);
                size++;
                return;
            } else if (e.deleted) {
                if (firstTombstone == -1) firstTombstone = p;
            } else if (e.key.equals(key)) {
                table[p].value = value;
                return;
            }
        }
        resize(table.length * 2);
        put(key, value);
    }

    public String get(String key) {
        int idx = hash(key, table.length);
        for (int i = 0; i < table.length; i++) {
            int p = (idx + i) % table.length;
            Entry e = table[p];
            if (e == null) return null;
            if (!e.deleted && e.key.equals(key)) return e.value;
        }
        return null;
    }

    public String remove(String key) {
        int idx = hash(key, table.length);
        for (int i = 0; i < table.length; i++) {
            int p = (idx + i) % table.length;
            Entry e = table[p];
            if (e == null) return null;
            if (!e.deleted && e.key.equals(key)) {
                String old = e.value;
                e.key = null;
                e.value = null;
                e.deleted = true;
                size--;
                return old;
            }
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public int capacity() {
        return table.length;
    }

    public double loadFactor() {
        return (double) size / table.length;
    }

    private void resize(int newCap) {
        Entry[] old = table;
        table = new Entry[newCap];
        size = 0;
        for (Entry e : old) {
            if (e != null && !e.deleted) {
                int idx = hash(e.key, table.length);
                for (int i = 0; i < table.length; i++) {
                    int p = (idx + i) % table.length;
                    if (table[p] == null) {
                        table[p] = new Entry(e.key, e.value);
                        size++;
                        break;
                    }
                }
            }
        }
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

        System.out.println("=== Open Hash (Linear Probing) ===");
        System.out.printf("n = %d, repetitions = %d, warmups = %d\n", n, repetitions, warmups);

        // === WARM-UP PHASE ===
        for (int w = 0; w < warmups; w++) {
            OpenHash temp = new OpenHash(16);
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
            OpenHash t = new OpenHash(16);
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                t.put(dataset[i], values[i]);
            }
            long t1 = System.nanoTime();
            insertTimes[rep] = t1 - t0;
        }
        printStats("INSERT", insertTimes, n);

        // Prepare table for GET/REMOVE tests
        OpenHash table = new OpenHash(16);
        for (int i = 0; i < n; i++) {
            table.put(dataset[i], values[i]);
        }
        System.out.printf("Table capacity after inserts: %d, load factor: %.4f\n", table.capacity(), table.loadFactor());

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
            OpenHash t = new OpenHash(16);
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



