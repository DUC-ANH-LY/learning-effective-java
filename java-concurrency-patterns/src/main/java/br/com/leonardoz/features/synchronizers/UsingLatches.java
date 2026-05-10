package br.com.leonardoz.features.synchronizers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UsingLatches {

	static void log(String msg) {

		System.out.printf(
				"[%d] [%s] %s%n",
				System.currentTimeMillis() % 100000,
				Thread.currentThread().getName(),
				msg
		);
	}

	public static void main(String[] args)
			throws Exception {

		var executor =
				Executors.newCachedThreadPool();

		var latch =
				new CountDownLatch(3);

		Runnable r = () -> {

			try {

				log("Task started");

				Thread.sleep(1000);

				log("Before countDown: "
						+ latch.getCount());

				latch.countDown();

				log("After countDown: "
						+ latch.getCount());

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};

		executor.execute(r);
		executor.execute(r);
		executor.execute(r);

		log("Main waiting on latch");

//		wait until count down to 0
		boolean completed =
				latch.await(2, TimeUnit.SECONDS);

		log("Latch released: " + completed);

		executor.shutdown();
	}
}