package appender;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class TestMemAppender 
{

    @Test
    public void testSingleton()
    {
        MemAppender instance1 = MemAppender.getInstance();
        MemAppender instance2 = MemAppender.getInstance();
        assertSame(instance1, instance2);
    }

    // Add test for adding log events and max size
    

}
