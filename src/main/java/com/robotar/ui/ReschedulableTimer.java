package com.robotar.ui;

import java.util.Timer;
import java.util.TimerTask;

public class ReschedulableTimer extends Timer {
	private Runnable task;
	private TimerTask timerTask;

	public void schedule(Runnable runnable, long delay) {
		task = runnable;
		timerTask = new TimerTask() {
			public void run() {
				task.run();
			};
		};
		schedule(timerTask, delay);
	}

	public void reschedule(long delay) {
		timerTask.cancel();
		timerTask = new TimerTask() {
			public void run() {
				task.run();
			};
		};
		schedule(timerTask, delay);
	}
}