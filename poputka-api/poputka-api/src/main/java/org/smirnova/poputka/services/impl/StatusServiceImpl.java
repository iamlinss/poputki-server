package org.smirnova.poputka.services.impl;

import lombok.RequiredArgsConstructor;
import org.smirnova.poputka.domain.entities.StatusEntity;
import org.smirnova.poputka.repositories.StatusRepository;
import org.smirnova.poputka.services.StatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Override
    public List<StatusEntity> findAllStatuses() {
        return statusRepository.findAll();
    }

    @Override
    public StatusEntity createStatus(StatusEntity statusEntity) {
        return statusRepository.save(statusEntity);
    }

    @Override
    public Optional<StatusEntity> updateStatus(Long id, StatusEntity statusEntity) {
        return statusRepository.findById(id)
                .map(existingStatus -> {
                    existingStatus.setStatus(statusEntity.getStatus());
                    return statusRepository.save(existingStatus);
                });
    }

    @Override
    public boolean deleteStatus(Long id) {
        if (statusRepository.existsById(id)) {
            statusRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
