package appender;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;


public class TestMemAppender 
{

    @Test
    public void testSingleton()
    {
        MemAppender instance1 = MemAppender.getInstance();
        MemAppender instance2 = MemAppender.getInstance();
        assertSame(instance1, instance2);
    }


}
