package de.bender.testing.jfr;

import de.bender.testing.ExampleAdd;
import jdk.jfr.consumer.RecordedEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.moditect.jfrunit.EnableEvent;
import org.moditect.jfrunit.JfrEventTest;
import org.moditect.jfrunit.JfrEvents;
import org.moditect.jfrunit.events.*;

import java.time.Duration;
import java.util.Arrays;

import static org.moditect.jfrunit.JfrEventsAssert.assertThat;


@JfrEventTest
public class JfrTest {

    public JfrEvents jfrEvents = new JfrEvents();

    /**
     * You can inspect the JFR file in `/target/jfrunit` and open it i.e. in MissionControl
     */
    @Test
    @EnableEvent(GarbageCollection.EVENT_NAME)
    @EnableEvent(ThreadSleep.EVENT_NAME)
    public void shouldHaveGcAndSleepEvents() throws Exception {

        // when
        System.gc();                        // causes GC-Event
        Thread.sleep(1000);           // causes ThreadSleep-Event

        // then
        jfrEvents.awaitEvents();            // collect 'em

        // import JFRs custom assertThat-method
        assertThat(jfrEvents).contains(JfrEventTypes.GARBAGE_COLLECTION);
        assertThat(jfrEvents).contains(JfrEventTypes.THREAD_SLEEP.withTime(Duration.ofMillis(1000)));
    }


    /**
     * You can inspect the JFR file in `/target/jfrunit` and open it i.e. in MissionControl or
     * even your IDE
     * {@see https://www.morling.dev/blog/towards-continuous-performance-regression-testing/}
     */
    @Test
    @EnableEvent(ObjectAllocationInNewTLAB.EVENT_NAME)
    @EnableEvent(ObjectAllocationOutsideTLAB.EVENT_NAME)
    public void testAllocationEvent() throws Exception {
        String threadName = Thread.currentThread().getName();

        // warm up
        for (int i = 1; i<= 20_000; i++) {
            ExampleAdd.add(Arrays.asList(3, 7, 5, 1), Arrays.asList(6, 9, 4, 2));
        }
        jfrEvents.awaitEvents();        // await all events
        jfrEvents.reset();              // reset all events collected so far (during warm-up)


        // actual tests
        for (int i = 1; i<= 10_000; i++) {
            ExampleAdd.add(Arrays.asList(3, 7, 5, 1), Arrays.asList(6, 9, 4, 2));
        }
        jfrEvents.awaitEvents();

        long sum = jfrEvents.filter(this::isObjectAllocationEvent)
                .filter(event -> event.getThread().getJavaName().equals(threadName))
                .mapToLong(this::getAllocationSize)
                .sum();

        Assertions.assertThat(sum / 10_000).isLessThan(300);
        Assertions.assertThat(sum / 10_000).isGreaterThan(250);
    }
    private boolean isObjectAllocationEvent(RecordedEvent re) {
        String name = re.getEventType().getName();
        return name.equals(ObjectAllocationInNewTLAB.EVENT_NAME) || name.equals(ObjectAllocationOutsideTLAB.EVENT_NAME);
    }
    private long getAllocationSize(RecordedEvent recordedEvent) {
        return recordedEvent.getEventType().getName().equals(ObjectAllocationInNewTLAB.EVENT_NAME)
                ? recordedEvent.getLong("tlabSize")
                : recordedEvent.getLong("allocationSize");
    }
}
