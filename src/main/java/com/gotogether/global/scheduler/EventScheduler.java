package com.gotogether.global.scheduler;

import java.time.LocalDateTime;

public interface EventScheduler {
	void scheduleEmail(Long reservationEmailId, LocalDateTime reservationDate);

	void scheduleUpdateEventStatus(Long eventId, LocalDateTime eventDate);

	void scheduleUpdateTicketStatus(Long ticketId, LocalDateTime eventDate);
}