package com.gotogether.domain.reservationemail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gotogether.domain.reservationemail.entity.ReservationEmail;

@Repository
public interface ReservationEmailRepository extends JpaRepository<ReservationEmail, Long> {
}