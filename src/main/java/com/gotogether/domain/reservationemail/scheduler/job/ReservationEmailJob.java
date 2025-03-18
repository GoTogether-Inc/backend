package com.gotogether.domain.reservationemail.scheduler.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.gotogether.domain.reservationemail.service.ReservationEmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEmailJob extends QuartzJobBean {

	private final ReservationEmailService reservationEmailService;

	@Override
	@Transactional
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Long reservationEmailId = context.getJobDetail().getJobDataMap().getLong("reservationEmailId");
		reservationEmailService.sendReservationEmail(reservationEmailId);
	}
}