package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class EventHandler implements Runnable {

    private final LinkedBlockingQueue<JSONObject> eventsToProcess = new LinkedBlockingQueue<>();

    public void addEvent(JSONObject event) {
        System.out.println("Event: " + event);
        eventsToProcess.offer(event);
    }

    @Override
    public void run() {
        for (; ; ) {
            while (!eventsToProcess.isEmpty()) {
                JSONObject event = null;
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
