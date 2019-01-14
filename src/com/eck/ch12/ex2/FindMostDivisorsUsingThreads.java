package com.eck.ch12.ex2;

/**
 * This program finds the number, within a specified range of integers from 1 to
 * whatever positive integer greater than one is chosen, with the most divisors.
 * This program only shows one number whether or not there is more
 * than one number with the specified greatest number of divisors.
 * The user can choose how many threads will divide the task.
 * 
 * @author toddnordquist September 15, 2018
 *
 */

public class FindMostDivisorsUsingThreads {

	/**
	 * The starting and ending point for the range of integers that are tested
	 * for number of divisors.
	 *
	 */

	private static final int START = 0;
	private static final int END = 100000;

	private static volatile int maxDivisors; // Maximum number of divisors
	// seen so

	// far.
	private static volatile int numWithMax = 0; // A value of N that had the
												// given number of divisors.

	/**
	 * This method compares the greatest number of divisors and its number up to
	 * this point in the program with each thread's greatest number of divisors
	 * and its number. This method is synchronized because it's possible for two
	 * or more threads to call this method at relatively the same time.
	 * 
	 * @param currentMaxDivisors
	 *            an individual thread's greatest number of divisors
	 * @param currentNumWithMaxDivisors
	 *            an individual thread's number from the range of numbers
	 *            assigned to it that has the greatest number of divisors.
	 */
	synchronized private static void updateDivisor(
			int currentMaxDivisors, int currentNumWithMaxDivisors) {
		if (currentMaxDivisors > maxDivisors) {
			maxDivisors = currentMaxDivisors;
			numWithMax = currentNumWithMaxDivisors;
		}

	}

	/**
	 * A Thread belonging to this class will count primes in a specified range
	 * of integers. The range is from min to max, inclusive, where min and max
	 * are given as parameters to the constructor. After counting, the thread
	 * outputs a message about the number of primes that it has found, and it
	 * adds its count to the overall total by calling the addToTotal(int)
	 * method.
	 */
	private static class CountDivisorsThread extends Thread {
		int min, max;
		int count = 0;
		int N = 0;
		int divisorsForGivenNum;
		int currentMaxDivisors;
		int currentNumWithMaxDivisors;

		public CountDivisorsThread(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public void run() {
			for (N = min; N <= max; N++) {
				divisorsForGivenNum = countDivisors(N);
				if (divisorsForGivenNum > currentMaxDivisors) {
					currentMaxDivisors = divisorsForGivenNum;
					currentNumWithMaxDivisors = N;
				}

			}

			updateDivisor(currentMaxDivisors,
					currentNumWithMaxDivisors);
		} // end run()
	}

	/**
	 * Counts the number of divisors in the range from (start+1) to (END), using
	 * a specified number of threads. The total elapsed time is printed. Note
	 * that
	 * 
	 * @param numberOfThreads
	 */
	private static void countMostDivisors(int numberOfThreads) {
		int increment = END / numberOfThreads;
		System.out.println("\nCounting primes between "
				+ (START + 1) + " and " + (END) + " using "
				+ numberOfThreads + " threads...\n");
		long startTime = System.currentTimeMillis();
		CountDivisorsThread[] worker = new CountDivisorsThread[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++)
			worker[i] = new CountDivisorsThread(START + i
					* increment + 1, START + (i + 1) * increment);
		maxDivisors = 0;
		for (int i = 0; i < numberOfThreads; i++)
			worker[i].start();
		for (int i = 0; i < numberOfThreads; i++) {
			while (worker[i].isAlive()) {
				try {
					worker[i].join();
				} catch (InterruptedException e) {

				}
			}
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out
				.println("Among integers between 1 and 100000,");
		System.out.println("The maximum number of divisors is "
				+ maxDivisors);
		System.out.println("A number with " + maxDivisors
				+ " divisors is " + numWithMax);
		System.out.println("\nTotal elapsed time:  "
				+ (elapsedTime / 1000.0) + " seconds.\n");
	}

	public static void main(String[] args) {

		int processors = Runtime.getRuntime()
				.availableProcessors();
		if (processors == 1)
			System.out
					.println("Your computer has only 1 available processor.\n");
		else
			System.out.println("Your computer has " + processors
					+ " available processors.\n");

		int numberOfThreads = 0;
		while (numberOfThreads < 1 || numberOfThreads > 100) {
			System.out
					.print("How many threads do you want to use  (from 1 to 100) ?  ");
			numberOfThreads = TextIO.getlnInt();
			if (numberOfThreads < 1 || numberOfThreads > 100)
				System.out
						.println("Please choose a number 1 through "
								+ processors + "!");
		}
		countMostDivisors(numberOfThreads);
	} // end main()

	/**
	 * Count the divisors between min and max, inclusive for a given integer.
	 * 
	 * @param num
	 *            the integer to be tested for its number of divisors.
	 * @return an int that is the number of divisors for the passed number.
	 */

	private static int countDivisors(int num) {

		// int N; // One of the integers whose divisors we have to count.
		// N = num;
		int divisorCount;
		/*
		 * Process all the remaining values of N from 2 to 100000, and update
		 * the values of maxDivisors and numWithMax whenever we find a value of
		 * N that has more divisors than the current value of maxDivisors.
		 */

		int D; // A number to be tested to see if it's a divisor of N.
		divisorCount = 0; // Number of divisors of N.
		for (D = 1; D <= num; D++) { // Count the divisors of N.
			if (num % D == 0)
				divisorCount++;

		}
		return divisorCount;
	}

} // end main class