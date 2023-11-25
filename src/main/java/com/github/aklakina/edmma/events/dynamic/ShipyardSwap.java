package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.events.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * ShipyardSwap event:
 * <pre>
 * {
 *   "timestamp":"2022-04-24T12:02:04Z",
 *   "event":"ShipyardSwap",
 *   "ShipType":"krait_mkii",
 *   "ShipType_Localised":"Krait Mk II",
 *   "ShipID":9,
 *   "StoreOldShip":"Anaconda",
 *   "StoreShipID":12,
 *   "MarketID":3221679616
 * }
 * </pre>
 * Triggered when the player swaps ships at a shipyard.
 */
public class ShipyardSwap extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(ShipyardSwap.class);

    /**
     * Constructor with parameters.
     * Initializes the event data.
     *
     * @param json the JSON object containing the event data
     */
    public ShipyardSwap(JSONObject json) {
        // TODO: find out something that actually worth implementing
    }

    /**
     * Processes the ShipyardSwap event.
     * Currently, this method does not perform any actions.
     */
    @Override
    public void run() {

    }
}