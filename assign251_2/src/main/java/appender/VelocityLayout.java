package appender;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

// Needs to integrate with both built in appenders and custom MemAppender

public class VelocityLayout extends Layout {

    private VelocityEngine velocityEngine;
    private Template tmpl;
    private String pattern;
    private static final String TEMPLATE = "src/main/resources/variableLogTemplate.vm";


    /**
     * Constructor for VelocityLayout. Initializes the Velocity engine and sets the pattern.
     * @param pattern The pattern to use for formatting log messages.
     * Can be any of:
     * <ul>
     *   <li><code>$c</code> (logger name)</li>
     *   <li><code>$p</code> (log level)</li>
     *   <li><code>$m</code> (message)</li>
     *   <li><code>$t</code> (thread name)</li>
     *   <li><code>$d</code> (date)</li>
     *   <li><code>$n</code> (new line)</li>
     * </ul>
     */
    public VelocityLayout(String pattern) {
        setPattern(pattern);
        velocityEngine = new VelocityEngine();
        velocityEngine.init();
    }

    /**
     * @brief Set the pattern for the layout & create/update the template file
     * @param pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
        try {
            // Test if file is accessible
            FileWriter fileWriter = new FileWriter(TEMPLATE);
            fileWriter.write(pattern);
            fileWriter.close();
        } 
        catch (IOException e) {
            System.err.println("Template file not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error accessing template file: " + e.getMessage());
        }
    }
    

    @Override
    public String format(LoggingEvent event) {
        tmpl = velocityEngine.getTemplate(TEMPLATE);
        VelocityContext context = new VelocityContext();

        context.put("c", event.getLoggerName());
        context.put("p", event.getLevel());
        context.put("m", event.getMessage());
        context.put("t", event.getThreadName());
        context.put("n", '\n');

        Date date = new Date(event.getTimeStamp());
        context.put("d", date.toString());

        StringWriter writer = new StringWriter();

        tmpl.merge(context, writer);

        return writer.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return true;
    }

    @Override
    public void activateOptions() {
        // No options to activate
    }

    
}
    