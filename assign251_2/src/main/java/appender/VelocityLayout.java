package appender;

import java.io.Serializable;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

// Needs to integrate with both built in appenders and custom MemAppender

public class VelocityLayout extends Layout {

    private VelocityEngine velocityEngine;

    public VelocityLayout() {
        velocityEngine = new VelocityEngine();
        velocityEngine.init();
    }

    @Override
    public void activateOptions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'activateOptions'");
    }

    @Override
    public String format(LoggingEvent event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'format'");
    }

    @Override
    public boolean ignoresThrowable() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ignoresThrowable'");
    }

  

    
}
    