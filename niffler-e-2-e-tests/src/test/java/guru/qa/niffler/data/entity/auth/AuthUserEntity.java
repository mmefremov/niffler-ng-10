package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.util.RandomDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
public class AuthUserEntity implements Serializable {

    private UUID id;
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    public static AuthUserEntity fromJson(UserJson json) {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setId(json.id());
        entity.setUsername(json.username());
        entity.setPassword(RandomDataUtils.randomPassword());
        entity.setEnabled(true);
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        return entity;
    }
}
