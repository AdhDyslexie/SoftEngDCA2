package appender;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.apache.log4j.Logger;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.PatternLayout;


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
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 100000, 1000000})
    public void testMemAppenderWithArrayList(int maxSize) {
        setUpAndRunMemAppender(maxSize, new ArrayList<LoggingEvent>(), null, "MemAppender With ArrayList");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 100000, 1000000})
    public void testMemAppenderWithLinkedList(int maxSize) {
        setUpAndRunMemAppender(maxSize, new LinkedList<LoggingEvent>(), null, "MemAppender With LinkedList");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 100000, 1000000})
    public void testConsoleAppender(int maxSize) {
        setUpAndRunConsoleAppender(maxSize, new SimpleLayout());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100, 1000, 10000, 100000, 1000000})
    public void testFileAppender(int maxSize) {
        setUpAndRunFileAppender(maxSize, new SimpleLayout());
    }

    @Test
    public void testPatternLayout() {

        Layout patternLayout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");
        int[] sizes = {1, 10, 100, 1000, 10000, 100000, 1000000};
        
        System.out.printf("=== PatternLayout Performance Test ===%n ");
        System.err.printf("=== PatternLayout Performance Test ===%n "); // Will still output to console when redirecting console output to a file

        startTime = System.currentTimeMillis();
        
        // Test MemAppender with PatternLayout. Use LinkedList to give MemAppender best chance at performance
        for (int size : sizes) {
            setUpAndRunMemAppender(size, new LinkedList<LoggingEvent>(), patternLayout, "MemAppender With PatternLayout");
        }
        // Test FileAppender with PatternLayout
        for (int size : sizes) {
            setUpAndRunFileAppender(size, patternLayout);
        }
        // Test ConsoleAppender with PatternLayout
        for (int size : sizes) {
            setUpAndRunConsoleAppender(size, patternLayout);
        }

        endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // TODO: account for sleeping thread if JConsole monitoring is enabled
        System.out.printf("Final stats:%n");
        System.out.printf("\tApproximate test duration: %d ms%n", duration);
        System.out.println("\n");
    }

    @Test
    public void testVelocityLayout() {

        VelocityLayout velocityLayout = new VelocityLayout("[$p] $c: $m");
        int[] sizes = {1, 10, 100, 1000, 10000, 100000, 1000000};

        System.out.printf("=== VelocityLayout Performance Test ===%n ");
        System.err.printf("=== VelocityLayout Performance Test ===%n "); // Will still output to console when redirecting console output to a file

        startTime = System.currentTimeMillis();
        
        // Test MemAppender with velocityLayout. Use LinkedList to give MemAppender best chance at performance
        for (int size : sizes) {
            setUpAndRunMemAppender(size, new LinkedList<LoggingEvent>(), velocityLayout, "MemAppender With VelocityLayout");
        }
        // Test FileAppender with velocityLayout
        for (int size : sizes) {
            setUpAndRunFileAppender(size, velocityLayout);
        }
        // Test ConsoleAppender with velocityLayout
        for (int size : sizes) {
            setUpAndRunConsoleAppender(size, velocityLayout);
        }

        endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // TODO: account for sleeping thread if JConsole monitoring is enabled
        System.out.printf("Final stats:%n");
        System.out.printf("\tApproximate test duration: %d ms%n", duration);
        System.out.println("\n");
    }


    /*
     * ######################################
     * ########### Helper methods ###########
     * ######################################
     */

    /**
     * Helper method to set up and run MemAppender performance tests for different data structures and layouts.
     * @param maxSize the maximum log capacity of MemAppender
     * @param dataStructure the data structure to use for storing logs (e.g., ArrayList, LinkedList)
     * @param patternLayout if a specific layout is desired, else null for default
     * @param prompt for identifying the test
     */
    private void setUpAndRunMemAppender(int maxSize, List<LoggingEvent> dataStructure, Layout patternLayout, String prompt) {

        System.out.printf("=== %s Performance Test - (%d) ===%n ", prompt, maxSize);
        System.err.printf("=== %s Performance Test - (%d) ===%n ", prompt, maxSize); // Will still output to console when redirecting console output to a file

        Long startTime = System.currentTimeMillis();

        // Set up MemAppender
        memAppender = MemAppender.getInstance();
        memAppender.setMaxSize(maxSize);
        memAppender.setLoggingEvents(dataStructure);
        if(patternLayout != null) {
            memAppender.setLayout(patternLayout);
        }
        
        // Set up Logger
        Logger logger = Logger.getLogger("MemLogger");
        logger.addAppender(memAppender);
        logger.setLevel(Level.INFO);
        
        logsForPerformanceTest(maxSize, logger);

        Long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // TODO: account for sleeping thread if JConsole monitoring is enabled

        System.out.printf("\tFinal stats:%n");
        System.out.printf("\t\tApproximate test duration: %d ms%n", duration);
        System.out.printf("\t\tLogs processed: %d%n", maxSize + memAppender.getDiscardedLogsCount());
        System.out.printf("\t\tLogs stored: %d%n", memAppender.getCurrentLogs().size());
        System.out.printf("\t\tLogs discarded: %d%n", memAppender.getDiscardedLogsCount());
        System.out.println("\n");
    
        // Verify that logs are stored correctly
        // assertTrue(memAppender.getCurrentLogs().size() > 0);
        // assertTrue(memAppender.getCurrentLogs().size() <= maxSize, "Number of stored logs should not exceed maxSize");
    }

    /**
     * Set up and run ConsoleAppender performance tests
     * @param maxSize used for logging iterations
     * @param layout layout to use
     */
    private void setUpAndRunConsoleAppender(int maxSize, Layout layout) {
        long startTime = System.currentTimeMillis();

        // Set up Logger
        Logger logger = Logger.getLogger("ConsoleLogger");
        logger.addAppender(
            new ConsoleAppender(
                layout
            )
        );
        logger.setLevel(Level.INFO);

        System.out.printf("=== Console Appender Performance Test - (%d) ===%n ", maxSize);
        System.err.printf("=== Console Appender Performance Test - (%d) ===%n ", maxSize); // Will still output to console when redirecting console output to a file

        logsForPerformanceTest(maxSize, logger);
       
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // TODO: account for sleeping thread if JConsole monitoring is enabled
        System.out.printf("\tFinal stats:%n");
        System.out.printf("\t\tApproximate test duration: %d ms%n", duration);
        System.out.println("\n");
    }

    /**
     * Set up and run FileAppender performance tests
     * @param maxSize used for logging iterations
     * @param layout layout to use
     */
    private void setUpAndRunFileAppender(int maxSize, Layout layout) {
        long startTime = System.currentTimeMillis();

        // Set up Logger
        Logger logger = Logger.getLogger("FileLogger");
        try {
            logger.addAppender(
                new FileAppender(
                    layout,
                    "logs/test.log"
                )
            );
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            System.err.println("Failed to create FileAppender: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Something went wrong 😢: " + e.getMessage());
        }

        System.out.printf("=== File Appender Performance Test - (%d) ===%n ", maxSize);
        System.err.printf("=== File Appender Performance Test - (%d) ===%n ", maxSize); // Will still output to console when redirecting console output to a file

        logsForPerformanceTest(maxSize, logger);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime; // TODO: account for sleeping thread if JConsole monitoring is enabled
        System.out.printf("\tFinal stats:%n");
        System.out.printf("\t\tApproximate test duration: %d ms%n", duration);
        System.out.println("\n");
    }

    /**
     * Log messages for performance tests
     * @param maxSize
     * @param logger
     */
    private void logsForPerformanceTest(int maxSize, Logger logger) {

        printPerformanceInfo("Before logging");
    
        for (int i = 0; i < maxSize; i++) {
            logger.info("Info message " + i);
        }
        
        printPerformanceInfo("Inbetween logging (After MaxSize reached, before overflow)");
    
        // Additional logs to exceed maxSize and test discarding
        int moreLogs = Math.max(NUMBER_OF_LOGS, maxSize / 2); 
        for (int i = 0; i < moreLogs; i++) {
            logger.info("Overflow message " + i);
        }
        
        printPerformanceInfo("After logging");
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
