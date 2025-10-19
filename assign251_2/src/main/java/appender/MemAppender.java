package appender;

import java.io.Serializable;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
 
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.core.appender.AbstractAppender;

public class MemAppender extends AbstractAppender
{
    private static int maxSize = 100;
    private static long discardedLogsCount = 0;
    private static List<LogEvent> logEvents;
    private static Layout<? extends Serializable> layout;
    
    private static MemAppender instance = new MemAppender();

    public static MemAppender getInstance() {
        return instance;
    }

    private MemAppender() throws IllegalCallerException {
        super("MemAppender", null, null, true, null);
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

    public static void setLogEvents(List<LogEvent> logEvents) {
        MemAppender.logEvents = logEvents;
    }

    public static List<LogEvent> getLogEvents() {
        return logEvents;
    }


    public static void addLogEvent(LogEvent logEvent) {
        if(logEvents.size() >= maxSize) {
            // Remove oldest log event and count as discarded
            logEvents.remove(0);
            discardedLogsCount++;
        }
        logEvents.add(logEvent);
    }

    // Not sure if needs to check for null List
    public static List<LogEvent> getCurrentLogs() {
        return Collections.unmodifiableList(logEvents);
    }

    public static List<String> getEventStrings() throws IllegalStateException {
        if (layout == null) {
            throw new IllegalStateException("Layout is not set");
        }
        List<String> eventStrings = new ArrayList<>();
        for (LogEvent event : logEvents) {
            eventStrings.add(layout.toSerializable(event).toString());
        }
        return Collections.unmodifiableList(eventStrings);
    }

    public static void printLogs() {
        for (LogEvent event : logEvents) {
            System.out.println(layout.toSerializable(event).toString());
        }
        logEvents.clear();
    }
    
    public static void setLayout(Layout<? extends Serializable> layout) {
        MemAppender.layout = layout;
    }


    @Override
    public void append(LogEvent event) {
        addLogEvent(event);
    }




}
