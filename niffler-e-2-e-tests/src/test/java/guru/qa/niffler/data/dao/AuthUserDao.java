package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.Optional;

public interface AuthUserDao {

    AuthUserEntity create(AuthUserEntity user);

    Optional<AuthUserEntity> findByUsername(String username);

    void delete(AuthUserEntity user);
}
