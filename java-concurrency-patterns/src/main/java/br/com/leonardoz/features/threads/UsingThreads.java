package br.com.leonardoz.features.threads;

public class UsingThreads {

	public static void logState(Thread thread, String label) {
		System.out.println(label + " -> " +
				thread.getName() +
				" state: " +
				thread.getState());
	}

	public static void main(String[] args) throws InterruptedException {

		// =========================
		// Creating thread
		// =========================
		var created = new Thread();

		logState(created, "Before start");

		created.start();

		logState(created, "After start");

		created.join();

		logState(created, "After join");


		// =========================
		// Thread with task
		// =========================
		var threadWithTask = new Thread(() -> {
			System.out.println("Inside thread: " +
					Thread.currentThread().getName());

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		logState(threadWithTask, "Task thread before start");

		threadWithTask.start();

		logState(threadWithTask, "Task thread after start");

		Thread.sleep(500);

		logState(threadWithTask, "Task thread during sleep");

//		main thread wait till threadWithTask done
		threadWithTask.join();

		logState(threadWithTask, "Task thread after finish");


		// =========================
		// Interrupting thread
		// =========================
		Runnable interruptiblyTask = () -> {

			while (!Thread.currentThread().isInterrupted()) {
				System.out.println(
						"Running: " +
								Thread.currentThread().getName()
				);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {

					System.out.println("Interrupted during sleep");

					// restore interrupted flag
					Thread.currentThread().interrupt();
				}
			}

			System.out.println("Thread stopped");
		};

		var interruptable = new Thread(interruptiblyTask);

		logState(interruptable, "Interrupt thread before start");

		interruptable.start();

		logState(interruptable, "Interrupt thread after start");

		Thread.sleep(2000);

		logState(interruptable, "Before interrupt");

		interruptable.interrupt();

		logState(interruptable, "After interrupt");

		interruptable.join();

		logState(interruptable, "After termination");
	}
}