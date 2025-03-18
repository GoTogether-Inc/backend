package com.gotogether.domain.reservationemail.scheduler;

import java.util.Date;

public interface EmailScheduler {
	void scheduleEmail(Long reservationEmailId, Date reservationDate);
}