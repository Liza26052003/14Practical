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
        Keys = new LinkedList[size];
        for (int i = 0; i < size; i++) {
            Keys[i] = new LinkedList<>();
        }
    } 








}