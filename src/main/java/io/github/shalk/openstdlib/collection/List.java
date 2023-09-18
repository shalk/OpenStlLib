package io.github.shalk.openstdlib.collection;

public interface List<T> {
  void add(T t);


  T get(int i);

  void set(int i, T t);

  void remove(int i, T t);

  void remove(T t);


  int size();

  boolean isEmpty();


}
