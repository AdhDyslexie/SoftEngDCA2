package appender;

import java.io.Serializable;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;
// import java.util.logging.Logger;

import org.apache.logging.log4j.core.layout.PatternLayout;

// Needs to integrate with both built in appenders and custom MemAppender

public class VelocityLayout { // implements Layout<String> {

    private VelocityEngine velocityEngine;

    public VelocityLayout() {
        velocityEngine = new VelocityEngine();
        velocityEngine.init();
    }

    public String render(LogEvent event) {
        Template template = velocityEngine.getTemplate("logEvent.vm");
        VelocityContext context = new VelocityContext();
        context.put("event", event);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    
}
    