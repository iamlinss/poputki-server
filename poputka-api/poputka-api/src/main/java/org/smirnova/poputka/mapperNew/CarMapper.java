package org.bratanov.poputka.mapperNew;

import org.bratanov.poputka.domain.dto.CarDto;
import org.bratanov.poputka.domain.entities.CarEntity;
import org.mapstruct.Mapper;


@Mapper
public interface CarMapper {
    CarEntity dtoToEntity(CarDto carDto);
    CarDto entityToDto(CarEntity cropStatus);
}
