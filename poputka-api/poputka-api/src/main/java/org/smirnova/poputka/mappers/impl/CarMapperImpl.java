package org.smirnova.poputka.mappers.impl;

import org.smirnova.poputka.domain.dto.CarDto;
import org.smirnova.poputka.domain.entities.CarEntity;
import org.smirnova.poputka.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CarMapperImpl implements Mapper<CarEntity, CarDto> {

    private final ModelMapper modelMapper;

    public CarMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CarDto mapTo(CarEntity carEntity) {
        return modelMapper.map(carEntity, CarDto.class);
    }

    @Override
    public CarEntity mapFrom(CarDto carDto) {
        return modelMapper.map(carDto, CarEntity.class);
    }
}
