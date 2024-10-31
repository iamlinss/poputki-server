package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    UserEntity save(UserEntity userEntity);

    List<UserEntity> findAll();

    Optional<UserEntity> findOne(Long id);

    boolean isExists(Long id);

    void delete(Long id);

    boolean isEmailExists(String email);
}
