package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.events.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ShipyardSwap extends Event {

    private static final Logger logger = LogManager.getLogger(ShipyardSwap.class);

    @Override
    public void run() {

    }
    /*{ "timestamp":"2022-04-24T12:02:04Z"
     * , "event":"ShipyardSwap"
     * , "ShipType":"krait_mkii"
     * , "ShipType_Localised":"Krait Mk II"
     * , "ShipID":9
     * , "StoreOldShip":"Anaconda"
     * , "StoreShipID":12
     * , "MarketID":3221679616 }
     */
    public ShipyardSwap(JSONObject json) {
        // TODO: find out something that actually worth implementing
    }
}
