package guru.qa.niffler.util;

import com.github.javafaker.Faker;

public class FakerUtils {

    private static final Faker FAKER = new Faker();

    public static String getUserName() {
        return FAKER.name().username();
    }

    public static String getPassword() {
        return FAKER.internet().password(3, 12);
    }

    public static String getCategory() {
        return FAKER.commerce().department();
    }
}
