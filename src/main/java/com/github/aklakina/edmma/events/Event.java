package com.github.aklakina.edmma.events;

import org.json.JSONObject;

public interface Event extends Runnable {
    // Somehow I need to enforce the implementation of a constructor that takes a JSONObject
    // and the registration of the event type with the DataFactory

}
