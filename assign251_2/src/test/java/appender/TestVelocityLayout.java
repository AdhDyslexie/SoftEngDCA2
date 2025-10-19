package appender;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;

import org.junit.jupiter.api.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class TestVelocityLayout {

    @Test
    public void testFormat() {
        String pattern = "[$p] $c $t $d: $m $n";
        VelocityLayout layout = new VelocityLayout(pattern);
        
        Logger logger = Logger.getLogger("TestLogger");
        logger.setLevel(Level.INFO);
        
        // Create a logging event
        LoggingEvent event = new LoggingEvent(
            "org.apache.log4j.Logger",
            logger,
            System.currentTimeMillis(),
            Level.INFO,
            "This is a test message",
            Thread.currentThread().getName(),
            null,
            null,
            null,
            null
        );

        
        String formattedMessage = layout.format(event);
        Date date = new Date(System.currentTimeMillis());
        String expectedDateStr = date.toString();
        String threadName = Thread.currentThread().getName();

        // Verify that the formatted message contains all expected components
        assertTrue(formattedMessage.contains("[INFO]"));
        assertTrue(formattedMessage.contains("TestLogger"));
        assertTrue(formattedMessage.contains(expectedDateStr));
        assertTrue(formattedMessage.contains(threadName));
        assertTrue(formattedMessage.contains("This is a test message"));
        assertTrue(formattedMessage.endsWith("\n"));
    }

    @Test
    public void testChangingPattern() {
        String initialPattern = "[$p] $m";

        VelocityLayout layout = new VelocityLayout(initialPattern);
        Logger logger = Logger.getLogger("TestLogger");
        logger.setLevel(Level.INFO);

        // Create a logging event
        LoggingEvent initEvent = new LoggingEvent(
            "org.apache.log4j.Logger",
            logger,
            System.currentTimeMillis(),
            Level.INFO,
            "Test message",
            Thread.currentThread().getName(),
            null,
            null,
            null,
            null
        );

        String expected = new String("[INFO] Test message");
        String actual = layout.format(initEvent);
        assertEquals(expected, actual);

        // Change pattern
        String newPattern = "New Pattern: $p $c $m";
        layout.setPattern(newPattern);

        // Create a logging event
        LoggingEvent newEvent = new LoggingEvent(
            "org.apache.log4j.Logger",
            logger,
            System.currentTimeMillis(),
            Level.INFO,
            "Test message",
            Thread.currentThread().getName(),
            null,
            null,
            null,
            null
        );

        String expectedNew = new String("New Pattern: INFO TestLogger Test message");
        String actualNew = layout.format(newEvent);
        assertEquals(expectedNew, actualNew);
    }
}
