package de.bender.testing.pqwik;

import net.jqwik.api.*;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyBasedTests {

    /**
     * - cannot be combined with JUnit 5 annotations (like {@link DisplayName}
     * - can also take a {@link net.jqwik.api.footnotes.Footnotes}-parameter to adjust the report output
     */
    @Property
    void absoluteValueOfAllNumbersIsPositive(@ForAll int anInteger) {
        boolean result = (anInteger == -2147483648) ? true : Math.abs(anInteger) >= 0;

        assertThat(result).isTrue();
    }


    /**
     * not much different than an ordinary JUnit 5 Parametrized test - the only addition is a nice report for
     * the execution of that method here.
     */
    @Property
    void favouritePrimes(@ForAll("favouritePrimes") int aFavourite) {
        assertThat(aFavourite).isGreaterThan(0);
    }
    @Provide
    Arbitrary<Integer> favouritePrimes() {
        return Arbitraries.of(3, 5, 7, 13, 17, 23, 41, 101);
    }



    @Property
    boolean concatenatingStringWithInt(@ForAll(supplier = ShortStrings.class) String aShortString,
                                       @ForAll(supplier = TenTo99.class) int aNumber) {
        String concatenated = aShortString + aNumber;
        return concatenated.length() > 2 && concatenated.length() < 11;
    }
    static class ShortStrings implements ArbitrarySupplier<String> {
        @Override
        public Arbitrary<String> get() {
            return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(8);
        }
    }
    static class TenTo99 implements ArbitrarySupplier<Integer> {
        @Override
        public Arbitrary<Integer> get() {
            return Arbitraries.integers().between(10, 99);
        }
    }
}
