package org.smirnova.poputka.mapperNew;

import org.smirnova.poputka.domain.dto.UserEditDto;
import org.smirnova.poputka.domain.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

      UserEditDto entityToDto(UserEntity userEntity);

}
