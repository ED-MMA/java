package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.events.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.TreeMap;

/**
 * Singleton class responsible for managing events.
 * It registers event factories and spawns events.
 */
@Singleton
public class DataFactory {

    // Logger instance for this class
    private final Logger logger = LogManager.getLogger(DataFactory.class);
    // Map to store event factories
    private static final TreeMap<String, Constructor<? extends Event>> eventFactories = new TreeMap<>();

    /**
     * Registers an event factory.
     * @param eventType The type of the event.
     * @param eventFactory The factory of the event.
     * @return true if the event factory is registered successfully, false otherwise.
     */
    public boolean registerEventFactory(String eventType, Constructor<? extends Event> eventFactory) {
        if (eventFactories.containsKey(eventType)) {
            return false;
        }
        eventFactories.put(eventType, eventFactory);
        return true;
    }

    /**
     * Spawns an event.
     * @param event The event to be spawned.
     * @return true if the event is spawned successfully, false otherwise.
     */
    public boolean spawnEvent(JSONObject event) {
        String eventType = event.getString("event");
        Constructor<? extends Event> eventFactory = eventFactories.get(eventType);
        if (eventFactory == null) {
            return false;
        }
        try {
            return SingletonFactory.getSingleton(EventHandler.class).addEvent(eventFactory.newInstance(event));
        } catch (Exception e) {
            logger.debug("Error spawning event: " + eventType);
            logger.error("Error: " + e.getMessage());
            logger.trace(e.getStackTrace());
            return false;
        }
    }

}