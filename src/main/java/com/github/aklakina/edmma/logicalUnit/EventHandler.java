package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.events.Event;
import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class EventHandler implements Runnable {

    private final LinkedBlockingQueue<Event> eventsToProcess = new LinkedBlockingQueue<>();

    public boolean addEvent(Event event) {
        System.out.println("Event: " + event);
        return eventsToProcess.offer(event);
    }

    @Override
    public void run() {
        for (; ; ) {
            while (!eventsToProcess.isEmpty()) {
                Event event = null;
                try {
                    event = eventsToProcess.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Processing event: " + event);
            }
        }
    }
}
