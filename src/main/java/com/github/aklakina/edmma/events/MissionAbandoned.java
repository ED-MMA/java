package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import org.json.JSONObject;

public class MissionAbandoned extends Event {
    /*
    {
      "event": "MissionAbandoned",
      "MissionID": 726287843
    }
     */

    private final long missionId;

    public MissionAbandoned(JSONObject json) {
        missionId = json.getLong("MissionID");
    }

    @Override
    public void run() {

    }
}
