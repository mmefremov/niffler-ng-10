package guru.qa.niffler.util;

import com.google.protobuf.GeneratedMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.BiConsumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProtobufBuilder {

    public static <B extends GeneratedMessage.Builder<B>, T> B setIfNotNull(
            B builder, T value, BiConsumer<B, T> setter) {
        if (value != null) {
            setter.accept(builder, value);
        }
        return builder;
    }

    public static <B extends GeneratedMessage.Builder<B>, T extends Enum<T>, E extends Enum<E>> B setEnumIfNotNull(
            B builder, E value, Class<T> targetEnumClass, BiConsumer<B, T> setter) {
        if (value != null) {
            T converted = Enum.valueOf(targetEnumClass, value.name());
            setter.accept(builder, converted);
        }
        return builder;
    }
}
