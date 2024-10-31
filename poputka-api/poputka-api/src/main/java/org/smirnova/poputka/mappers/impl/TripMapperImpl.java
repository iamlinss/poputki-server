package org.bratanov.poputka.mappers.impl;

import org.bratanov.poputka.domain.dto.trip.TripDto;
import org.bratanov.poputka.domain.entities.TripEntity;
import org.bratanov.poputka.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TripMapperImpl implements Mapper<TripEntity,TripDto> {

    private ModelMapper modelMapper;

    public TripMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TripDto mapTo(TripEntity tripEntity) {
        return modelMapper.map(tripEntity, TripDto.class);
    }

    @Override
    public TripEntity mapFrom(TripDto tripDto) {
        return modelMapper.map(tripDto, TripEntity.class);
    }

}
