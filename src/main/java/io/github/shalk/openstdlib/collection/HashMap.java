package io.github.shalk.openstdlib.collection;


import java.util.Objects;

/**
 * 1. 用entry 数组保存map
 * 2. 对于k 存放的位置，通过hash保存 求mod
 * 3. 如果存放的位置已经 有元素了，就保存成链表或者红黑树
 * 4. 找位置
 * 5. hash 冲突之后，链表和红黑树
 * 6. 扩容迁移
 * // TODO 红黑树转换
 *
 * @param <K>
 * @param <V>
 */
public class HashMap<K, V> implements Map<K, V> {
  private int INIT_SIZE = 16;
  private Object[] arr;
  private int size;

  private double factor = .75;

  public HashMap() {
    this.arr = new Object[INIT_SIZE];
    this.size = 0;
  }

  private static class Entry<K, V> {
    private K k;
    private V v;

    public Entry(K k, V v) {
      this.k = k;
      this.v = v;
    }
  }

  private static class ListNodeEntry<K, V> {
    private K k;
    private V v;
    private ListNodeEntry<K, V> next;
    private ListNodeEntry<K, V> pre;

    public ListNodeEntry(K k, V v) {
      this.k = k;
      this.v = v;
    }
  }


  @Override
  public void put(K k, V v) {
    int i = indexOf(k);
    putAtIndex(i, k, v, arr);
    size++;
    checkResize();
  }

  private void checkResize() {
    if (size * 1.0 / arr.length >= factor) {
      // 发生扩容
      System.out.println("发生扩容");
      if (arr.length > (Integer.MAX_VALUE >> 1)) {
        throw new IllegalStateException("too large map");
      }
      Object[] newArr = new Object[arr.length << 1];
      for (int i = 0; i < arr.length; i++) {
        if (arr[i] == null) {

        } else if (arr[i] instanceof Entry) {
          Entry<K, V> node = (Entry) arr[i];
          int j = indexOf(node.k, newArr.length);
          putAtIndex(j, node.k, node.v, newArr);
        } else if (arr[i] instanceof ListNodeEntry) {
          ListNodeEntry<K, V> node = (ListNodeEntry) arr[i];
          while (node != null) {
            int j = indexOf(node.k, newArr.length);
            putAtIndex(j, node.k, node.v, newArr);
            node = node.next;
          }
        }
      }
      arr = newArr;
    }
  }

  private void putAtIndex(int i, K k, V v, Object[] arr) {
    if (arr[i] == null) {
      arr[i] = newEntry(k, v);
    } else {
      Object p = arr[i];
      if (p instanceof Entry) {
        Entry<K, V> node = (Entry) p;
        if (Objects.equals(node.k, k)) {
          node.v = v;
        } else {
          // 转链表
          ListNodeEntry listNodeEntry = newListEntry((K) node.k, (V) node.v);
          ListNodeEntry listNodeEntry2 = newListEntry(k, v);
          arr[i] = listNodeEntry;
          listNodeEntry.next = listNodeEntry2;
          listNodeEntry2.pre = listNodeEntry;
//          System.out.printf("hash冲突了，弄成链表，k1=%s, k2=%s\n", node.k, k);
        }
      } else if (p instanceof ListNodeEntry) {
        ListNodeEntry node = (ListNodeEntry) p;
        ListNodeEntry pre = null;
        while (node != null) {
          if (Objects.equals(node.k, k)) {
            node.v = v;
            break;
          }
          pre = node;
          node = node.next;
        }
        if (node == null) {
          pre.next = newListEntry(k, v);
          pre.next.pre = pre;
//          System.out.printf("链表连起来，k1=%s, k2=%s\n", pre.k, k);
        }

      }
    }
  }

  Entry newEntry(K k, V v) {
    return new Entry(k, v);
  }

  ListNodeEntry newListEntry(K k, V v) {
    return new ListNodeEntry<>(k, v);
  }

  int indexOf(K k, int len) {
    int hash = k.hashCode();
    hash = (hash >>> 16) ^ hash;
    // arr 的长度始终为 2的幂次
    int n = len;
    return hash & (n - 1);
  }

  int indexOf(K k) {
    return indexOf(k, arr.length);
  }

  @Override
  public V get(K k) {
    int i = indexOf(k);
    if (arr[i] == null) {
      return null;
    } else if (arr[i] instanceof Entry) {
      Entry<K, V> node = (Entry<K, V>) arr[i];
      if (Objects.equals(node.k, k)) {
        return node.v;
      }
    } else if (arr[i] instanceof ListNodeEntry) {
      ListNodeEntry<K, V> node = (ListNodeEntry<K, V>) arr[i];
      while (node != null) {
        if (Objects.equals(node.k, k)) {
          return node.v;
        }
        node = node.next;
      }
    }
    return null;
  }

  @Override
  public void remove(K k) {
    int i = indexOf(k);
    if (arr[i] == null) {

    } else if (arr[i] instanceof Entry) {
      Entry<K, V> node = (Entry<K, V>) arr[i];
      if (Objects.equals(node.k, k)) {
        arr[i] = null;
        size--;
      }
    } else if (arr[i] instanceof ListNodeEntry) {
      ListNodeEntry<K, V> node = (ListNodeEntry<K, V>) arr[i];
      ListNodeEntry dump = new ListNodeEntry(null, null);
      dump.next = node;
      node.pre = dump;
      while (node != null) {
        if (Objects.equals(node.k, k)) {
          node.pre.next = node.next;
          node.next.pre = node.pre;
          node.pre = null;
          node.next = null;
          size--;
          break;
        }
        node = node.next;
      }
      arr[i] = dump.next;
    }
  }

  @Override
  public boolean containsKey(K k) {
    int i = indexOf(k);
    if (arr[i] == null) {
      return false;
    } else if (arr[i] instanceof Entry) {
      Entry<K, V> node = (Entry<K, V>) arr[i];
      if (Objects.equals(node.k, k)) {
        return true;
      }
    } else if (arr[i] instanceof ListNodeEntry) {
      ListNodeEntry<K, V> node = (ListNodeEntry<K, V>) arr[i];
      while (node != null) {
        if (Objects.equals(node.k, k)) {
          return true;
        }
        node = node.next;
      }
    }
    return false;
  }

  @Override
  public int size() {
    return size;
  }

  public static void main(String[] args) {
    HashMap<String, String> map = new HashMap<>();
    for (int i = 0; i < 2000; i++) {
      map.put(String.valueOf(i), String.valueOf(i));
    }
    for (int i = 0; i < 2000; i ++) {
      String k = String.valueOf(i);
      assert k.equals(map.get(k));
    }
    assert map.size == 2000;
  }
}
