package com.gotogether.domain.ticketqrcode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gotogether.domain.ticketqrcode.entity.TicketQrCode;

@Repository
public interface TicketQrCodeRepository extends JpaRepository<TicketQrCode, Long> {
}