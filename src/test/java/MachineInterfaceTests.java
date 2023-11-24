import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.machineInterface.FileReader;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MachineInterfaceTests extends TestFramework {

    private static final Logger logger = LogManager.getLogger(MachineInterfaceTests.class);

    private static Path file1, file2;

    @BeforeAll
    public static void init() {
        Globals.ELITE_LOG_HOME = "./src/test/resources/EliteLogs";
        file1 = Paths.get(Globals.ELITE_LOG_HOME + "/Journal.20210101.log");
        file2 = Paths.get(Globals.ELITE_LOG_HOME + "/Journal.20210102.log");
        if (Files.exists(file1)) {
            try {
                Files.delete(file1);
            } catch (IOException e) {
                logger.error("Error deleting test file");
                logger.error("Error: " + e.getMessage());
            }
        }
        if (Files.exists(file2)) {
            try {
                Files.delete(file2);
            } catch (IOException e) {
                logger.error("Error deleting test file");
                logger.error("Error: " + e.getMessage());
            }
        }
        Globals.FILE_READER_CHECK_INTERVAL_UNIT = TimeUnit.SECONDS;
        Globals.FILE_READER_CHECK_INTERVAL = 4;
        TestFramework.init();
    }

    @AfterAll
    public static void tearDown() {
        TestFramework.tearDown();
    }

    @Test
    @Order(1)
    public void watchDirProcessesFileChangedCorrectly() throws InterruptedException, IOException {
        boolean created = file1.toFile().createNewFile();
        assertTrue(created);

        waitForEvents();

        file1.toFile().deleteOnExit();

        FileData fileData = Queries_.getFileDataByName(entityManager, file1.getFileName().toString());
        assertEquals(file1.getFileName().toString(), fileData.getName());
        assertEquals(0, fileData.getLastLineRead());
        assertEquals(0, fileData.getLastSize());
    }

    @Test
    @Order(2)
    public void anotherFileCreatedTest() throws IOException, InterruptedException {
        boolean created = file2.toFile().createNewFile();
        assertTrue(created);

        waitForEvents();

        file2.toFile().deleteOnExit();

        FileData fileData = Queries_.getFileDataByName(entityManager, file2.getFileName().toString());
        assertEquals(file2.getFileName().toString(), fileData.getName());
        assertEquals(0, fileData.getLastLineRead());
        assertEquals(0, fileData.getLastSize());
    }

    @Test
    @Order(3)
    public void fileChangedEventProcessesCorrectly() throws IOException, InterruptedException {
        JSONObject content = new JSONObject();
        content.put("event", "TestEvent");
        content.put("Test", "Test3");
        java.nio.file.Files.write(file1, content.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
        waitForEvents();
        FileData fileData = Queries_.getFileDataByName(entityManager, file1.getFileName().toString());
        assertEquals(file1.getFileName().toString(), fileData.getName());
        assertEquals(0, fileData.getLastLineRead());
        assertEquals(0, fileData.getLastSize());

        assertEquals(content.toString(), TestFramework.events.get(0).toString());
    }

    @Test
    @Order(4)
    public void fileChangedEventProcessesCorrectly2() throws IOException, InterruptedException {
        JSONObject content = new JSONObject();
        content.put("event", "TestEvent");
        content.put("Test", "Test4");
        java.nio.file.Files.write(file2, content.toString().getBytes(), java.nio.file.StandardOpenOption.APPEND);
        waitForEvents();
        FileData fileData = Queries_.getFileDataByName(entityManager, file2.getFileName().toString());
        assertEquals(file2.getFileName().toString(), fileData.getName());
        assertEquals(0, fileData.getLastLineRead());
        assertEquals(0, fileData.getLastSize());

        assertEquals(content.toString(), TestFramework.events.get(1).toString());
    }

    @Test
    @Order(5)
    public void fileDeletedEventProcessesCorrectly() throws InterruptedException, SecurityException, IOException, NoResultException {
        assertTrue(file2.toFile().exists());
        SingletonFactory.getSingleton(FileReader.class).removeFile(Queries_.getFileDataByName(entityManager, file2.getFileName().toString()));
        while (true) {
            try {
                Files.delete(file2);
                break;
            } catch (IOException e) {
                logger.debug("File not deleted yet");
                continue;
            }
        }
        waitForEvents();
        assertThrows(NoResultException.class, () -> Queries_.getFileDataByName(entityManager, file2.getFileName().toString()));
    }

    @Test
    @Order(6)
    public void fileChangedEventProcessesCorrectly3() throws IOException, InterruptedException {
        JSONObject content = new JSONObject();
        content.put("event", "TestEvent");
        content.put("Test", "Test5");
        Files.write(file1, ("\n" + content).getBytes(), java.nio.file.StandardOpenOption.APPEND);
        waitForEvents();
        FileData fileData = Queries_.getFileDataByName(entityManager, file1.getFileName().toString());
        assertEquals(file1.getFileName().toString(), fileData.getName());
        assertEquals(0, fileData.getLastLineRead());
        assertEquals(0, fileData.getLastSize());

        assertEquals(content.toString(), TestFramework.events.get(2).toString());
    }

    @Test
    @Order(7)
    public void closeTest() {
        SingletonFactory.getSingleton(FileReader.class).close();
        SingletonFactory.getSingleton(FileReader.class).waitForThreadOnFile(file1.toFile());
    }

    @Test
    @Order(8)
    public void saveTest() {
        FileData fileData = Queries_.getFileDataByName(entityManager, file1.getFileName().toString());
        assertEquals(file1.getFileName().toString(), fileData.getName());
        assertEquals(3, fileData.getLastLineRead());
        assertEquals(file1.toFile().length(), fileData.getLastSize());
    }

}