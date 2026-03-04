import java.util.*;
public class openHash{

    public static void main(String[] args) {
        int N = 1 << 20; // 1,048,576
        int limit = (args.length > 0) ? Integer.parseInt(args[0]) : 950_000;

        // 1. Generate keys
        String[] keys = new String[N];
        for (int i = 0; i < N; i++) {
            keys[i] = "K" + i;
        }

        // 2. Shuffle keys
        List<String> keyList = Arrays.asList(keys);
        Collections.shuffle(keyList);

        // 3. Create Key-Value Pairs
        KeyValuePair[] dataset = new KeyValuePair[N];
        for (int i = 0; i < N; i++) {
            dataset[i] = new KeyValuePair(keyList.get(i), String.valueOf(i + 1));
        }

        KeyValuePair[] experimentData = Arrays.copyOfRange(dataset, 0, limit);

        System.out.println("Dataset ready. Size: " + experimentData.length);

        // Create hash table and insert
        OpenAddressingHashTable table = new OpenAddressingHashTable();

        long t0 = System.nanoTime();
        for (KeyValuePair kv : experimentData) {
            table.put(kv.key, kv.value);
        }
        long t1 = System.nanoTime();

        System.out.printf("Inserted %d entries in %.2f ms\n", table.size(), (t1 - t0) / 1e6);
        System.out.printf("Table size: %d, load factor: %.4f\n", table.capacity(), table.loadFactor());

        // Verify lookups for all inserted keys
        long g0 = System.nanoTime();
        int found = 0;
        for (KeyValuePair kv : experimentData) {
            String v = table.get(kv.key);
            if (v != null && v.equals(kv.value)) found++;
        }
        long g1 = System.nanoTime();
        System.out.printf("Verified %d/%d entries in %.2f ms\n", found, experimentData.length, (g1 - g0) / 1e6);

        // Remove half of the keys (every 2nd key) and re-verify
        long r0 = System.nanoTime();
        int removed = 0;
        for (int i = 0; i < experimentData.length; i += 2) {
            if (table.remove(experimentData[i].key) != null) removed++;
        }
        long r1 = System.nanoTime();
        System.out.printf("Removed %d entries in %.2f ms\n", removed, (r1 - r0) / 1e6);

        System.out.printf("Size after removals: %d, load factor: %.4f\n", table.size(), table.loadFactor());

        // Quick sanity: ensure removed keys are gone and remaining are present
        int ok = 0, miss = 0;
        for (int i = 0; i < experimentData.length; i++) {
            String v = table.get(experimentData[i].key);
            boolean shouldExist = (i % 2 == 1);
            if (shouldExist) {
                if (v != null) ok++; else miss++;
            } else {
                if (v == null) ok++; else miss++;
            }
        }
        System.out.printf("Post-removal check: ok=%d, miss=%d\n", ok, miss);
    }

    static class KeyValuePair {
        String key;
        String value;

        KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    static class OpenAddressingHashTable {
        static class Entry {
            String key;
            String value;
            boolean deleted;
            Entry(String k, String v) { key = k; value = v; deleted = false; }
        }

        private Entry[] table;
        private int size;

        OpenAddressingHashTable() {
            table = new Entry[16];
            size = 0;
        }

        private int indexFor(String key, int capacity) {
            int h = key.hashCode() & 0x7fffffff;
            return h % capacity;
        }

        public void put(String key, String value) {
            if (loadFactor() > 0.5) resize(table.length * 2);

            int idx = indexFor(key, table.length);
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
            // if we reach here, table is full of tombstones; resize and retry
            resize(table.length * 2);
            put(key, value);
        }

        public String get(String key) {
            int idx = indexFor(key, table.length);
            for (int i = 0; i < table.length; i++) {
                int p = (idx + i) % table.length;
                Entry e = table[p];
                if (e == null) return null;
                if (!e.deleted && e.key.equals(key)) return e.value;
            }
            return null;
        }

        public String remove(String key) {
            int idx = indexFor(key, table.length);
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

        public int size() { return size; }

        public int capacity() { return table.length; }

        public double loadFactor() {
            return (double) size / table.length;
        }

        private void resize(int newCap) {
            Entry[] old = table;
            table = new Entry[newCap];
            size = 0;
            for (Entry e : old) {
                if (e != null && !e.deleted) {
                    // reinsert
                    int idx = indexFor(e.key, table.length);
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
    }
}







