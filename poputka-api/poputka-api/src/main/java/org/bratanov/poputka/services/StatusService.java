package org.bratanov.poputka.services;

import org.bratanov.poputka.domain.entities.StatusEntity;
import org.springframework.stereotype.Service;

@Service
public interface StatusService {

    StatusEntity getCreatedStatus();

}
