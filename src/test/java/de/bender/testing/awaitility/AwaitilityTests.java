package de.bender.testing.awaitility;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Future.State.SUCCESS;

public class AwaitilityTests {

    ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    @DisplayName("You can wait for asynchronous execution to finish (will be polled in the background)")
    void assertResultOfAsyncExecution() {
        // when
        Future<String> submit = executorService.submit(() -> {
            Thread.sleep(Duration.of(2, ChronoUnit.SECONDS));
            return "This is Sparta!!!";
        });

        // then
        Awaitility.await()
                .atLeast(1, TimeUnit.SECONDS)       // fails if method returns sooner !!
                .atMost(8, TimeUnit.SECONDS)        // max time to wait for result
            // here we do a trick - awaitility polls frequently ... hence we mustn't call .get() here since that'll
            // block - so this won't work
            // .until(() -> submit.get().equals("This is Sparta!!!"));
            .until(() -> submit.state().equals(SUCCESS) && submit.get().equals("This is Sparta!!!"));
    }
}
