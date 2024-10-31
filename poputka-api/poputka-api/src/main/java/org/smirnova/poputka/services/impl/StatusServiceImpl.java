package org.bratanov.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.bratanov.poputka.domain.entities.StatusEntity;
import org.bratanov.poputka.repositories.StatusRepository;
import org.bratanov.poputka.services.StatusService;
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
