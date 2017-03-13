package parallel_sorting;

/*
 * Created by WhatNick on 17.08.2016.
 * 18:40
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ParallelSorting implements Paralleling {
    private volatile int countOfThreads; // Count of threads
    private volatile int[][] arrayOfArrays; // Array of arrays
    private volatile int countingThread = 0; // Count of realtime thread
    private volatile int maximum = 0; // Maximum of original array
    private volatile int minimum = 0; // Minimum of original array
    private volatile int pieceOfDistance; // Piece of distance
    private volatile int[] array; // Copy of original array
    private volatile int lengthVariable; // Length of original array
    private volatile CountDownLatch countDawnLatch; // Count of threads
    private volatile long maxSortTime = 0;

    /*
     * Constructor
     * Parameter = count of threads
     * v1.01
     */
    ParallelSorting(int countOfThreads) {
        this.countOfThreads = countOfThreads;
        arrayOfArrays = new int [countOfThreads][];
        this.countDawnLatch = new CountDownLatch(countOfThreads);
    }

    /*
     * Adding to the array of array all elements
     * which satisfy the range of the variable "pieceOfDistance"
     * Synchronizing used, because Lock are not important here, coz count of
     *  calculating threads <=10
     * Semaphore's time: 3237-7520
     * Sync's time: 1940-4677
     * Lock's time: 2080-6280
     * v1.05
     */
    public synchronized int[] toCreatePiece(int[] mas, int piece, int countThread) {

        int localMin = minimum;
        this.minimum += piece;

        List<Integer> ar = new ArrayList<>();
        int tempMinimum = localMin + piece;

        for (int ma : mas) {
            if (countThread == countOfThreads - 1) {
                if (ma < localMin || ma > maximum) {
                    continue;
                }
                ar.add(ma);
            } else if (ma >= localMin && ma < tempMinimum) {
                ar.add(ma);
            }
        }

        int[] finalAr = new int[ar.size()];
        for (int i = 0;i<ar.size();i++) {
            finalAr[i] = ar.get(i);
        }

        /*
         * Use this code if need to debug
         * Displays information of current thread
        System.out.println(new Date() + "_номер потока: " + countThread + "_array length: " + finalAr.length);
        System.out.println("Minimum: " + this.minimum + " TempMinimum: "  + tempMinimum);
        System.out.println();
        */

        return finalAr;
    }

    /*
     * Adds a sorted array in to the array of arrays
     * Variable "a" should to assign ID of current thread
     * But seems that it's not working...
     * v1.02
     */
    public void toAddPieceOfArray(int[] pieceOfAr, int a) {
        arrayOfArrays[a] = new int[pieceOfAr.length];
        System.arraycopy(pieceOfAr, 0, arrayOfArrays[a], 0, pieceOfAr.length);
    }

    /*
     * Searching min and max in original array
     * Initialising important variables
     *
     * With timer
     * v1.02
     */
    public void getMaxAndMin(int[] mas) {
        Date d1 = new Date();
        int max = mas[0];
        int min = mas[0];

        for (int anMas : mas) {
            if (max < anMas) {
                max = anMas;
            } else if (min > anMas) {
                min = anMas;
            }
        }

        // Initialising important variables
        maximum = max;
        minimum = min;
        int distance;

        if (min > 0) {
            distance = (max - min) + 1;
        } else {
            distance = Math.abs(max) + Math.abs(min);
        }
        pieceOfDistance = distance / countOfThreads;
        Date d2 = new Date();
        // Displays information
        System.out.println("Maximum: " + maximum + ", minimum: " + minimum + ", distance: " + distance
                + ", piece of distance: " + pieceOfDistance +
                    "\r\n" + "\r\n" + "Finding min and max time is: " + (d2.getTime()-d1.getTime()) + "ms.");
    }

    /*
     * Quick sorting
     * v1.01
     */
    public void quickSorting(int[] mas, int start, int end) {
        if (start >= end)
            return;
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (mas[i] <= mas[cur])) {
                i++;
            }
            while (j > cur && (mas[cur] <= mas[j])) {
                j--;
            }
            if (i < j) {
                int temp = mas[i];
                mas[i] = mas[j];
                mas[j] = temp;
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        quickSorting(mas,start, cur);
        quickSorting(mas,cur+1, end);
    }

    /*
     * Start method
     * In the beginning it's finds max and min values in an array
     * Initialising important variables
     * Call to need count of threads
     * Waiting, while all of threads is not finished
     * Returns a sorted array
     *
     * Added Semaphore in paralleling threads
     * v1.03
     */
    public int[] toSort(int[] mas)
    {
        try {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            getMaxAndMin(mas);
            array = new int[mas.length];
            this.lengthVariable = mas.length;
            System.arraycopy(mas, 0, array, 0, mas.length);

            Date d1 = new Date();
            for (int i = 0; i < countOfThreads; i++) {
                new Paralleling(countDawnLatch).start();
            }

            countDawnLatch.await();
            Date d2 = new Date();
            maxSortTime = d2.getTime()-d1.getTime();

        } catch (InterruptedException e) {
            System.out.println("Error in await method!");
            e.printStackTrace();
        }

        return sortingArraysAndRelease();
    }

    /*
     * Sorts array of arrays
     *
     * With timer
     * v1.03
     */
    public int[] sortingArraysAndRelease() {
        Date d1 = new Date();
        int[] finalArray = new int[this.lengthVariable];
        int finalArrayKey = 0;
        int a1;
        int cor = 0;
        int[] temp;
        int[] temp2;
        boolean flag = false;

        for (int i = 0;i < arrayOfArrays.length;i++) {
            a1 = arrayOfArrays[i][0];

            for (int j = i + 1; j < arrayOfArrays.length; j++) {
                if (a1 > arrayOfArrays[j][0]) {
                    a1 = arrayOfArrays[j][0];
                    cor = j;
                    flag = true;
                }
            }

            if (flag) {
                temp = arrayOfArrays[i];
                temp2 = arrayOfArrays[cor];
                arrayOfArrays[i] = temp2;
                arrayOfArrays[cor] = temp;
                flag = false;
            }
        }

        for (int[] arrayOfArray : arrayOfArrays) {
            for (int anArrayOfArray : arrayOfArray) {
                finalArray[finalArrayKey] = anArrayOfArray;

                if (finalArrayKey < this.lengthVariable - 1) {
                    finalArrayKey++;
                }
            }
        }

        Date d2 = new Date();
        System.out.println("Max. sorting time is: " + maxSortTime + "ms.");
        System.out.println("Release time is: " + (d2.getTime()-d1.getTime()) + "ms.");
        return finalArray;
    }

    /*
     * Increments the value of countingThread
     * v1.02
     */
    public synchronized int toIncrementCountingThread() {
        return countingThread++;
    }

    /*
     * Thread for parallel sorting
     * v1.04
     */
    private class Paralleling extends Thread {

        private int a;
        private CountDownLatch cDL;

        Paralleling(CountDownLatch cDL) {
            this.cDL = cDL;
        }

        @Override
        public void run() {
            a = toIncrementCountingThread();
            int[] massiv = toCreatePiece(array,pieceOfDistance,a);

            /*
             * Quick sorting

            */
            int startIndex = 0;
            int endIndex = massiv.length - 1;
            quickSorting(massiv,startIndex, endIndex);
            //Arrays.sort(massiv);

            toAddPieceOfArray(massiv,a);

            this.cDL.countDown();
        }
    }
}




