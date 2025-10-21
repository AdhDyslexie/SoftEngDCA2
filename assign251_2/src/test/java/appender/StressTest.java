package appender;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Date;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class StressTest {
    
    private static final int NUMBER_OF_LOGS = 10000;
    private MemAppender memAppender;
    private static final boolean ENABLE_JCONSOLE_MONITORING = false; // Toggle for JConsole mode
    private static long startTime;
    private static long endTime;
 


    @BeforeAll
    public static void initAll() {
        // Hard reset MemAppender & force garbage collection (in case of prior tests)
        MemAppender.resetAppender();
        System.gc();

        // Delay start to allow attaching JConsole
        if(ENABLE_JCONSOLE_MONITORING)
        {
            System.out.println("=== JConsole Monitoring Enabled ===");
            System.out.println("1. Open JConsole in another terminal: jconsole");
            System.out.println("2. Connect to this Java process");
            System.out.println("3. Go to Memory tab to monitor heap usage");
            System.out.println("4. Waiting 10 seconds before starting tests...");
            try {
                Thread.sleep(10000); // 10 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {

        // Reset the MemAppender after each test
        MemAppender.resetAppenderFields();
        // Delay after each test to allow for clearer JConsole monitoring
        if(ENABLE_JCONSOLE_MONITORING)
        {
            try {
                Thread.sleep(5000); // 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 1000000})
    public void testMemAppenderWithArrayList(int maxSize) {

        // System.err.printf("Test name: testMemAppenderWithArrayList (%d)%n", maxSize);
        System.err.printf("=== MemAppender With ArrayList Performance Test - (%d) ===%n ", maxSize);
        
        // Set up MemAppender
        memAppender = MemAppender.getInstance();
        memAppender.setMaxSize(maxSize);
        memAppender.setLoggingEvents(new ArrayList<LoggingEvent>());

        // Set up Logger
        Logger logger = Logger.getLogger("ArrayListLogger");
        logger.addAppender(memAppender);
        logger.setLevel(Level.INFO);


        startTime = System.currentTimeMillis();

        
        // 1. 
        printPerformanceInfo("Before logging");

        for (int i = 0; i < maxSize; i++) {
            logger.info("Info message " + i);
        }
        
        // 2. 
        printPerformanceInfo("Inbetween logging (After MaxSize reached, before overflow)");

        // Additional logs to exceed maxSize and test discarding
        int moreLogs = Math.max(NUMBER_OF_LOGS, maxSize / 2); 
        for (int i = 0; i < moreLogs; i++) {
            logger.info("Overflow message " + i);
        }
        
        // 3.
        printPerformanceInfo("After logging");


        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.printf("\tFinal stats:%n");
        System.out.printf("\t\tApprox Test duration: %d ms%n", duration);
        System.out.printf("\t\tLogs processed: %d%n", maxSize + moreLogs);
        System.out.printf("\t\tLogs stored: %d%n", memAppender.getCurrentLogs().size());
        System.out.printf("\t\tLogs discarded: %d%n", memAppender.getDiscardedLogsCount());
        System.out.println("\n");


        // Verify that logs are stored correctly
        assertTrue(memAppender.getCurrentLogs().size() > 0);
        assertTrue(memAppender.getCurrentLogs().size() <= maxSize, "Number of stored logs should not exceed maxSize");

    }



    private void printPerformanceInfo(String phase) {

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        System.out.printf("\t%s:%n", phase);
        System.out.printf("\t\tUsed Memory: %d MB (%d KB)%n", usedMemory / 1024 / 1024, usedMemory / 1024);
        System.out.printf("\t\tFree Memory: %d MB%n", freeMemory / 1024 / 1024);
        System.out.printf("\t\tTotal Memory: %d MB%n", totalMemory / 1024 / 1024);
    }

}
