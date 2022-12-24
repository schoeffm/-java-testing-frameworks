package de.bender.testing.junit;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

public class LoggingExtension implements TestInstancePostProcessor {
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        var displayName = context.getDisplayName();
        var tags = context.getTags();

        System.out.printf("Executed for '%s' {%s}%n", displayName, tags);
    }
}
