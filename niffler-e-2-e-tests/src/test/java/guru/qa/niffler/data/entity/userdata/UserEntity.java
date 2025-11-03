package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Setter
@Getter
public class UserEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstName;
    private String surname;
    private byte[] photo;
    private byte[] photoSmall;
    private String fullName;

    public static UserEntity fromJson(UserJson json) {
        UserEntity entity = new UserEntity();
        entity.setId(json.id());
        entity.setUsername(json.username());
        entity.setCurrency(json.currency());
        entity.setFirstName(json.firstname());
        entity.setSurname(json.surname());
        entity.setPhoto(json.photo() != null ? json.photo().getBytes(StandardCharsets.UTF_8) : null);
        entity.setPhotoSmall(json.photoSmall() != null ? json.photoSmall().getBytes(StandardCharsets.UTF_8) : null);
        entity.setFullName(json.fullname());
        return entity;
    }
}
