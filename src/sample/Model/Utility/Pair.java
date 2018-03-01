package sample.Model.Utility;

public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        Pair pair = (Pair) o;
        return o != null && key.equals( pair.key ) && value.equals( pair.value );
    }

}
