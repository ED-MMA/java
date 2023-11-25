import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.threading.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThreadsTest {

    private Threads threads;
    private RegisteredThread registeredThread;
    private RegisteredThread anotherThread;

    @BeforeEach
    public void setup() {
        threads = SingletonFactory.getSingleton(Threads.class);
        registeredThread = new RegisteredThread(new ResourceReleasingRunnable() {
            @Override
            public void actionOnThreadNotify() {
                // wait for 100ms to simulate a running thread
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        anotherThread = new RegisteredThread(new ResourceReleasingRunnable() {
            @Override
            public void actionOnThreadNotify() {
                // wait for 100ms to simulate a running thread
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        registeredThread.start();
        try {
            if (!registeredThread.isWaiting())
                synchronized (registeredThread) {
                    registeredThread.wait();
                }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        anotherThread.start();
        try {
            if (!anotherThread.isWaiting())
                synchronized (anotherThread) {
                    anotherThread.wait();
                }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        threads.close();
    }

    @Test
    @Order(1)
    public void registersThreadSuccessfully() {
        assertTrue(threads.getThreads().contains(registeredThread));
    }

    @Test
    @Order(2)
    public void doesNotRegisterThreadTwice() {
        threads.registerThread(registeredThread);
        assertEquals(2, threads.getThreads().size());
    }

    @Test
    @Order(3)
    public void doesNotRegisterNotRunningThread() {
        RegisteredThread notRunningThread = new RegisteredThread(new ResourceReleasingRunnable() {
            @Override
            public void actionOnThreadNotify() {
                // Implement the action to be performed on thread notification
            }
        });
        assertEquals(2, threads.getThreads().size());
    }

    @Test
    @Order(4)
    public void removesThreadSuccessfully() {
        registeredThread.exit();
        assertFalse(threads.getThreads().contains(registeredThread));
    }

    @Test
    @Order(5)
    public void closesAllThreadsSuccessfully() {
        threads.close();

        // Check if threads are removed after closing
        assertFalse(threads.getThreads().contains(registeredThread));
        assertFalse(threads.getThreads().contains(anotherThread));
    }
}