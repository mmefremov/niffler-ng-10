package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RandomDataUtils {

    private RandomDataUtils() {
    }

    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        return faker.name().username();
    }

    @Nonnull
    public static String randomPassword() {
        return faker.internet().password(3, 12);
    }

    @Nonnull
    public static String randomName() {
        return faker.name().firstName();
    }

    @Nonnull
    public static String randomSurname() {
        return faker.name().lastName();
    }

    @Nonnull
    public static String randomCategoryName() {
        return faker.commerce().department();
    }

    @Nonnull
    public static Long randomInteger() {
        int digits = faker.number().randomDigitNotZero();
        return faker.number().randomNumber(digits, false);
    }

    @Nonnull
    public static String randomSentence(int wordsCount) {
        return faker.lorem().sentence(wordsCount);
    }
}
