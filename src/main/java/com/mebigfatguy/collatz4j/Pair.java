package com.mebigfatguy.collatz4j;

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
    public int hashCode() {
        return key.hashCode() ^ value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }

        Pair that = (Pair) obj;
        return key.equals(that.key) && value.equals(that.value);
    }

    @Override
    public String toString() {
        return "[" + key + ", " + value + "]";
    }

}
