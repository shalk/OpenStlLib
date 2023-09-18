package io.github.shalk.openstdlib.collection;

public interface Map<K, V> {

  void put(K k, V v);

  V get(K k);

  void remove(K k);

  boolean containsKey(K k);

  int size();
}
