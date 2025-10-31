package guru.qa.niffler.data.entity.auth;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
public class AuthorityEntity implements Serializable {

    private UUID id;
    private String authority;
    private AuthUserEntity user;
}
