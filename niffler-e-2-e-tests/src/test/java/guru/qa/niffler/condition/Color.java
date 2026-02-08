package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Color {
    YELLOW("rgba(255, 183, 3, 1)"),
    GREEN("rgba(53, 173, 123, 1)"),
    BLUE100("rgba(41, 65, 204, 1)"),
    ORANGE("rgba(251, 133, 0, 1)"),
    AZURE("rgba(33, 158, 188, 1)"),
    BLUE200("rgba(22, 41, 149, 1)"),
    RED("rgba(247, 89, 67, 1)"),
    SKY_BLUE("rgba(99, 181, 226, 1)"),
    PURPLE("rgba(148, 85, 198, 1)");

    public final String rgba;

    public static Color getByRgba(String rgba) {
        return Arrays.stream(values())
                .filter(color -> color.rgba.equals(rgba))
                .findFirst().orElseThrow();
    }
}
