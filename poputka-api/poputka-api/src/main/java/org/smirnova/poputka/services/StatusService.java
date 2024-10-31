package org.smirnova.poputka.services;

import org.smirnova.poputka.domain.entities.StatusEntity;
import org.springframework.stereotype.Service;

@Service
public interface StatusService {

    StatusEntity getCreatedStatus();

}
