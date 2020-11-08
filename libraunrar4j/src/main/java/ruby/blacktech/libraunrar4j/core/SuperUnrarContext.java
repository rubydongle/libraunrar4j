package ruby.blacktech.libraunrar4j.core;

import java.util.concurrent.ExecutorService;

public class SuperUnrarContext {

    private class UnRarRunnable implements Runnable {
        UnrarTask mUnrarTask;
        UnRarRunnable(UnrarTask task) {
            mUnrarTask = task;
        }

        @Override
        public void run() {
            mUnrarTask.startExtract();
        }
    }

    public void startExtract(ExecutorService threadPool, UnrarTask task) {
        threadPool.execute(new UnRarRunnable(task));
    }

    public void startExtract(UnrarTask task) {
        new Thread(new UnRarRunnable(task)).start();
    }
}
