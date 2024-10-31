package org.bratanov.poputka.mappers.impl;

import org.bratanov.poputka.domain.dto.CarDto;
import org.bratanov.poputka.domain.entities.CarEntity;
import org.bratanov.poputka.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CarMapperImpl implements Mapper<CarEntity, CarDto> {

    private ModelMapper modelMapper;

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
