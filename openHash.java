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

        int limit = 950_000;
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < limit; i++) {
            put(list.get(i), String.valueOf(i + 1));
        }
        System.out.println("Inserted " + limit + " in " + (System.currentTimeMillis() - t0) + "ms");
    }

    static void put(String k, String v) {
        int i = (k.hashCode() & 0x7fffffff) % tableK.length;
        while (tableK[i] != null && !tableK[i].equals(DELETED) && !tableK[i].equals(k)) {
            i = (i + 1) % tableK.length;
        }
        tableK[i] = k;
        tableV[i] = v;
    }

    static String get(String k) {
        int i = (k.hashCode() & 0x7fffffff) % tableK.length;
        while (tableK[i] != null) {
            if (tableK[i].equals(k)) return tableV[i];
            i = (i + 1) % tableK.length;
        }
        return null;
    }
    public boolean isitIn(String k) {
        int i = (k.hashCode() & 0x7fffffff) % tableK.length;
        while (tableK[i] != null) {
            if (tableK[i].equals(k)) return true;
            i = (i + 1) % tableK.length;
        }
        return false;
    }
    public boolean isEmpty() {
        for (String key : tableK) {
            if (key != null && !key.equals(DELETED)) return false;
        }
        return true;
    }
    public void remove(String k) {
        int i = (k.hashCode() & 0x7fffffff) % tableK.length;
        while (tableK[i] != null) {
            if (tableK[i].equals(k)) {
                tableK[i] = DELETED;
                tableV[i] = null;
                return;
            }
            i = (i + 1) % tableK.length;
        }
    
    }
    
    }




