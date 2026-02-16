package guru.qa.niffler.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ParametersAreNonnullByDefault
public class OauthUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Base64.Encoder BASE_64_ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Nonnull
    public static String generateCodeVerifier() {
        byte[] codeVerifier = new byte[32];
        SECURE_RANDOM.nextBytes(codeVerifier);
        return BASE_64_ENCODER.encodeToString(codeVerifier);
    }

    @Nonnull
    public static String generateCodeChallenge(String codeVerifier) {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = messageDigest.digest(bytes);
        return BASE_64_ENCODER.encodeToString(digest);
    }
}
