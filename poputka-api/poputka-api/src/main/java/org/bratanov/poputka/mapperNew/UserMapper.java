package org.bratanov.poputka.mapperNew;

import org.bratanov.poputka.domain.dto.UserEditDto;
import org.bratanov.poputka.domain.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

      UserEditDto entityToDto(UserEntity userEntity);

}
