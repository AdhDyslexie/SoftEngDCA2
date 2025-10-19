package appender;

import java.io.Serializable;


 
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;


public class MemAppender extends AppenderSkeleton
{
    private static int maxSize = 100;
    private static long discardedLogsCount = 0;
    private static List<LoggingEvent> LoggingEvents;
    private static Layout layout;
    
    private static MemAppender instance = new MemAppender();

    public static MemAppender getInstance() {
        return instance;
    }

    private MemAppender() throws IllegalCallerException {
        if(instance != null) {
            throw new IllegalCallerException("MemAppender instance already exists!");
        }
    }
    
    public static void setMaxSize(int maxSize) {
        MemAppender.maxSize = maxSize;
    }

    public static int getMaxSize() {
        return maxSize;
    }

    public static long getDiscardedLogsCount() {
        return discardedLogsCount;
    }
    
    private static void setDiscardedLogsCount(long newCount) {
        MemAppender.discardedLogsCount = newCount;
    }

    public static void resetDiscardedLogsCount() {
        setDiscardedLogsCount(0);
    }

    public static void setLoggingEvents(List<LoggingEvent> LoggingEvents) {
        MemAppender.LoggingEvents = LoggingEvents;
    }

    public static List<LoggingEvent> getLoggingEvents() {
        return LoggingEvents;
    }


    public static void addLoggingEvent(LoggingEvent LoggingEvent) {
        if(LoggingEvents.size() >= maxSize) {
            // Remove oldest log event and count as discarded
            LoggingEvents.remove(0);
            discardedLogsCount++;
        }
        LoggingEvents.add(LoggingEvent);
    }

    // Not sure if needs to check for null List
    public static List<LoggingEvent> getCurrentLogs() {
        return Collections.unmodifiableList(LoggingEvents);
    }

    public static List<String> getEventStrings() throws IllegalStateException {
        if (layout == null) {
            throw new IllegalStateException("Layout is not set");
        }
        List<String> eventStrings = new ArrayList<>();
        for (LoggingEvent event : LoggingEvents) {
            eventStrings.add("layout.toSerializable(event).toString()");
        }
        return Collections.unmodifiableList(eventStrings);
    }

    public static void printLogs() {
        for (LoggingEvent event : LoggingEvents) {
            System.out.println("layout.toSerializable(event).toString()");
        }
        LoggingEvents.clear();
    }

    public void setLayout(Layout layout) {
        MemAppender.layout = layout;
    }



    @Override
    public void close() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

    @Override
    public boolean requiresLayout() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requiresLayout'");
    }

    @Override
    protected void append(LoggingEvent event) {
        addLoggingEvent(event);
    }


}
