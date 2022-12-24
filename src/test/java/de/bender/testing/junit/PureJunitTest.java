package de.bender.testing.junit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;


// you can wrap 'em in a {@link Tags}-annotation - but you don't have to
@Tag("pure")
@Tag("junit")
@ExtendWith(LoggingExtension.class)
public class PureJunitTest {
    @Nested
    class StandardJunit5Tests {
        @Test
        @DisplayName("assertAll is not soft - the first failing assertion prevents further checks")
        void exampleWhereYouAssertSeveralConditions() {
            // when
            int result = Math.max(4, 9);

            /*
             * Does assert all - all or nothing (so no soft-assertion). The first failing assertion brings the
             * execution to an end!!
             */
            Assertions.assertAll(
                    () -> Assertions.assertEquals(9, result),
                    () -> Assertions.assertNotEquals(4, result),
                    () -> Assertions.assertDoesNotThrow(() -> Math.max(4,9))
            );
        }

        @Test
        @Disabled
        @DisplayName("Disabled test since that one (on purpose) fails")
        void exampleWhereYouAssertSeveralConditionsButOneOfThemFails() {
            // when
            int result = Math.max(4, 9);

            /*
             * Does assert all - all or nothing (so no soft-assertion). The first failing assertion brings the
             * execution to an end!!
             */
            Assertions.assertAll(
                    () -> Assertions.assertEquals(4, result),
                    () -> Assertions.assertNotEquals(4, result),
                    () -> Assertions.assertDoesNotThrow(() -> new IllegalStateException())
            );
        }
        @Test
        @DisplayName("Common Integers get cached by the JVM")
        void sameIsComparingObjectReferences() {
            Integer intOne = 23;
            Integer intTwo = 23;
            Assertions.assertSame(intOne, intTwo);

            // integers up to 127 are cached -> hence they always point to the very same instance - above that threshold
            // they're created on demand and thus point to different obejcts
            Integer intOne_nonCached = 128;
            Integer intTwo_nonCached = 128;
            Assertions.assertNotSame(intOne_nonCached, intTwo_nonCached);
        }
    }


    // ------------- for the next few tests we had to add `junit-jupiter-params` dependency as well ------------------

    @Nested
    class ParametrizedTests {

        /**
         * This is just _one_ (useful) example of a parameter-source. You can have a look at the other examples like
         * {@link org.junit.jupiter.params.provider.EmptySource}
         * {@link org.junit.jupiter.params.provider.MethodSource}
         * {@link org.junit.jupiter.params.provider.NullAndEmptySource}
         * {@link org.junit.jupiter.params.provider.CsvSource}
         * {@link org.junit.jupiter.params.provider.CsvFileSource}
         * {@link org.junit.jupiter.params.provider.EnumSource}
         * ...
         * the CSV-source isn't that bad ... compare the next two examples ... what is more concise
         */
        @ParameterizedTest
        @MethodSource("provideStringsForIsBlank")
        void exampleWithParameters(Integer first, Integer second, Integer expected) {
            // when
            int result = Math.max(first, second);

            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, result),
                    () -> Assertions.assertDoesNotThrow(() -> new IllegalStateException())
            );
        }

        static Stream<Arguments> provideStringsForIsBlank() {
            return Stream.of(
                    Arguments.of(1, 6, 6),
                    Arguments.of(6, 1, 6),
                    Arguments.of(1, 1, 1)
            );
        }

        /**
         * IMHO this one could replace many times simple {@link MethodSource}s easily ...
         */
        @ParameterizedTest
        @CsvSource({"1,6,6", "6,1,6", "1,1,1"})
        void exampleWithCsvParameters(Integer first, Integer second, Integer expected) {
            // when
            int result = Math.max(first, second);

            Assertions.assertAll(
                    () -> Assertions.assertEquals(expected, result),
                    () -> Assertions.assertDoesNotThrow(() -> new IllegalStateException())
            );
        }


        @ParameterizedTest
        @NullAndEmptySource
        void isBlank_ShouldReturnTrueForNullAndEmptyStrings(String input) {
            Assertions.assertTrue(input == null || input.equals(""));
        }
    }
}
