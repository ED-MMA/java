import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.humanInterface.main_window;
import com.github.aklakina.edmma.logicalUnit.StatisticsCollector;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatisticsCollectorTests extends TestFramework {

    private StatisticsCollector statisticsCollector;
    private HashMap<String, String> expected;
    @Mock
    private main_window mainWindowMock;

    @BeforeAll
    public static void init() {
        TestFramework.init();
    }

    @AfterAll
    public static void tearDown() {
        TestFramework.tearDown();
    }

    @BeforeEach
    @Override
    public void setup() {
        statisticsCollector = SingletonFactory.getSingleton(StatisticsCollector.class);
        mockEntities = MockitoAnnotations.openMocks(this);
        expected = new HashMap<>();
        SingletonFactory.setSingleton(main_window.class, mainWindowMock);
        spawnFSDJump("system1");
        spawnDocked("station1", "system1");
        try {
            waitForEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    @Override
    public void tearDownEach() throws Exception {
        mockEntities.close();
    }

    @Test
    @Order(1)
    public void notifiesMainWindowWithCorrectJson() {
        spawnMissionAccepted(
                1L,
                "Faction1",
                "TargetFaction",
                "DestinationSystem",
                "DestinationStation",
                "Expiry",
                true,
                1000000.0,
                1
        );
        expected.put("theorReward", "0.0");
        expected.put("theorKills", "0");
        expected.put("theorMissions", "0");
        expected.put("killCounter", "0");
        expected.put("killsLeft", "1");
        expected.put("missionsLeft", "1");
        expected.put("killEfficiency", String.valueOf(1.0));
        expected.put("maxKills", String.valueOf(1));
        expected.put("completedCounter", "0");
        expected.put("PaymentCounter", "0.0");
        expected.put("paymentRatio", String.valueOf(1000000.0));
        expected.put("paymentLeft", String.valueOf(1000000.0));
        try {
            synchronized (statisticsCollector) {
                statisticsCollector.wait();
            }
            waitForEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mainWindowMock, atLeastOnce()).displayStatistics(expected);
    }

    @Test
    @Order(2)
    public void anotherMissionWithSameTargetButDifferentSource() {
        spawnMissionAccepted(
                2L,
                "Faction2",
                "TargetFaction",
                "DestinationSystem",
                "DestinationStation",
                "Expiry",
                true,
                1000000.0,
                1
        );
        expected.put("killCounter", "0");
        expected.put("killsLeft", "2");
        expected.put("missionsLeft", "2");
        expected.put("killEfficiency", String.valueOf(2.0));
        expected.put("maxKills", "1");
        expected.put("completedCounter", "0");
        expected.put("PaymentCounter", "0.0");
        expected.put("paymentRatio", String.valueOf(2000000.0));
        expected.put("paymentLeft", String.valueOf(2000000.0));
        try {
            synchronized (statisticsCollector) {
                statisticsCollector.wait();
            }
            waitForEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mainWindowMock, atLeastOnce()).displayStatistics(expected);
    }

    @Test
    @Order(3)
    public void missionCompletedStatisticsRefresh() {
        spawnMissionCompleted(1L);
        expected.put("killCounter", "0");
        expected.put("killsLeft", "1");
        expected.put("missionsLeft", "1");
        expected.put("killEfficiency", String.valueOf(1.0));
        expected.put("maxKills", "1");
        expected.put("completedCounter", "0");
        expected.put("PaymentCounter", "0.0");
        expected.put("paymentRatio", String.valueOf(1000000.0));
        expected.put("paymentLeft", String.valueOf(1000000.0));
        try {
            synchronized (statisticsCollector) {
                statisticsCollector.wait();
            }
            waitForEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mainWindowMock, atLeastOnce()).displayStatistics(expected);
    }

    @Test
    @Order(4)
    public void handleBountyEvent() {
        spawnBounty("TargetFaction");
        expected.put("killCounter", "1");
        expected.put("killsLeft", "0");
        expected.put("missionsLeft", "0");
        expected.put("killEfficiency", String.valueOf(0.0));
        expected.put("maxKills", "0");
        expected.put("completedCounter", "1");
        expected.put("PaymentCounter", "1000000.0");
        expected.put("paymentRatio", String.valueOf(0.0));
        expected.put("paymentLeft", String.valueOf(0.0));
        try {
            synchronized (statisticsCollector) {
                statisticsCollector.wait();
            }
            waitForEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mainWindowMock, atLeastOnce()).displayStatistics(expected);
    }

    @Test
    @Order(5)
    public void missionRedirectEventTest() {
        spawnMissionRedirected(2L, "station1", "system1");
        expected.put("missionsLeft", "0");
        expected.put("completedCounter", "1");
        expected.put("PaymentCounter", "1000000.0");
        expected.put("paymentRatio", String.valueOf(0.0));
        expected.put("paymentLeft", String.valueOf(0.0));
        try {
            synchronized (statisticsCollector) {
                statisticsCollector.wait();
            }
            waitForEvents();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mainWindowMock, atLeastOnce()).displayStatistics(expected);
    }
}