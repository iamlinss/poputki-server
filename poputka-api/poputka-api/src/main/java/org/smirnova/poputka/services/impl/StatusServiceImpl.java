package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.StatusEntity;
import org.smirnova.poputka.repositories.StatusRepository;
import org.smirnova.poputka.services.StatusService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;

    @Override
    public StatusEntity getCreatedStatus() {
        Optional<StatusEntity> createdStatus = statusRepository.findById(1L);
        return createdStatus.orElse(null);
    }
}
