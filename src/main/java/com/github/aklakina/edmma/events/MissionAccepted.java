package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import org.json.JSONObject;

public class MissionAccepted extends Event {

    /*{ "timestamp":"2021-03-10T14:58:54Z"
     *  , "event":"MissionAccepted"
     *  , "Faction":"People's MET 20 Liberals"
     *  , "Name":"Mission_MassacreWing"
     *  , "LocalisedName":"Massacre Qi Yomisii Council faction Pirates"
     *  , "TargetType":"$MissionUtil_FactionTag_Pirate;"
     *  , "TargetType_Localised":"Pirates"
     *  , "TargetFaction":"Qi Yomisii Council"
     *  , "KillCount":20
     *  , "DestinationSystem":"Qi Yomisii"
     *  , "DestinationStation":"Seddon Terminal"
     *  , "Expiry":"2021-03-17T14:26:09Z"
     *  , "Wing":true
     *  , "Influence":"++"
     *  , "Reputation":"++"
     *  , "Reward":2428060
     *  , "MissionID":726287843 }
     */

    Integer missionID;
    String sourceFaction;
    String targetFaction;
    String targetSystem;
    String expiry;
    boolean winged;
    double reward;

    public MissionAccepted(JSONObject json) {
        missionID = json.getInt("MissionID");
        sourceFaction = json.getString("Faction");
        targetFaction = json.getString("TargetFaction");
        targetSystem = json.getString("DestinationSystem");
        expiry = json.getString("Expiry");
        winged = json.getBoolean("Wing");
        reward = json.getDouble("Reward")/1000000.0;
    }

    @Override
    public void run() {
        System.out.println("Mission accepted: " + this);
    }
}
