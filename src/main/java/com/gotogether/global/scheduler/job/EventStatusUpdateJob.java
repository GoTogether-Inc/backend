package com.gotogether.global.scheduler.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.gotogether.domain.event.service.EventService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventStatusUpdateJob extends QuartzJobBean {

	private final EventService eventService;

	@Override
	@Transactional
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Long eventId = context.getJobDetail().getJobDataMap().getLong("eventId");
		eventService.updateEventStatusToCompleted(eventId);
	}
}