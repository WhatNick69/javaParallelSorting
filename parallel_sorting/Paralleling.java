package parallel_sorting;

/*
 * Created by WhatNick on 17.08.2016.
 * 18:40
 */

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

interface Paralleling {
    int[] toCreatePiece(int[] mas, int piece, int countThread);

    void toAddPieceOfArray(int[] pieceOfAr, int a);

    void getMaxAndMin(int[] mas);

    void quickSorting(int[] mas, int start, int end);

    int[] toSort(int[] mas);

    int[] sortingArraysAndRelease();

    int toIncrementCountingThread();
}
