package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.events.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class EventHandler implements Runnable {

    public boolean shouldExit = false;

    private final LinkedBlockingQueue<Event> eventsToProcess = new LinkedBlockingQueue<>();

    public boolean addEvent(Event event) {
        System.out.println("Event: " + event);
        return eventsToProcess.offer(event);
    }

    // Create a thread pool for event processing
    //private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void run() {
        do {
            Event event = null;
            try {
                event = eventsToProcess.take();
            } catch (InterruptedException e) {
                shouldExit = true;
                break;
            }
            System.out.println("Processing event: " + event);
            event.run();

        } while (!shouldExit);
    }
}
