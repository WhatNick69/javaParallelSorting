package parallel_sorting;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by WhatNick on 18.08.2016.
 */
 class QuickSortExample {
    public static int ARRAY_LENGTH = 100000000;
    private static int[] array = new int[ARRAY_LENGTH];
    private static Random generator = new Random();
    private static long bestTime = Long.MAX_VALUE;
    public static void initArray() {
        for (int i=0; i<ARRAY_LENGTH; i++) {
            array[i] = generator.nextInt(10000)-5000;
        }
    }

    public static void quickSort() {
        int startIndex = 0;
        int endIndex = ARRAY_LENGTH - 1;
        doSort(startIndex, endIndex);
    }

    private static void doSort(int start, int end) {
        if (start >= end)
            return;
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (array[i] <= array[cur])) {
                i++;
            }
            while (j > cur && (array[cur] <= array[j])) {
                j--;
            }
            if (i < j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        doSort(start, cur);
        doSort(cur+1, end);
    }

    public static void main(String[] args) {
        for (int i = 0;i<1;i++) {
            Date d1 = new Date();
            initArray();
            quickSort();
            //Arrays.sort(array);
            Date d2 = new Date();
            long tempTime = d2.getTime() - d1.getTime();
            if (tempTime < bestTime) bestTime = tempTime;
        }
        System.out.println("Best time is: " + bestTime + "ms.");
    }
}
