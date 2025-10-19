package appender;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

public class MemAppender extends AppenderSkeleton {

    private static int maxSize;
    private static long discardedLogsCount;
    private static List<LoggingEvent> loggingEvents;
    private static Layout layout;
    
    private static MemAppender instance;


    // Default private constructor for singleton
    private MemAppender() {
        maxSize = 100;
        discardedLogsCount = 0;
        loggingEvents = new ArrayList<>();
    }


    /* 
    #####################
     Getters and Setters 
    #####################
    */ 

    public static MemAppender getInstance() {
        if (instance == null) {
            instance = new MemAppender();
        }
        return instance;
    }
    
    public static void setMaxSize(int maxSize) {
        MemAppender.maxSize = maxSize;
    }
    public static int getMaxSize() {
        return maxSize;
    }
    
    public static void setLoggingEvents(List<LoggingEvent> events) {
        loggingEvents = events;
        resetDiscardedLogsCount();
    }
    public static List<LoggingEvent> getLoggingEvents() {
        return loggingEvents;
    }
    
    public static long getDiscardedLogsCount() {
        return discardedLogsCount;
    }

    public static List<LoggingEvent> getCurrentLogs() {
        if (loggingEvents == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(loggingEvents);
    }

    /* 
    ###############
     Other Methods 
    ###############
    */ 

    public static void resetDiscardedLogsCount() {
        discardedLogsCount = 0;
    }

    public static void addLoggingEvent(LoggingEvent event) {
        // Possibly add check here for loggingEvents being null

        if (loggingEvents.size() >= maxSize) {
            loggingEvents.remove(0);
            discardedLogsCount++;
        }
        loggingEvents.add(event);
    }

    /**
     * @brief Print logs to console using the current layout & clear stored logs
     * @throws IllegalStateException if layout is not set
     */
    public static void printLogs() throws IllegalStateException{
        if (layout == null) {
            throw new IllegalStateException("Layout is not set");
        }
        for (LoggingEvent event : loggingEvents) {
            System.out.println(layout.format(event));
        }
        loggingEvents.clear();
    }

    /**
     * Get formatted event strings using the current layout.
     * @return unmodifiableList of formatted event strings
     * @throws IllegalStateException if layout is not set
     */
    public static List<String> getEventStrings() throws IllegalStateException {
        if (layout == null) {
            throw new IllegalStateException("Layout is not set");
        }
        List<String> eventStrings = new ArrayList<>();
        for (LoggingEvent event : loggingEvents) {
            eventStrings.add(layout.format(event));
        }
        return Collections.unmodifiableList(eventStrings);
    }

    /* Inherited Methods */

    @Override
    public void setLayout(Layout layout) {
        MemAppender.layout = layout;
    }

    @Override
    public void close() {
        // Clean up resources
        if (loggingEvents != null) {
            loggingEvents.clear();
        }
    }

    @Override
    public boolean requiresLayout() {
        return false; // MemAppender can work without a layout
    }

    @Override
    protected void append(LoggingEvent event) {
        addLoggingEvent(event);
    }
}