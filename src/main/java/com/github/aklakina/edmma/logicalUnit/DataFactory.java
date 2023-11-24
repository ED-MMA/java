package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.events.Event;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.TreeMap;

@Singleton
public class DataFactory {
    private static final TreeMap<String, Constructor<? extends Event>> eventFactories = new TreeMap<>();

    public boolean registerEventFactory(String eventType, Constructor<? extends Event> eventFactory) {
        if (eventFactories.containsKey(eventType)) {
            return false;
        }
        eventFactories.put(eventType, eventFactory);
        return true;
    }

    public boolean spawnEvent(JSONObject event) {
        String eventType = event.getString("event");
        Constructor<? extends Event> eventFactory = eventFactories.get(eventType);
        if (eventFactory == null) {
            return false;
        }
        try {
            return SingletonFactory.getSingleton(EventHandler.class).addEvent(eventFactory.newInstance(event));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
