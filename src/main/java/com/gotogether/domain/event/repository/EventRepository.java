package com.gotogether.domain.event.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.event.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	Optional<Event> findByIdAndIsDeletedFalse(Long eventId);
}
