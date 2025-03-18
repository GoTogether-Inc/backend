package com.gotogether.domain.reservationemail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.reservationemail.entity.ReservationEmail;
import com.gotogether.domain.reservationemail.entity.ReservationEmailStatus;

@Repository
public interface ReservationEmailRepository extends JpaRepository<ReservationEmail, Long> {
	List<ReservationEmail> findByEventId(Long eventId);

	List<ReservationEmail> findByEventIdAndStatus(Long eventId, ReservationEmailStatus status);
}