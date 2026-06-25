package exilib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Utility helpers for commonly used operations.
 *
 * <p>
 * Provides static helper methods to check whether a string represents an
 * integer or double, to test whether a string is non-numeric, and to prompt for
 * validated input using a {@link java.util.Scanner}.
 * </p>
 * This class is not instantiable.
 */
public final class Utils {
    private static final Random RAND = new Random();

    /** Prevent instantiation of this utility class. */
    private Utils() {
        /* This utility class should not be instantiated */
    }

    /**
     * Sort a generic array in-place using insertion sort (O(n^2)).
     *
     * @param <T> element type implementing {@link Comparable}
     * @param arr array to sort; may be {@code null}
     */
    public static <T extends Comparable<T>> void insertionSort(T[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= 0 && key.compareTo(arr[j]) < 0) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort a generic array in-place using selection sort (O(n²)).
     *
     * @param <T> element type implementing {@link Comparable}
     * @param arr array to sort; may be {@code null}
     */
    public static <T extends Comparable<T>> void selectionSort(T[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j].compareTo(arr[min]) < 0) {
                    min = j;
                }
            }
            if (min != i) {
                swap(arr, i, min);
            }
        }
    }

    private static <T extends Comparable<T>> void insertionSortRange(T[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= left && key.compareTo(arr[j]) < 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static <T extends Comparable<T>> void mergeTo(T[] source, T[] dest, int left, int mid, int right) {
        if (source[mid].compareTo(source[mid + 1]) <= 0) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i].compareTo(source[j]) <= 0) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort a generic array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param <T> element type implementing {@link Comparable}
     * @param arr array to sort; may be {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> void mergeSort(T[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        T[] temp = (T[]) new Comparable[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        T[] source = arr;
        T[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            T[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Sort a double array in-place using insertion sort (O(n²)). Efficient for
     * small/nearly-sorted arrays.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void insertionSort(double[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            double key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort a double array in-place using selection sort (O(n²)).
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void selectionSort(double[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                Utils.swap(arr, i, min);
            }
        }
    }

    private static void insertionSortRange(double[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            double key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void mergeTo(double[] source, double[] dest, int left, int mid, int right) {
        if (source[mid] <= source[mid + 1]) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i] <= source[j]) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort a double array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void mergeSort(double[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        double[] temp = new double[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        double[] source = arr;
        double[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            double[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Sort a float array in-place using insertion sort (O(n²)). Efficient for
     * small/nearly-sorted arrays.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void insertionSort(float[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            float key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort a float array in-place using selection sort (O(n²)).
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void selectionSort(float[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                Utils.swap(arr, i, min);
            }
        }
    }

    private static void insertionSortRange(float[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            float key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void mergeTo(float[] source, float[] dest, int left, int mid, int right) {
        if (source[mid] <= source[mid + 1]) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i] <= source[j]) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort a float array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void mergeSort(float[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        float[] temp = new float[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        float[] source = arr;
        float[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            float[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Sort a long array in-place using insertion sort (O(n²)). Efficient for
     * small/nearly-sorted arrays.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void insertionSort(long[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            long key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort a long array in-place using selection sort (O(n²)).
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void selectionSort(long[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                Utils.swap(arr, i, min);
            }
        }
    }

    private static void insertionSortRange(long[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            long key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void mergeTo(long[] source, long[] dest, int left, int mid, int right) {
        if (source[mid] <= source[mid + 1]) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i] <= source[j]) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort a long array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void mergeSort(long[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        long[] temp = new long[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        long[] source = arr;
        long[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            long[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Sort an int array in-place using insertion sort (O(n²)). Efficient for
     * small/nearly-sorted arrays.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void insertionSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort an int array in-place using selection sort (O(n²)).
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void selectionSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                Utils.swap(arr, i, min);
            }
        }
    }

    private static void insertionSortRange(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void mergeTo(int[] source, int[] dest, int left, int mid, int right) {
        if (source[mid] <= source[mid + 1]) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i] <= source[j]) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort an int array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        int[] temp = new int[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        int[] source = arr;
        int[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            int[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Sort a short array in-place using insertion sort (O(n²)). Efficient for
     * small/nearly-sorted arrays.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void insertionSort(short[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            short key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort a short array in-place using selection sort (O(n²)).
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void selectionSort(short[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                Utils.swap(arr, i, min);
            }
        }
    }

    private static void insertionSortRange(short[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            short key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void mergeTo(short[] source, short[] dest, int left, int mid, int right) {
        if (source[mid] <= source[mid + 1]) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i] <= source[j]) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort a short array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void mergeSort(short[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        short[] temp = new short[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        short[] source = arr;
        short[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            short[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Sort a byte array in-place using insertion sort (O(n²)). Efficient for
     * small/nearly-sorted arrays.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void insertionSort(byte[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 1; i < arr.length; i++) {
            byte key = arr[i];
            int j = i - 1;
            while (j >= 0 && key < arr[j]) {
                arr[j + 1] = arr[j--];
            }
            arr[j + 1] = key;
        }
    }

    /**
     * Sort a byte array in-place using selection sort (O(n²)).
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void selectionSort(byte[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            if (min != i) {
                Utils.swap(arr, i, min);
            }
        }
    }

    private static void insertionSortRange(byte[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            byte key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void mergeTo(byte[] source, byte[] dest, int left, int mid, int right) {
        if (source[mid] <= source[mid + 1]) {
            System.arraycopy(source, left, dest, left, right - left + 1);
            return;
        }
        int i = left;
        int j = mid + 1;
        int k = left;
        while (i <= mid && j <= right) {
            if (source[i] <= source[j]) {
                dest[k++] = source[i++];
            } else {
                dest[k++] = source[j++];
            }
        }
        while (i <= mid) {
            dest[k++] = source[i++];
        }
        while (j <= right) {
            dest[k++] = source[j++];
        }
    }

    /**
     * Sort a byte array in-place using optimized merge sort (O(n log n)). Uses
     * insertion sort for small runs and ping-pong buffering for efficiency.
     *
     * @param arr array to sort; may be {@code null}
     */
    public static void mergeSort(byte[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        final int INSERTION_SORT_THRESHOLD = 32;
        int len = arr.length;
        if (len <= INSERTION_SORT_THRESHOLD) {
            insertionSortRange(arr, 0, len - 1);
            return;
        }
        byte[] temp = new byte[len];
        for (int left = 0; left < len; left += INSERTION_SORT_THRESHOLD) {
            int right = Math.min(left + INSERTION_SORT_THRESHOLD - 1, len - 1);
            insertionSortRange(arr, left, right);
        }
        byte[] source = arr;
        byte[] dest = temp;
        int width = INSERTION_SORT_THRESHOLD;
        while (width < len) {
            for (int left = 0; left < len; left += width << 1) {
                int mid = Math.min(left + width - 1, len - 1);
                int right = Math.min(left + (width << 1) - 1, len - 1);
                if (mid >= right) {
                    System.arraycopy(source, left, dest, left, right - left + 1);
                } else {
                    mergeTo(source, dest, left, mid, right);
                }
            }
            byte[] tmp = source;
            source = dest;
            dest = tmp;
            width <<= 1;
        }
        if (source != arr) {
            System.arraycopy(source, 0, arr, 0, len);
        }
    }

    /**
     * Convert months to years, rounded to two decimal places.
     *
     * @param months number of months
     * @return equivalent years
     */
    public static double toYears(int months) { return Math.round(months / 12.0 * 100.0) / 100.0; }

    /**
     * Print an object array to standard output.
     *
     * @param arr array to print; may be {@code null}
     */
    public static void print(Object[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a float array to standard output.
     *
     * @param arr array to print; may be null
     */
    public static void print(float[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a double array to standard output.
     *
     * @param arr array to print; may be null
     */
    public static void print(double[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a long array to standard output.
     *
     * @param arr array to print; may be null
     */
    public static void print(long[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print an int array to standard output.
     *
     * @param arr array to print; may be null
     */
    public static void print(int[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a short array to standard output.
     *
     * @param arr array to print; may be null
     */
    public static void print(short[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a byte array to standard output.
     *
     * @param arr array to print; may be null
     */
    public static void print(byte[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a list to standard output.
     *
     * @param list list to print; may be null
     */
    public static void print(List<?> list) { System.out.println(listStr(list)); }

    /**
     * Return a compact string representation of a list (e.g., {@code [a, b, c]}).
     * Returns {@code "null"} if list is null, {@code "[]"} if empty.
     *
     * @param list list to convert; may be {@code null}
     * @return non-null string representation
     */
    public static String listStr(List<?> list) {
        if (list == null) {
            return "null";
        }
        int max = list.size() - 1;
        if (max == -1) {
            return "[]";
        }
        var sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < max; i++) {
            sb.append(list.get(i).toString()).append(", ");
        }
        sb.append(list.get(max)).append("]");
        return sb.toString();
    }

    /**
     * Fill an int array in-place with pseudorandom values in {@code [0, length)}.
     *
     * @param arr array to fill; must not be {@code null}
     * @throws NullPointerException if arr is null
     */
    public static void fillRandomArray(int[] arr) {
        final int n = arr.length << 1;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = RAND.nextInt(0, n);
        }
    }

    /**
     * Create and return a new int array of the given length filled with
     * pseudorandom values in {@code [0, length)}.
     *
     * @param length array length (non-negative)
     * @return newly allocated random array
     * @throws NegativeArraySizeException if length is negative
     */
    public static int[] newRandomArray(int length) {
        var arr = new int[length];
        final int n = length << 1;
        for (int i = 0; i < length; i++) {
            arr[i] = RAND.nextInt(0, n);
        }
        return arr;
    }

    /**
     * Create a shallow copy of a generic array.
     *
     * @param <T> element type
     * @param arr source array to copy; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] arr) {
        if (arr.length == 0) {
            return (T[]) new Object[0];
        }
        T[] copy = (T[]) new Object[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of a long array.
     *
     * @param arr source array; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    public static long[] copy(long[] arr) {
        long[] copy = new long[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of an int array.
     *
     * @param arr source array; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    public static int[] copy(int[] arr) {
        int[] copy = new int[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of a short array.
     *
     * @param arr source array; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    public static short[] copy(short[] arr) {
        short[] copy = new short[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of a byte array.
     *
     * @param arr source array; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    public static byte[] copy(byte[] arr) {
        byte[] copy = new byte[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of a double array.
     *
     * @param arr source array; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    public static double[] copy(double[] arr) {
        double[] copy = new double[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of a float array.
     *
     * @param arr source array; must not be null
     * @return new array with same elements
     * @throws NullPointerException if arr is null
     */
    public static float[] copy(float[] arr) {
        float[] copy = new float[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of a list as a new modifiable {@link ArrayList}.
     *
     * @param <T>  element type
     * @param list source list; must not be null
     * @return new ArrayList with same elements
     * @throws NullPointerException if list is null
     */
    public static <T> List<T> copy(List<T> list) {
        var copy = new ArrayList<T>();
        for (int i = 0; i < list.size(); i++) {
            copy.add(list.get(i));
        }
        return copy;
    }

    /**
     * Swap two elements in a double array in-place.
     *
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static void swap(double[] arr, int indexA, int indexB) {
        double temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a float array in-place.
     *
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static void swap(float[] arr, int indexA, int indexB) {
        float temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a long array in-place.
     *
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static void swap(long[] arr, int indexA, int indexB) {
        long temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in an int array in-place.
     *
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static void swap(int[] arr, int indexA, int indexB) {
        int temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a short array in-place.
     *
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static void swap(short[] arr, int indexA, int indexB) {
        short temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a byte array in-place.
     *
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static void swap(byte[] arr, int indexA, int indexB) {
        byte temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a generic array in-place.
     *
     * @param <T>    element type
     * @param arr    array; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or ArrayIndexOutOfBoundsException on error
     */
    public static <T> void swap(T[] arr, int indexA, int indexB) {
        T temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a list in-place. Not thread-safe.
     *
     * @param <T>    element type
     * @param list   list; must not be null
     * @param indexA first index
     * @param indexB second index
     * @throws NullPointerException or IndexOutOfBoundsException on error
     */
    public static <T> void swap(List<T> list, int indexA, int indexB) {
        list.set(indexA, list.set(indexB, list.get(indexA)));
    }

    /**
     * Measure the elapsed wall-clock time (in nanoseconds) to execute a
     * {@link Runnable}.
     *
     * @param runnable task to execute; may be null
     * @return elapsed nanoseconds, or 0 if runnable is null
     */
    public static long time(Runnable runnable) {
        if (runnable == null) {
            return 0;
        }
        long startTime = System.nanoTime();
        runnable.run();
        return System.nanoTime() - startTime;
    }

    /**
     * Measure the elapsed time (in nanoseconds) to execute a {@link Supplier} and
     * return both elapsed time and result as an Object[] {nanos, result}.
     *
     * @param <T>      result type
     * @param supplier task to execute; may be null
     * @return {nanos, result} or {0, null} if supplier is null
     */
    public static <T> Object[] timeAndGetResult(Supplier<T> supplier) {
        if (supplier == null) {
            return new Object[] {0, null};
        }
        long startTime = System.nanoTime();
        T result = supplier.get();
        return new Object[] {System.nanoTime() - startTime, result};
    }

    /**
     * Convert nanoseconds to milliseconds.
     *
     * @param nanos duration in nanoseconds
     * @return duration in milliseconds
     */
    public static double nanosToMillis(long nanos) { return nanos / 1000000.0; }

    /**
     * Convert nanoseconds (double) to milliseconds.
     *
     * @param nanos duration in nanoseconds
     * @return duration in milliseconds
     */
    public static double nanosToMillis(double nanos) { return nanos / 1000000.0; }

    /**
     * Convert nanoseconds to milliseconds rounded to two decimal places.
     *
     * @param nanos duration in nanoseconds
     * @return rounded duration in milliseconds
     */
    public static double roundedNanosToMillis(long nanos) { return roundTwoDecimals(nanos / 1000000.0); }

    /**
     * Convert nanoseconds (double) to milliseconds rounded to two decimal places.
     *
     * @param nanos duration in nanoseconds
     * @return rounded duration in milliseconds
     */
    public static double roundedNanosToMillis(double nanos) { return roundTwoDecimals(nanos / 1000000.0); }

    /**
     * Return the arithmetic average of Number values in a list.
     *
     * @param list list to average; may be null or empty
     * @return average as a Number, or 0 if list is empty
     */
    public static Number avg(List<? extends Number> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Number num : list) {
            if (num != null) {
                sum += num.doubleValue();
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return sum / count;
    }

    /**
     * Return the arithmetic average of Double values in a list.
     *
     * @param list list to average; may be null or empty
     * @return average as a double, or 0 if list is empty
     */
    public static double avgDouble(List<Double> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Double num : list) {
            if (num != null) {
                sum += num;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return sum / count;
    }

    /**
     * Return the arithmetic average of Float values in a list.
     *
     * @param list list to average; may be null or empty
     * @return average as a double, or 0 if list is empty
     */
    public static double avgFloat(List<Float> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Float num : list) {
            if (num != null) {
                sum += num;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return sum / count;
    }

    /**
     * Return the rounded arithmetic average of Long values in a list.
     *
     * @param list list to average; may be null or empty
     * @return rounded average as a long, or 0 if list is empty
     */
    public static long avgLong(List<Long> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Long num : list) {
            if (num != null) {
                sum += num;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return Math.round(sum / count);
    }

    /**
     * Return the rounded arithmetic average of Integer values in a list.
     *
     * @param list list to average; may be null or empty
     * @return rounded average as an int, or 0 if list is empty
     */
    public static int avgInt(List<Integer> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Integer num : list) {
            if (num != null) {
                sum += num;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return (int) Math.round(sum / count);
    }

    /**
     * Return the rounded arithmetic average of Short values in a list.
     *
     * @param list list to average; may be null or empty
     * @return rounded average as a short, or 0 if list is empty
     */
    public static short avgShort(List<Short> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Short num : list) {
            if (num != null) {
                sum += num;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return (short) Math.round(sum / count);
    }

    /**
     * Return the rounded arithmetic average of Byte values in a list.
     *
     * @param list list to average; may be null or empty
     * @return rounded average as a byte, or 0 if list is empty
     */
    public static byte avgByte(List<Byte> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        double sum = 0.0;
        int count = 0;
        for (Byte num : list) {
            if (num != null) {
                sum += num;
                count++;
            }
        }
        if (count == 0) {
            return 0;
        }
        return (byte) Math.round(sum / count);
    }

    /**
     * Round a number to two decimal places.
     *
     * @param num number to round
     * @return rounded number
     */
    public static double roundTwoDecimals(double num) { return Math.round(num * 100.0) / 100.0; }

    /**
     * Test whether a string can be parsed as an Integer.
     *
     * @param in string to test; may be null
     * @return true if in is a valid integer, false otherwise
     */
    public static boolean isInt(String in) { return in != null && isIntFast(in); }

    /**
     * Test whether a string can be parsed as a Double.
     *
     * @param in string to test; may be null
     * @return true if in is a valid double, false otherwise
     */
    public static boolean isDouble(String in) { return in != null && isDoubleFast(in); }

    /**
     * Fast check whether a string contains a valid integer representation. This
     * helper assumes callers have already checked for {@code null}.
     *
     * @param in the input string to test (must be non-null)
     * @return {@code true} if {@code in} parses as an integer, {@code false}
     *         otherwise
     */
    private static boolean isIntFast(String in) {
        try {
            Integer.valueOf(in);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    /**
     * Quick test whether a non-null string contains a valid double representation.
     * Assumes callers have already checked for null.
     *
     * @param in non-null input string to test
     * @return true if in parses as a double, false otherwise
     */
    private static boolean isDoubleFast(String in) {
        try {
            Double.valueOf(in);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    /**
     * Test whether a string is non-empty and not numeric.
     *
     * @param in string to test; may be null
     * @return true if in is non-null, non-empty, and neither an integer nor double
     */
    public static boolean isNotNum(String in) {
        return in != null && !in.isEmpty() && !isIntFast(in) && !isDoubleFast(in);
    }

    /**
     * Normalize a possibly {@code null} string to a non-null value.
     *
     * @param in input string that may be {@code null}
     * @return original string when non-null, otherwise the empty string
     */
    private static String validateNonNullString(String in) { return in == null ? "" : in; }

    /**
     * Prompt user repeatedly until a valid double is entered.
     *
     * @param input        Scanner to read from; must not be null
     * @param inputMessage prompt message; may be null
     * @return parsed double entered by user
     * @throws IllegalArgumentException if input is null
     */
    public static double takeUserDoubleInput(Scanner input, String inputMessage) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        String message = validateNonNullString(inputMessage);
        String in;
        do {
            System.out.print(message);
            in = input.nextLine().trim();
        } while (!isDouble(in));
        return Double.parseDouble(in);
    }

    /**
     * Prompt user repeatedly until a valid integer is entered.
     *
     * @param input        Scanner to read from; must not be null
     * @param inputMessage prompt message; may be null
     * @return parsed int entered by user
     * @throws IllegalArgumentException if input is null
     */
    public static int takeUserIntInput(Scanner input, String inputMessage) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        String message = validateNonNullString(inputMessage);
        String in;
        do {
            System.out.print(message);
            in = input.nextLine().trim();
        } while (!isInt(in));
        return Integer.parseInt(in);
    }

    /**
     * Prompt user repeatedly until a non-numeric string is entered.
     *
     * @param input        Scanner to read from; must not be null
     * @param inputMessage prompt message; may be null
     * @return non-numeric string entered by user
     * @throws IllegalArgumentException if input is null
     */
    public static String takeUserStringInput(Scanner input, String inputMessage) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        String message = validateNonNullString(inputMessage);
        String in;
        do {
            System.out.print(message);
            in = input.nextLine().trim();
        } while (!isNotNum(in));
        return in;
    }
}