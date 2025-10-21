package appender;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

public class MemAppender extends AppenderSkeleton {

    private static MemAppender instance;
    
    private static List<LoggingEvent> loggingEvents;
    private static Layout memLayout;
    private static int maxSize;
    private static final int DEFAULT_MAX_SIZE = 100;
    private static long discardedLogsCount;
    


    // Default constructor for singleton
    private MemAppender() {
        maxSize = DEFAULT_MAX_SIZE;
        discardedLogsCount = 0;
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

    /**
     * Set layout when getting instance
     * @param layout
     * @return MemAppender instance with specified layout
     */
    public static MemAppender getInstance(Layout layout) {
        if (instance == null) {
            instance = new MemAppender();
        }
        instance.setLayout(layout);
        return instance;
    }
    
    public void setMaxSize(int maxSize) {
        MemAppender.maxSize = maxSize;
    }
    public int getMaxSize() {
        return maxSize;
    }

    public void setLoggingEvents(List<LoggingEvent> events) throws IllegalArgumentException {
        if(events == null) {
            throw new IllegalArgumentException("events cannot be null");
        }
        loggingEvents = events;
        resetDiscardedLogsCount();
    }
    // public List<LoggingEvent> getLoggingEvents() {
    //     return loggingEvents;
    // }
    
    public long getDiscardedLogsCount() {
        return discardedLogsCount;
    }

    /**
     * @return unmodifiableList of current stored LoggingEvents
     */
    public List<LoggingEvent> getCurrentLogs() {
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

    /**
     * Reset the class fields but keep the singleton instance
     */
    public static void resetAppenderFields()
    {
        if (loggingEvents != null) {
            loggingEvents.clear();
        }
        memLayout = null;
        maxSize = DEFAULT_MAX_SIZE;
        resetDiscardedLogsCount();
    }

    /**
     * Reset the singleton class instance and its fields
     */
    public static void resetAppender()
    {
        instance = null;
        if (loggingEvents != null) {
            loggingEvents.clear();
        }
        memLayout = null; // getLayout();
        maxSize = DEFAULT_MAX_SIZE;
        resetDiscardedLogsCount();
    }

    public static void resetDiscardedLogsCount() {
        discardedLogsCount = 0;
    }

    public void addLoggingEvent(LoggingEvent event) {
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
    public void printLogs() throws IllegalStateException{
        if (memLayout == null) {
            throw new IllegalStateException("Layout is not set");
        }
        for (LoggingEvent event : loggingEvents) {
            System.out.println(memLayout.format(event));
        }
        loggingEvents.clear();
    }

    /**
     * Get formatted event strings using the current layout.
     * @return unmodifiableList of formatted event strings
     * @throws IllegalStateException if layout is not set
     */
    public List<String> getEventStrings() throws IllegalStateException {
        if (memLayout == null) {
            throw new IllegalStateException("Layout is not set");
        }
        List<String> eventStrings = new ArrayList<>();
        for (LoggingEvent event : loggingEvents) {
            eventStrings.add(memLayout.format(event));
        }
        return Collections.unmodifiableList(eventStrings);
    }

    /* Inherited Methods */

    @Override
    public void setLayout(Layout newLayout) {
        MemAppender.memLayout = newLayout;
    }

    @Override
    public Layout getLayout() {
        return memLayout;
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