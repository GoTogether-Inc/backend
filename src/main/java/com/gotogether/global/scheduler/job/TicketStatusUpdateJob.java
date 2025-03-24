package com.gotogether.global.scheduler.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.gotogether.domain.ticket.service.TicketService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketStatusUpdateJob extends QuartzJobBean {

	private final TicketService ticketService;

	@Override
	@Transactional
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Long ticketId = context.getJobDetail().getJobDataMap().getLong("ticketId");
		ticketService.updateTicketStatusToCompleted(ticketId);
	}
}
