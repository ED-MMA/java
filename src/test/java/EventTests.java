import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.*;
import com.github.aklakina.edmma.logicalUnit.AppCloser;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import com.github.aklakina.edmma.logicalUnit.EventHandler;
import com.github.aklakina.edmma.logicalUnit.Init;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventTests extends TestFramework {

    @BeforeAll
    public static void init() {
        TestFramework.init();
    }

    @Test
    @Order(3)
    public void missionAcceptedEventProcessesCorrectly() throws InterruptedException {
        when(json.getLong("MissionID")).thenReturn(1L);
        when(json.getString("Faction")).thenReturn("Faction");
        when(json.getString("TargetFaction")).thenReturn("TargetFaction");
        when(json.getString("DestinationSystem")).thenReturn("DestinationSystem");
        when(json.getString("DestinationStation")).thenReturn("DestinationStation");
        when(json.getString("Expiry")).thenReturn("Expiry");
        when(json.getBoolean("Wing")).thenReturn(true);
        when(json.getDouble("Reward")).thenReturn(1000000.0);
        when(json.getInt("KillCount")).thenReturn(2);
        when(json.getString("Name")).thenReturn("Massacre");
        when(json.getString("event")).thenReturn("MissionAccepted");

        createAndWaitForEvent();

        Mission mission = Queries_.getMissionByID(entityManager, 1L);
        assertEquals(1L, mission.getID());
        assertEquals("Faction", mission.getSource().getFaction().getName());
        assertEquals("TargetFaction", mission.getCluster().getTargetFaction().getName());
        assertEquals("DestinationSystem", mission.getCluster().getTargetSystem().getName());
        assertEquals("DestinationStation", mission.getSource().getStation().getName());
        assertEquals("Expiry", mission.getExpiry());
        assertTrue(mission.isShareable());
        assertEquals(1.0, mission.getReward());
        assertEquals(2, mission.getKillsRequired());
        assertEquals(0, mission.getProgress());
        assertFalse(mission.isCompleted());

    }

    @Test
    @Order(2)
    public void dockedEventProcessesCorrectly() throws InterruptedException {
        when(json.getString("StationName")).thenReturn("station1");
        when(json.getString("StarSystem")).thenReturn("system1");
        when(json.getString("event")).thenReturn("Docked");

        createAndWaitForEvent();

        assertEquals(Globals.GALACTIC_POSITION.getSystem().getName(), Queries_.getGalacticPosition(entityManager, 0L).getSystem().getName());
        assertEquals(Globals.GALACTIC_POSITION.getStation().getName(), Queries_.getGalacticPosition(entityManager, 0L).getStation().getName());
    }

    @Test
    @Order(1)
    public void fsdJumpEventProcessesCorrectly() throws InterruptedException {
        when(json.getString("StarSystem")).thenReturn("StarSystem");
        when(json.getString("event")).thenReturn("FSDJump");

        createAndWaitForEvent();

        assertEquals(Globals.GALACTIC_POSITION.getSystem().getName(), Queries_.getGalacticPosition(entityManager, 0L).getSystem().getName());
        assertNull(Globals.GALACTIC_POSITION.getStation());
    }

    @Test
    @Order(4)
    public void bountyEventProcessesCorrectly() throws InterruptedException {
        when(json.getString("VictimFaction")).thenReturn("TargetFaction");
        when(json.getString("event")).thenReturn("Bounty");

        createAndWaitForEvent();

        List<Mission> mission = Queries_.getMissionByTargetFaction(entityManager, "TargetFaction");
        for (Mission m : mission) {
            assertEquals(1, m.getProgress());
        }
    }

    @Test
    @Order(5)
    public void missionCompletedEventProcessesCorrectly() throws InterruptedException, NoResultException {
        when(json.getString("Name")).thenReturn("Massacre");
        when(json.getLong("MissionID")).thenReturn(1L);
        when(json.getString("event")).thenReturn("MissionCompleted");

        createAndWaitForEvent();

        assertThrows(NoResultException.class, () -> Queries_.getMissionByID(entityManager, 1L));

    }

    @AfterAll
    public static void tearDown() {
        TestFramework.tearDown();
    }
}