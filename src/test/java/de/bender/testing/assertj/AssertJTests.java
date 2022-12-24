package de.bender.testing.assertj;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertJTests {

    @Test
    @DisplayName("SoftAssertions are lazily evaluated and collect all erors")
    void exampleWhereYouAssertSeveralConditionsSoftly() {
        // when
        int result = Math.max(4, 9);

        assertThat(result).isEqualTo(9);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(result).isEqualTo(9);
        softly.assertThat(result).isNotEqualTo(4);
        softly.assertAll();
    }

    @Test
    @DisplayName("With assertj you can chain several tests for one result in a fluent way (easier to comprehend)")
    void chainingSeveralAssertions() {
        // when
        String result = "This is Sparta!!!";

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .endsWith("!")
                .startsWith("This is")
                .matches("^.*is.*$");
    }

    @Test
    @DisplayName("You can inspect iterables in a way more expressive way")
    void iterableAssertions() {
        // when
        var result = List.of("This", "is", "Sparta!!!");

        // then
        assertThat(result)
                .isNotEmpty()
                .contains("is")
                .doesNotContain("Troja")
                .doesNotHaveDuplicates()
                .doesNotContainNull()
                .hasSize(3);
    }

    @Test
    @DisplayName("Check objects for content-equality without relying on equals/hashcode")
    void objectAssertionsFieldByFieldWithoutRelyingOnEqualsAndHashCode() {
        // when
        var addressOne = new AddressAsClass("Bahnhofstraße", "32a", "94469", "Deggendorf");
        var userOne = new UserAsClass("John", "Doe", addressOne);
        var userTwo = new UserAsClass("John", "Doe", addressOne);

        // then
        var soft = new SoftAssertions();
        soft.assertThat(userOne).isNotEqualTo(userTwo);
        soft.assertThat(userOne).usingRecursiveComparison().isEqualTo(userTwo);
        soft.assertAll();
    }

    @Test
    @DisplayName("You can extract pieces of an object and further drill down assertions on that pieces")
    void objectAssertionsWhenUsingExtractions() {
        // when
        var addressOne = new Address("Bahnhofstraße", "32a", "94469", "Deggendorf");
        var userOne = new User("John", "Doe", addressOne);
        var userTwo = new User("Marry", "Jane", addressOne);

        // then
        assertThat(userOne).isNotEqualTo(userTwo);
        assertThat(userOne).extracting(User::address).isEqualTo(addressOne);
        assertThat(userTwo).extracting(User::address).isEqualTo(addressOne);
        assertThat(userTwo).hasFieldOrProperty("firstName").isNotNull();
    }


    record User(String firstName, String lastName, Address address) {}
    record Address(String street, String number, String postalCode, String town) {}

    static class UserAsClass {
        String firstName;
        String lastName;
        AddressAsClass address;

        public UserAsClass(String firstName, String lastName, AddressAsClass address) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
        }
    }
    static class AddressAsClass {
        String street;
        String number;
        String postalCode;
        String town;

        public AddressAsClass(String street, String number, String postalCode, String town) {
            this.street = street;
            this.number = number;
            this.postalCode = postalCode;
            this.town = town;
        }
    }
}
