package org.smirnova.poputka.services;

public interface ReviewService {
    String leaveDriverReview(Long passengerId, Integer rating, String comment);
    String leavePassengerReview(Long passengerId, Integer rating, String comment);
}
