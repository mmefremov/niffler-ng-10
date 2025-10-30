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
        entity.setFirstName(json.firstname());
        entity.setSurname(json.surname());
        entity.setPhoto(json.photo().getBytes(StandardCharsets.UTF_8));
        entity.setPhotoSmall(json.photoSmall().getBytes(StandardCharsets.UTF_8));
        entity.setFullName(json.fullname());
        return entity;
    }
}
