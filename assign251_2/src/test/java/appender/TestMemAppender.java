package appender;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

public class TestMemAppender 
{
    private MemAppender appender;
    private Logger logger;

    @BeforeAll
    public static void initAll() {
        // Reset the MemAppender completely first (in case of prior tests)
        MemAppender.resetAppender();
    }

    @BeforeEach
    public void setUp() {
        // System.out.println("Setting up test..."); // Debug output
        
        // Get the instance
        appender = MemAppender.getInstance();
        assertNotNull(appender, "MemAppender instance should not be null"); // Safety check
        
        // Initialize with empty list
        try {
            appender.setLoggingEvents(new ArrayList<LoggingEvent>());
        } catch (Exception e) {
            System.err.println("Error setting logging events: " + e.getMessage());
        }
        
        // Set up logger
        logger = Logger.getLogger("TestLogger");
        logger.removeAllAppenders();
        Logger.getRootLogger().removeAllAppenders();
        
        // System.out.println("Setup complete. Appender: " + appender); // Debug output
    }

    @AfterEach
    public void tearDown() {
        // Reset the MemAppender after each test
        MemAppender.resetAppender();
    }

    @Test
    public void testSingleton()
    {
        MemAppender instance1 = MemAppender.getInstance();
        MemAppender instance2 = MemAppender.getInstance();
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void testBasicLogging() {
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log some messages
        logger.info("Test message 1");
        logger.info("Test message 2");
        
        // Check if messages were captured
        List<LoggingEvent> logs = appender.getCurrentLogs();
        assertEquals(2, logs.size());
        assertEquals("Test message 1", logs.get(0).getMessage());
        assertEquals("Test message 2", logs.get(1).getMessage());
    }

    @Test
    public void testMaxSizeLimit() {
        appender.setMaxSize(3);
        
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log more messages than the limit
        logger.info("Message 1");
        logger.info("Message 2");
        logger.info("Message 3");
        logger.info("Message 4"); // This should cause discard
        logger.info("Message 5"); // This should cause discard
        
        List<LoggingEvent> logs = appender.getCurrentLogs();
        assertEquals(3, logs.size()); // Should only keep 3 messages
        assertEquals(2, appender.getDiscardedLogsCount()); // Should have discarded 2
        
        // Check that we kept the most recent messages
        assertEquals("Message 3", logs.get(0).getMessage());
        assertEquals("Message 4", logs.get(1).getMessage());
        assertEquals("Message 5", logs.get(2).getMessage());
    }

    @Test
    public void testChangeMaxSize() {
        appender.setMaxSize(2);
        
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log messages to exceed initial max size
        for (int i = 1; i <= 3; i++) {
            logger.info("Msg " + i);
        }
        assertEquals(1, appender.getDiscardedLogsCount());

        // Change max size to a larger value
        appender.setMaxSize(4);
        
        // Log more messages
        logger.info("Msg 4");
        logger.info("Msg 5");
        
        assertEquals(4, appender.getCurrentLogs().size()); // Should now keep 4 messages
        assertEquals(1, appender.getDiscardedLogsCount()); // Discard count should remain the same
    }

    @Test
    public void testResetDiscardedLogsCount() {
        appender.setMaxSize(2);
        
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log messages to exceed max size
        logger.info("Msg 1");
        logger.info("Msg 2");
        logger.info("Msg 3"); // Discarded
        assertEquals(1, appender.getDiscardedLogsCount());
        
        // Reset count
        MemAppender.resetDiscardedLogsCount();
        assertEquals(0, appender.getDiscardedLogsCount());
    }

    @Test
    public void testWithLayout() {
        PatternLayout layout = new PatternLayout("%p - %m");
        appender.setLayout(layout);

        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        logger.info("Test message");

        List<String> eventStrings = appender.getEventStrings();
        String expected = new String("INFO - Test message");
        String actual = new String(eventStrings.get(0));
        assertEquals(expected, actual);
    }

    @Test
    public void testVelocityLayoutIntegration() {
        VelocityLayout layout = new VelocityLayout("[$p] $c: $m");
        appender.setLayout(layout);

        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        logger.info("Velocity test message");

        List<String> eventStrings = appender.getEventStrings();
        assumeTrue(eventStrings.size() == 1);
        assertEquals("[INFO] TestLogger: Velocity test message", eventStrings.get(0));
    }

}
