import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventTests extends TestFramework {

    @BeforeAll
    public static void init() {
        TestFramework.init();
    }

    @AfterAll
    public static void tearDown() {
        TestFramework.tearDown();
    }

    @Test
    @Order(3)
    public void missionAcceptedEventProcessesCorrectly() throws InterruptedException {
        spawnMissionAccepted(
                1L,
                "Faction",
                "TargetFaction",
                "DestinationSystem",
                "DestinationStation",
                "Expiry",
                true,
                1000000.0,
                2);

        waitForEvents();

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
        spawnDocked("station1", "system1");

        waitForEvents();

        assertEquals(Globals.GALACTIC_POSITION.getSystem().getName(), Queries_.getGalacticPosition(entityManager, 0L).getSystem().getName());
        assertEquals(Globals.GALACTIC_POSITION.getStation().getName(), Queries_.getGalacticPosition(entityManager, 0L).getStation().getName());
    }

    @Test
    @Order(1)
    public void fsdJumpEventProcessesCorrectly() throws InterruptedException {
        spawnFSDJump("StarSystem");

        waitForEvents();

        assertEquals(Globals.GALACTIC_POSITION.getSystem().getName(), Queries_.getGalacticPosition(entityManager, 0L).getSystem().getName());
        assertNull(Globals.GALACTIC_POSITION.getStation());
    }

    @Test
    @Order(4)
    public void bountyEventProcessesCorrectly() throws InterruptedException {
        spawnBounty("TargetFaction");

        waitForEvents();

        List<Mission> mission = Queries_.getMissionByTargetFaction(entityManager, "TargetFaction");
        for (Mission m : mission) {
            assertEquals(1, m.getProgress());
        }
    }

    @Test
    @Order(5)
    public void missionCompletedEventProcessesCorrectly() throws InterruptedException, NoResultException {
        spawnMissionCompleted(1L);

        waitForEvents();

        assertThrows(NoResultException.class, () -> Queries_.getMissionByID(entityManager, 1L));

    }
}