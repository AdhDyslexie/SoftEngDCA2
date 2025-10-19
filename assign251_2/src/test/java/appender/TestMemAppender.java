package appender;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Level;

public class TestMemAppender 
{

    @BeforeEach
    public void setUp() {
        MemAppender.setLoggingEvents(new ArrayList<LoggingEvent>());
        MemAppender.resetDiscardedLogsCount();
    }

    @Test
    public void testSingleton()
    {
        MemAppender instance1 = MemAppender.getInstance();
        MemAppender instance2 = MemAppender.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testBasicLogging() {
        MemAppender appender = MemAppender.getInstance();
        
        // Create a simple logger and add our appender
        Logger logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log some messages
        logger.info("Test message 1");
        logger.info("Test message 2");
        
        // Check if messages were captured
        List<LoggingEvent> logs = MemAppender.getCurrentLogs();
        assertEquals(2, logs.size());
        assertEquals("Test message 1", logs.get(0).getMessage());
        assertEquals("Test message 2", logs.get(1).getMessage());
    }

    @Test
    public void testMaxSizeLimit() {
        MemAppender.setMaxSize(3);
        MemAppender appender = MemAppender.getInstance();
        
        Logger logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log more messages than the limit
        logger.info("Message 1");
        logger.info("Message 2");
        logger.info("Message 3");
        logger.info("Message 4"); // This should cause discard
        logger.info("Message 5"); // This should cause discard
        
        List<LoggingEvent> logs = MemAppender.getCurrentLogs();
        assertEquals(3, logs.size()); // Should only keep 3 messages
        assertEquals(2, MemAppender.getDiscardedLogsCount()); // Should have discarded 2
        
        // Check that we kept the most recent messages
        assertEquals("Message 3", logs.get(0).getMessage());
        assertEquals("Message 4", logs.get(1).getMessage());
        assertEquals("Message 5", logs.get(2).getMessage());
    }

    @Test
    public void testResetDiscardedLogsCount() {
        MemAppender.setMaxSize(2);
        MemAppender appender = MemAppender.getInstance();
        
        Logger logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        // Log messages to exceed max size
        logger.info("Msg 1");
        logger.info("Msg 2");
        logger.info("Msg 3"); // Discarded
        assertEquals(1, MemAppender.getDiscardedLogsCount());
        
        // Reset count
        MemAppender.resetDiscardedLogsCount();
        assertEquals(0, MemAppender.getDiscardedLogsCount());
    }

    @Test
    public void testWithLayout() {
        MemAppender appender = MemAppender.getInstance();
        PatternLayout layout = new PatternLayout("%p - %m");
        appender.setLayout(layout);
        
        Logger logger = Logger.getLogger("TestLogger");
        logger.addAppender(appender);
        logger.setLevel(Level.INFO);
        
        logger.info("Test message");

        List<String> eventStrings = MemAppender.getEventStrings();
        String expected = new String("INFO - Test message");
        String actual = new String(eventStrings.get(0));
        assertEquals(expected, actual);
    }

}
