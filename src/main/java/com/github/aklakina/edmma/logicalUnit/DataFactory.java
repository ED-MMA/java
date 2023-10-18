package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.events.Event;
import org.json.JSONObject;

import java.util.TreeMap;
import java.util.function.Function;

@Singleton
public class DataFactory {
    private static TreeMap<String, Function<JSONObject,Event>> eventFactories = new TreeMap<>();

    public boolean registerEventFactory(String eventType, Function<JSONObject,Event> eventFactory) {
        if (eventFactories.containsKey(eventType)) {
            return false;
        }
        eventFactories.put(eventType, eventFactory);
        return true;
    }

    public boolean spawnEvent(JSONObject event) {
        String eventType = event.getString("event");
        Function<JSONObject,Event> eventFactory = eventFactories.get(eventType);
        if (eventFactory == null) {
            return false;
        }
        return SingletonFactory.getSingleton(EventHandler.class).addEvent(eventFactory.apply(event));
    }

}
