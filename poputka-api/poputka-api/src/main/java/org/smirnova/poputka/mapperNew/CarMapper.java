package org.smirnova.poputka.mapperNew;

import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.entities.CarEntity;
import org.mapstruct.Mapper;


@Mapper
public interface CarMapper {
    CarEntity dtoToEntity(CarDto carDto);
    CarDto entityToDto(CarEntity cropStatus);
}
