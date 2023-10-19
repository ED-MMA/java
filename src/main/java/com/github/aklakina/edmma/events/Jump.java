package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import org.json.JSONObject;

public class Jump extends Event {
    /* { "timestamp":"2022-04-21T20:05:12Z"
     * , "event":"FSDJump"
     * , "StarSystem":"Pegasi Sector XU-O b6-4"
     * , "SystemAddress":9465168209329
     * , "StarPos":[-129.56250,-75.81250,15.78125]
     * , "SystemAllegiance":""
     * , "SystemEconomy":"$economy_None;"
     * , "SystemEconomy_Localised":"None"
     * , "SystemSecondEconomy":"$economy_None;"
     * , "SystemSecondEconomy_Localised":"None"
     * , "SystemGovernment":"$government_None;"
     * , "SystemGovernment_Localised":"None"
     * , "SystemSecurity":"$GAlAXY_MAP_INFO_state_anarchy;"
     * , "SystemSecurity_Localised":"Anarchy"
     * , "Population":0
     * , "Body":"Pegasi Sector XU-O b6-4"
     * , "BodyID":0
     * , "BodyType":"Star"
     * , "JumpDist":19.757
     * , "FuelUsed":6.710013
     * , "FuelLevel":25.289986 }
     */

    private String StarSystem;

    public Jump(JSONObject json) {
        StarSystem = json.getString("StarSystem");
    }

    @Override
    public void run() {

    }
}
