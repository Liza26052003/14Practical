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








}