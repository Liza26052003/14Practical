import java.util.*;
public class openHash{

    public static void main(String[] args) {
        int N = 1 << 20; // 1,048,576
        int limit = 950_000;
        
        // 1. Generate keys
        String[] keys = new String[N];
        for (int i = 0; i < N; i++) {
            keys[i] = "K" + i;
        }

        // 2. Shuffle keys
        List<String> keyList = Arrays.asList(keys);
        Collections.shuffle(keyList);

        // 3. Create Key-Value Pairs
        // Using a custom Pair class or an array of objects
        KeyValuePair[] dataset = new KeyValuePair[N];
        for (int i = 0; i < N; i++) {
            // Numbering the shuffled keys from 1 to N
            dataset[i] = new KeyValuePair(keyList.get(i), String.valueOf(i + 1));
        }

        KeyValuePair[] experimentData = Arrays.copyOfRange(dataset, 0, limit);

        System.out.println("Dataset ready. Size: " + experimentData.length);
    }
    static class KeyValuePair {
        String key;
        String value;

        KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}







