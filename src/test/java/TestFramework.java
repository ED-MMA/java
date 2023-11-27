import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.logicalUnit.AppCloser;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import com.github.aklakina.edmma.logicalUnit.EventHandler;
import com.github.aklakina.edmma.logicalUnit.Init;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFramework {

    protected static final List<JSONObject> events = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(TestFramework.class);
    @Mock
    protected JSONObject json;
    protected EntityManager entityManager;
    protected AutoCloseable mockEntities;

    @BeforeAll
    public static void init() {
        Globals.DATABASE_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        try {
            SingletonFactory.getSingleton(DataFactory.class).registerEventFactory("TestEvent", TestEvent.class.getDeclaredConstructor(JSONObject.class));
        } catch (Exception e) {
            // This should never happen tbh
            logger.error("TestEvent could not be registered!");
            logger.trace(e.getStackTrace());
        }
        SingletonFactory.getSingleton(Init.class).start();
    }

    @AfterAll
    public static void tearDown() {

        SingletonFactory.getSingleton(AppCloser.class);
    }

    protected void createAndWaitForEvent() throws InterruptedException {
        SingletonFactory.getSingleton(DataFactory.class).spawnEvent(json);
        waitForEvents();
    }

    protected void createEvent(JSONObject json) {
        SingletonFactory.getSingleton(DataFactory.class).spawnEvent(json);
    }

    protected void waitForEvents() throws InterruptedException {
        SingletonFactory.getSingleton(EventHandler.class).waitForEvents();
    }

    @BeforeEach
    public void setup() {
        mockEntities = MockitoAnnotations.openMocks(this);
        entityManager = ORMConfig.sessionFactory.createEntityManager();
    }

    @AfterEach
    public void tearDownEach() throws Exception {
        entityManager.close();
        mockEntities.close();
    }

    void spawnDocked(String systemName, String stationName) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("StarSystem")).thenReturn(systemName);
        when(json.getString("StationName")).thenReturn(stationName);
        when(json.getString("event")).thenReturn("Docked");
        createEvent(json);
    }

    void spawnUndocked() {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("event")).thenReturn("Undocked");
        createEvent(json);
    }

    void spawnBounty(String victimFaction) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("VictimFaction")).thenReturn(victimFaction);
        when(json.getString("event")).thenReturn("Bounty");
        createEvent(json);
    }

    void spawnMissionRedirected(Long missionID, String newDestinationStation, String newDestinationSystem) {
        JSONObject json = mock(JSONObject.class);
        when(json.getLong("MissionID")).thenReturn(missionID);
        when(json.getString("NewDestinationStation")).thenReturn(newDestinationStation);
        when(json.getString("NewDestinationSystem")).thenReturn(newDestinationSystem);
        when(json.getString("event")).thenReturn("MissionRedirected");
        createEvent(json);
    }

    void spawnShipyardSwap(String shipType, String storeOldShip) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("ShipType")).thenReturn(shipType);
        when(json.getString("StoreOldShip")).thenReturn(storeOldShip);
        when(json.getString("event")).thenReturn("ShipyardSwap");
        createEvent(json);
    }

    void spawnFileDeleted(String path) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("path")).thenReturn(path);
        when(json.getString("event")).thenReturn("FileDeleted");
        createEvent(json);
    }

    void spawnFSDJump(String starSystem) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("StarSystem")).thenReturn(starSystem);
        when(json.getString("event")).thenReturn("FSDJump");
        createEvent(json);
    }

    void spawnFileChanged(String path) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("path")).thenReturn(path);
        when(json.getString("event")).thenReturn("FileChanged");
        createEvent(json);
    }

    void spawnShipTargeted(String victimFaction) {
        JSONObject json = mock(JSONObject.class);
        when(json.getString("VictimFaction")).thenReturn(victimFaction);
        when(json.getString("event")).thenReturn("ShipTargeted");
        createEvent(json);
    }

    void spawnMissionAccepted(Long missionID, String faction, String targetFaction, String destinationSystem, String destinationStation, String expiry, boolean wing, double reward, int killCount) {
        JSONObject json = mock(JSONObject.class);
        when(json.getLong("MissionID")).thenReturn(missionID);
        when(json.getString("Faction")).thenReturn(faction);
        when(json.getString("TargetFaction")).thenReturn(targetFaction);
        when(json.getString("DestinationSystem")).thenReturn(destinationSystem);
        when(json.getString("DestinationStation")).thenReturn(destinationStation);
        when(json.getString("Expiry")).thenReturn(expiry);
        when(json.getBoolean("Wing")).thenReturn(wing);
        when(json.getDouble("Reward")).thenReturn(reward);
        when(json.getInt("KillCount")).thenReturn(killCount);
        when(json.getString("Name")).thenReturn("Massacre");
        when(json.getString("event")).thenReturn("MissionAccepted");
        createEvent(json);
    }

    void spawnMissionAbandoned(Long missionID) {
        JSONObject json = mock(JSONObject.class);
        when(json.getLong("MissionID")).thenReturn(missionID);
        when(json.getString("event")).thenReturn("MissionAbandoned");
        createEvent(json);
    }

    void spawnMissionCompleted(Long missionID) {
        JSONObject json = mock(JSONObject.class);
        when(json.getLong("MissionID")).thenReturn(missionID);
        when(json.getString("Name")).thenReturn("Massacre");
        when(json.getString("event")).thenReturn("MissionCompleted");
        createEvent(json);
    }

    protected static class TestEvent extends com.github.aklakina.edmma.events.Event {

        private final JSONObject json;

        public TestEvent(JSONObject json) {
            this.json = json;
        }

        @Override
        public void run() {
            TestFramework.events.add(json);
        }
    }
}
