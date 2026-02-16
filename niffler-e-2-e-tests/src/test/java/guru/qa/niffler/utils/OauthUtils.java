package guru.qa.niffler.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ParametersAreNonnullByDefault
public class OauthUtils {

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    @Nonnull
    public static String generateCodeVerifier() {
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return base64Encoder.encodeToString(codeVerifier);
    }

    @SneakyThrows
    @Nonnull
    public static String generateCodeChallenge(String codeVerifier) {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(bytes);
        return base64Encoder.encodeToString(digest);
    }
}
