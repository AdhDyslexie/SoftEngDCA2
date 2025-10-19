package appender;

import java.io.Serializable;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
 
// import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.logging.log4j.core.appender.AbstractAppender;

public class MemAppender extends AbstractAppender
{
    private static int maxSize;
    private static List<LogEvent> logEvents;
    
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

    public static void setLogEvents(List<LogEvent> logEvents) {
        MemAppender.logEvents = logEvents;
    }

    public static List<LogEvent> getLogEvents() {
        return logEvents;
    }


    public static void addLogEvent(LogEvent logEvent) {
        if(logEvents.size() < maxSize) {
            logEvents.add(logEvent);
        } else {
            // Remove oldest log event
            logEvents.remove(0);
            logEvents.add(logEvent);
        }
    }

    public static void getCurrentLogs() {}
    public static void getEventStrings() {}
    public static void printLogs() {}
    public static void setLayout() {}




    @Override
    public void append(LogEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'append'");
    }




}
