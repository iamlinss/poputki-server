package org.smirnova.poputka.mappers.impl;

import org.smirnova.poputka.domain.dto.trip.TripDto;
import org.smirnova.poputka.domain.entities.TripEntity;
import org.smirnova.poputka.mappers.Mapper;
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
