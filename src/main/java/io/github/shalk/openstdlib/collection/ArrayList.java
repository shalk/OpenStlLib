package io.github.shalk.openstdlib.collection;

/**
 * 1. 数据结构是数组
 * 2. 增：就放到数组最后面, 可能超出大小，需要扩容
 * 3. 查询: 判断是否越界
 * 4. 删除: 判断是否越界，删除了元素需要，移动其他元素
 * 5. 修改：判断是否越界
 * 越界实现 checkOutOfIndex()
 * 扩容： resize();
 * 移动元素: copy();
 */
public class ArrayList<T> implements List<T> {

  private final int INIT_SIZE = 10;

  private final int MAX_SIZE = Integer.MAX_VALUE - 8;
  Object[] arr;

  private int size = 0;


  public ArrayList() {
    this.arr = new Object[INIT_SIZE];
    this.size = 0;
  }

  @Override
  public void add(T t) {
    if (size + 1 > arr.length) {
      resize();
    }
    arr[size] = t;
    size++;
  }

  void resize() {
    // 1.5 扩容
    int oldSize = arr.length;
    int newSize = oldSize + (oldSize << 1);
    if (oldSize > newSize) {
      // 越界了
      newSize = oldSize;
    }
    Object[] newArr = new Object[newSize];
    System.arraycopy(arr, 0, newArr, 0, oldSize);
    arr = newArr;
  }

  @Override
  public T get(int i) {
    checkOutOfBound(i);
    return (T) arr[i];
  }

  private void checkOutOfBound(int i) {
    if (i >= size) throw new IndexOutOfBoundsException();
  }

  @Override
  public void remove(T t) {
    int j = -1;
    for (int i = 0; i < size; i++) {
      if (arr[i] == t) {
        j = i;
        break;
      }
    }
    if (j == -1) return;
    remove(j);
  }

  private void copy(int a, int b, int len) {
    if (len <= 0) return;
    System.arraycopy(arr, a, arr, b, len);
  }


  @Override
  public void set(int i, T t) {
    checkOutOfBound(i);
    arr[i] = t;
  }

  @Override
  public void remove(int j) {
    checkOutOfBound(j);
    int len = size - j - 1;
    copy(j + 1, j, len);
    size--;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size <= 0;
  }

  public static void main(String[] args) {
    ArrayList<Integer> arr = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      arr.add(i);
    }
    System.out.println("arr.size() = " + arr.size());
    for (int i = 0 ; i< arr.size(); i++) {
      System.out.printf("arr[%d]=%d\n",i, arr.get(i));
    }
    for (int i = 1; i < 19; i++) {
      arr.remove(1);
    }
    System.out.println("arr.size() = " + arr.size());
    for (int i = 0 ; i< arr.size(); i++) {
      System.out.printf("arr[%d]=%d\n",i, arr.get(i));
    }
  }
}
