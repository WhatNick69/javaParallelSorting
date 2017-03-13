package parallel_sorting;

import javax.sound.sampled.LineUnavailableException;
import java.util.Date;
import java.util.Random;

/*
 * Created by WhatNick on 17.08.2016.
 * 18:40
 *
 * Average loading CPU on FX-8300 is: 65%
 * Speed for 50kk elements: 6009 - 13190ms.
 */

public class Mainer {
    public static void main(String[] args) throws InterruptedException, LineUnavailableException {
        // Initializing of array
        int n = Runtime.getRuntime().availableProcessors();
        int masLength = 50000000;
        Random rnd = new Random();
        int errors = 0;
        Date d1;
        Date d2;
        int uncorrectIterations = 0;
        int correctIterations = 0;
        boolean flag = false;
        long minTime = Long.MAX_VALUE ;
        long maxTime = Long.MIN_VALUE;
        int threaads = 1;

        while (correctIterations < 5) {
            System.out.println("=======================================================================");
            int[] mas = new int[masLength];

            for (int i = 0;i<mas.length;i++) {
                mas[i] = rnd.nextInt(Integer.MAX_VALUE)-(Integer.MAX_VALUE/2);
            }

            // Call of sorting
            d1 = new Date();
            mas = new ParallelSorting(n).toSort(mas);
            d2 = new Date();
            if (d2.getTime()-d1.getTime() < minTime) {
                minTime = d2.getTime()-d1.getTime();
                threaads = n;
            }

            if (d2.getTime()-d1.getTime() > maxTime) {
                maxTime = d2.getTime()-d1.getTime();
            }

            for (int i = 0; i < mas.length - 2; i++) {
                if (mas[i] > mas[i + 1]) {
                    flag = true;
                    System.out.println("________________________________________________");
                    System.out.println(mas[i] + "_: "+i+" :ERROR: "+(i+1)+" :_" + mas[i+1]);
                    System.out.println("________________________________________________");
                    errors++;
                }
            }

            if (flag) {
                uncorrectIterations++;
                System.out.println("\r\n" + "Count of threads " + n + ", length of array: " + mas.length);
                System.out.println("The sorting was completed INCORRECTLY! Count of errors: " + errors +
                        "\r\n" + "Count of correct iterations: " + correctIterations +
                            "\r\n" + "Count of incorrect iterations: " + uncorrectIterations);

                System.out.println("Time of sorting with " + n + " thread(s): " + (d2.getTime() - d1.getTime()) + "ms.");
                System.out.println("Length of array: " + mas.length);
                beep(flag);
                break;
            }  else {
                correctIterations++;
                System.out.println("\r\n" + "Count of threads " + n + ", length of array: " + mas.length);
                System.out.println("The sorting " + correctIterations + " was completed correctly!");
                System.out.println("Time of sorting with " + n + " thread(s): " + (d2.getTime() - d1.getTime()) + "ms.");
                beep(flag);
            }
        }

        System.out.println("=======================================================================");
        System.out.println("Correct iterations: " + correctIterations);
        System.out.println("Uncorrect iterations: " + uncorrectIterations);
        System.out.println("Time is: " + minTime + " - " + maxTime + "ms., with " + threaads + " threads.");
    }

    static void beep(boolean flag) throws LineUnavailableException, InterruptedException {
        if (flag) {
            for (int i = 0;i<3;i++) {
                SoundUtils.tone(5000, 100);
                Thread.sleep(100);
            }
        } else {
            SoundUtils.tone(300,250);
            Thread.sleep(250);
        }
    }
}
