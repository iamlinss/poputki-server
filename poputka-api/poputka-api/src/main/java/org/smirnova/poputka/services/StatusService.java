package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.StatusEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface StatusService {

    StatusEntity getCreatedStatus();

    List<StatusEntity> findAllStatuses();

    StatusEntity createStatus(StatusEntity statusEntity);

    Optional<StatusEntity> updateStatus(Long id, StatusEntity statusEntity);

    boolean deleteStatus(Long id);
}
