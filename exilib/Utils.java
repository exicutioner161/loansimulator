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
     * Sort a generic array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param <T> the element type; must implement {@link Comparable}
     * @param arr the array to sort; may be {@code null}
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
     * Sort a double array using insertion sort.
     * <p>
     * Sorts the array in-place using insertion sort, which has O(n^2) time
     * complexity but is efficient for small or nearly sorted arrays. Null arrays
     * are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a double array using selection sort.
     * <p>
     * Sorts the array in-place using selection sort, which has O(n^2) time
     * complexity. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a double array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a float array using insertion sort.
     * <p>
     * Sorts the array in-place using insertion sort, which has O(n^2) time
     * complexity but is efficient for small or nearly sorted arrays. Null arrays
     * are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a float array using selection sort.
     * <p>
     * Sorts the array in-place using selection sort, which has O(n^2) time
     * complexity. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a float array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a long array using insertion sort.
     * <p>
     * Sorts the array in-place using insertion sort, which has O(n^2) time
     * complexity but is efficient for small or nearly sorted arrays. Null arrays
     * are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a long array using selection sort.
     * <p>
     * Sorts the array in-place using selection sort, which has O(n^2) time
     * complexity. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a long array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort an int array using insertion sort.
     * <p>
     * Sorts the array in-place using insertion sort, which has O(n^2) time
     * complexity but is efficient for small or nearly sorted arrays. Null arrays
     * are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort an int array using selection sort.
     * <p>
     * Sorts the array in-place using selection sort, which has O(n^2) time
     * complexity. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort an int array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a short array using insertion sort.
     * <p>
     * Sorts the array in-place using insertion sort, which has O(n^2) time
     * complexity but is efficient for small or nearly sorted arrays. Null arrays
     * are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a short array using selection sort.
     * <p>
     * Sorts the array in-place using selection sort, which has O(n^2) time
     * complexity. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a short array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a byte array using insertion sort.
     * <p>
     * Sorts the array in-place using insertion sort, which has O(n^2) time
     * complexity but is efficient for small or nearly sorted arrays. Null arrays
     * are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a byte array using selection sort.
     * <p>
     * Sorts the array in-place using selection sort, which has O(n^2) time
     * complexity. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Sort a byte array using merge sort.
     * <p>
     * Sorts the array in-place using an optimized bottom-up merge sort with O(n log
     * n) time complexity. Uses insertion sort for small runs and ping-pong
     * buffering to reduce intermediate copies. Null arrays are ignored.
     * </p>
     *
     * @param arr the array to sort; may be {@code null}
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
     * Convert months to years and round to two decimal places.
     *
     * @param months number of months
     * @return equivalent years rounded to two decimal places
     */
    public static double toYears(int months) { return Math.round(months / 12.0 * 100.0) / 100.0; }

    /**
     * Print an object array to standard output using
     * {@link Arrays#toString(Object[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(Object[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a float array to standard output using
     * {@link Arrays#toString(float[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(float[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a double array to standard output using
     * {@link Arrays#toString(double[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(double[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a long array to standard output using {@link Arrays#toString(long[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(long[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print an int array to standard output using {@link Arrays#toString(int[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(int[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a short array to standard output using
     * {@link Arrays#toString(short[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(short[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a byte array to standard output using {@link Arrays#toString(byte[])}.
     *
     * @param arr the array to print; may be {@code null}
     */
    public static void print(byte[] arr) { System.out.println(Arrays.toString(arr)); }

    /**
     * Print a {@link List} to standard output using {@link #listStr(List)}.
     *
     * @param list the list to print; may be {@code null}
     */
    public static void print(List<?> list) { System.out.println(listStr(list)); }

    /**
     * Return a compact string representation of the provided {@link List}.
     *
     * <p>
     * The representation uses each element's {@code toString()} value and follows
     * the conventional list format (for example: {@code [a, b, c]}). Special cases
     * are handled explicitly: when {@code list} is {@code null} this method returns
     * the literal string {@code "null"}; when the list is empty this method returns
     * {@code "[]"}.
     * </p>
     *
     * @param list the list to convert to a string; may be {@code null}
     * @return a non-null string representation of the list
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
     * Fill the given integer array with pseudorandom values in the range
     * {@code [0, arr.length)}.
     *
     * <p>
     * This method mutates the provided array in-place. It uses a shared
     * {@link Random} instance and calls {@link Random#nextInt(int,int)} for each
     * slot.
     * </p>
     *
     * @param arr the array to fill; must not be {@code null}
     * @throws NullPointerException if {@code arr} is {@code null}
     * @implNote If {@code arr.length == 0} the method returns immediately and no
     *           random values are generated.
     */
    public static void fillRandomArray(int[] arr) {
        final int n = arr.length << 1;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = RAND.nextInt(0, n);
        }
    }

    /**
     * Create and return a new {@code int} array of the given length filled with
     * pseudorandom values in the range {@code [0, length)}.
     *
     * <p>
     * The returned array is newly allocated and populated using the shared
     * {@link Random} instance. Each element is produced by
     * {@link Random#nextInt(int,int)} with origin {@code 0} and bound equal to the
     * array length.
     * </p>
     *
     * @param length the length of the array to create; must be non-negative
     * @return a newly allocated array of length {@code length} containing
     *         pseudorandom values in {@code [0, length)}
     * @throws NegativeArraySizeException if {@code length} is negative
     * @implNote If {@code length == 0} an empty array is returned and no random
     *           values are generated.
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
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same element references as the original.
     * The copy is a shallow copy; elements themselves are not cloned.
     * </p>
     *
     * @param <T> the array element type
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     * @implNote The implementation performs an unchecked cast by converting an
     *           {@code Object[]} to {@code T[]}.
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
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same {@code long} values as the original.
     * </p>
     *
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static long[] copy(long[] arr) {
        long[] copy = new long[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same {@code int} values as the original.
     * </p>
     *
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static int[] copy(int[] arr) {
        int[] copy = new int[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same {@code short} values as the original.
     * </p>
     *
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static short[] copy(short[] arr) {
        short[] copy = new short[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same {@code byte} values as the original.
     * </p>
     *
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static byte[] copy(byte[] arr) {
        byte[] copy = new byte[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same {@code double} values as the
     * original.
     * </p>
     *
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static double[] copy(double[] arr) {
        double[] copy = new double[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of the given array.
     *
     * <p>
     * Returns a new array containing the same {@code float} values as the original.
     * </p>
     *
     * @param arr the source array to copy; must not be {@code null}
     * @return a new array containing the same elements in the same order
     * @throws NullPointerException if {@code arr} is {@code null}
     */
    public static float[] copy(float[] arr) {
        float[] copy = new float[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    /**
     * Create a shallow copy of the provided {@link List}.
     *
     * <p>
     * Returns a new {@link ArrayList} containing the same element references in the
     * same order. This is a shallow copy; elements themselves are not cloned. The
     * returned list is modifiable and independent of the source list.
     * </p>
     *
     * @param <T>  the list element type
     * @param list the source list to copy; must not be {@code null}
     * @return a new {@link ArrayList} containing the same elements in the same
     *         order
     * @throws NullPointerException if {@code list} is {@code null}
     */
    public static <T> List<T> copy(List<T> list) {
        var copy = new ArrayList<T>();
        for (int i = 0; i < list.size(); i++) {
            copy.add(list.get(i));
        }
        return copy;
    }

    /**
     * Swap two elements in an {@code double} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static void swap(double[] arr, int indexA, int indexB) {
        double temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in an {@code float} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static void swap(float[] arr, int indexA, int indexB) {
        float temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in an {@code long} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static void swap(long[] arr, int indexA, int indexB) {
        long temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in an {@code int} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static void swap(int[] arr, int indexA, int indexB) {
        int temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in an {@code short} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static void swap(short[] arr, int indexA, int indexB) {
        short temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in an {@code byte} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static void swap(byte[] arr, int indexA, int indexB) {
        byte temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a {@code T} array in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided array.
     * </p>
     *
     * @param arr    the array containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException           if {@code arr} is {@code null}
     * @throws ArrayIndexOutOfBoundsException if either index is out of range
     */
    public static <T> void swap(T[] arr, int indexA, int indexB) {
        T temp = arr[indexA];
        arr[indexA] = arr[indexB];
        arr[indexB] = temp;
    }

    /**
     * Swap two elements in a {@link java.util.List} in-place.
     *
     * <p>
     * Exchanges the elements at the specified indices; this method mutates the
     * provided list. The operation is type-safe and preserves the list's element
     * type {@code T}. The method is not thread-safe and external synchronization is
     * required when accessing the list concurrently.
     * </p>
     *
     * @param <T>    the element type of the list
     * @param list   the list containing the elements to swap; must not be
     *               {@code null}
     * @param indexA 0-based index of the first element to swap
     * @param indexB 0-based index of the second element to swap
     * @throws NullPointerException      if {@code list} is {@code null}
     * @throws IndexOutOfBoundsException if either index is out of range
     */
    public static <T> void swap(List<T> list, int indexA, int indexB) {
        list.set(indexA, list.set(indexB, list.get(indexA)));
    }

    /**
     * Measure the elapsed wall-clock time required to execute a {@link Runnable}.
     *
     * <p>
     * Executes the supplied {@code Runnable} synchronously and returns the elapsed
     * time in nanoseconds using {@link System#nanoTime()}.
     * </p>
     *
     * @param runnable the task to execute; may be {@code null}
     * @return elapsed time in nanoseconds, or {@code 0} if {@code runnable} is
     *         {@code null}
     * @implNote Any exception thrown by the supplied {@code Runnable} is propagated
     *           to the caller.
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
     * Measure the elapsed wall-clock time to execute a {@link Supplier} and return
     * both the elapsed time (in nanoseconds) and the result.
     *
     * @param <T>      result type
     * @param supplier the task to execute; may be {@code null}
     * @return Object[] containing {nanos, result}, or {0, null} if supplier is null
     * @implNote Any exception thrown by the supplier is propagated to the caller.
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
     * Convert a duration in nanoseconds to milliseconds.
     *
     * @param nanos the duration in nanoseconds
     * @return the duration in milliseconds as a {@code long}
     */
    public static long nanosToMillis(long nanos) { return nanos / 1000000; }

    /**
     * Convert a duration in nanoseconds (as a double) to milliseconds.
     *
     * @param nanos the duration in nanoseconds
     * @return the duration in milliseconds as a {@code double}
     */
    public static double nanosToMillis(double nanos) { return nanos / 1000000.0; }

    /**
     * Convert nanoseconds to milliseconds and round to two decimal places.
     *
     * @param nanos the duration in nanoseconds
     * @return the rounded duration in milliseconds as a {@code double}
     */
    public static double roundedNanosToMillis(long nanos) { return roundTwoDecimals(nanos / 1000000.0); }

    /**
     * Convert nanoseconds (as a double) to milliseconds and round to two decimal
     * places.
     *
     * @param nanos the duration in nanoseconds
     * @return the rounded duration in milliseconds as a {@code double}
     */
    public static double roundedNanosToMillis(double nanos) { return roundTwoDecimals(nanos / 1000000.0); }

    /**
     * Return the arithmetic average of the provided {@code Number} values.
     *
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the arithmetic average as a {@code Number}, or {@code 0} when the
     *         list is {@code null} or empty
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
     * Return the arithmetic average of the provided {@code Double} values.
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the arithmetic average as a {@code double}, or {@code 0} when the
     *         list is {@code null} or empty
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
     * Return the arithmetic average of the provided {@code Float} values.
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the arithmetic average as a {@code double}, or {@code 0} when the
     *         list is {@code null} or empty
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
     * Return the rounded average of the provided {@code Long} values.
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the rounded {@code long} average, or {@code 0} when the list is
     *         {@code null} or empty
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
     * Return the rounded average of the provided {@code Integer} values.
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the rounded {@code int} average, or {@code 0} when the list is
     *         {@code null} or empty
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
     * Return the rounded average of the provided {@code Short} values.
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the rounded {@code short} average, or {@code 0} when the list is
     *         {@code null} or empty
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
     * Return the rounded average of the provided {@code Byte} values.
     *
     * @param list values to average; may be {@code null} or empty. Ignores
     *             {@code null} elements.
     * @return the rounded {@code byte} average, or {@code 0} when the list is
     *         {@code null} or empty
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
     * @return the number rounded to two decimal places
     */
    public static double roundTwoDecimals(double num) { return Math.round(num * 100.0) / 100.0; }

    /**
     * Returns {@code true} if the given string can be parsed as an {@link Integer}.
     *
     * @param in the string to test; may be {@code null}
     * @return {@code true} if {@code in} represents a valid integer, {@code false}
     *         otherwise
     */
    public static boolean isInt(String in) { return in != null && isIntFast(in); }

    /**
     * Returns {@code true} if the given string can be parsed as a {@link Double}.
     *
     * @param in the string to test; may be {@code null}
     * @return {@code true} if {@code in} represents a valid double, {@code false}
     *         otherwise
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
     * Fast check whether a string contains a valid double representation.
     * <p>
     * This helper assumes callers have already checked for {@code null}.
     * </p>
     *
     * @param in the input string to test (must be non-null)
     * @return {@code true} if {@code in} parses as a double, {@code false}
     *         otherwise
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
     * Returns {@code true} if the input is non-empty and is not a number (neither
     * {@link #isInt(String)} nor {@link #isDouble(String)}).
     *
     * @param in the string to test; may be {@code null}
     * @return {@code true} when {@code in} is non-null, non-empty, and not numeric
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
     * Prompt the user with {@code inputMessage} until a valid double is entered.
     *
     * @param input        the {@link Scanner} to read user input from
     * @param inputMessage the prompt message printed to standard output; may be
     *                     {@code null}
     * @return the parsed {@code double} entered by the user
     * @throws IllegalArgumentException if {@code input} is {@code null}
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
     * Prompt the user with {@code inputMessage} until a valid integer is entered.
     *
     * @param input        the {@link Scanner} to read user input from
     * @param inputMessage the prompt message printed to standard output; may be
     *                     {@code null}
     * @return the parsed {@code int} entered by the user
     * @throws IllegalArgumentException if {@code input} is {@code null}
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
     * Prompt the user with {@code inputMessage} until a non-numeric string is
     * entered.
     *
     * @param input        the {@link Scanner} to read user input from
     * @param inputMessage the prompt message printed to standard output; may be
     *                     {@code null}
     * @return the validated non-numeric string entered by the user
     * @throws IllegalArgumentException if {@code input} is {@code null}
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