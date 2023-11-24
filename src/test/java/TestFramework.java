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

public class TestFramework {

    private static final Logger logger = LogManager.getLogger(TestFramework.class);

    protected static final List<JSONObject> events = new ArrayList<>();

    protected static class TestEvent extends com.github.aklakina.edmma.events.Event {

        private final JSONObject json;

        @Override
        public void run() {
            TestFramework.events.add(json);
        }

        public TestEvent(JSONObject json) {
            this.json = json;
        }
    }

    @Mock
    protected JSONObject json;

    protected EntityManager entityManager;

    protected AutoCloseable mockEntities;

    protected void createAndWaitForEvent() throws InterruptedException {
        SingletonFactory.getSingleton(DataFactory.class).spawnEvent(json);
        waitForEvents();
    }

    protected void waitForEvents() throws InterruptedException {
        SingletonFactory.getSingleton(EventHandler.class).waitForEvents();
    }

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

    @AfterAll
    public static void tearDown() {

        SingletonFactory.getSingleton(AppCloser.class);
    }
}
