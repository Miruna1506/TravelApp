package org.miru.repository;

import org.miru.model.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

    Optional<Itinerary> findTopByOrderByIdDesc();

    List<Itinerary> findByDestinationContainingIgnoreCase(String destination);
}