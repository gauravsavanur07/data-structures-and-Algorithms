
private static void sort(Comparable[] a, int lo, int hi)
{
  if (hi <= lo) return;
  int lt = lo, gt = hi;
  Comparable v = a[lo];
  int i = lo;
  while (i <= gt)
  {
     int cmp = a[i].compareTo(v);
     if      (cmp < 0) exch(a, lt++, i++);
     else if (cmp > 0) exch(a, i, gt--);
     else              i++;
}
  sort(a, lo, lt - 1);
  sort(a, gt + 1, hi);
}
public static void printArray(int arr[])
{
    int n = arr.length;
    for (int i=0; i<n; ++i)
        System.out.print(arr[i] + " ");

    System.out.println();
}
public static void main(String args[])
{
    int arr[] = {12, 11, 13, 5, 6};

    QuickSort ob = new QuickSort();
    ob.sort(arr);

    printArray(arr);
}
