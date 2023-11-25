package com.github.aklakina.edmma.logicalUnit.threading;

/**
 * Enum representing different methods to close a thread.
 */
public enum CloserMethods {
    /**
     * The NOTIFY method.
     * This method will notify the thread and then join it.
     */
    NOTIFY {
        @Override
        public void execute(RegisteredThread thread) throws InterruptedException {
            synchronized (thread) {
                // Notify the thread
                thread.notify();
            }
            // Join the thread
            thread.join();
        }
    },
    /**
     * The INTERRUPT method.
     * This method will interrupt the thread and then join it.
     */
    INTERRUPT {
        @Override
        public void execute(RegisteredThread thread) throws InterruptedException {
            // Interrupt the thread
            thread.interrupt();
            // Join the thread
            thread.join();
        }
    };

    /**
     * Abstract method to be implemented by each enum value.
     * This method will execute the specific action on the thread.
     *
     * @param thread the thread to be acted upon
     * @throws InterruptedException if any thread has interrupted the current thread
     */
    public abstract void execute(RegisteredThread thread) throws InterruptedException;
}