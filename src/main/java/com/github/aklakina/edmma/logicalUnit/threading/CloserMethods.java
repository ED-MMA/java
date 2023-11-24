package com.github.aklakina.edmma.logicalUnit.threading;

public enum CloserMethods {
    NOTIFY {
        @Override
        public void execute(RegisteredThread thread) {
            synchronized (thread) {
                thread.notify();
            }
        }
    },
    INTERRUPT {
        @Override
        public void execute(RegisteredThread thread) {
            thread.interrupt();
        }
    };

    public abstract void execute(RegisteredThread thread);
}