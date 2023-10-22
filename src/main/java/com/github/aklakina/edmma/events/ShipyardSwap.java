package com.github.aklakina.edmma.events;

import org.json.JSONObject;

public class ShipyardSwap extends Event {
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
