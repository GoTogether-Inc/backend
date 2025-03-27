package com.gotogether.global.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import com.gotogether.global.scheduler.job.EventStatusUpdateJob;
import com.gotogether.global.scheduler.job.ReservationEmailJob;
import com.gotogether.global.scheduler.job.TicketStatusUpdateJob;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuartzJobScheduler implements EventScheduler {
	private final Scheduler scheduler;

	private void scheduleJob(Class<? extends Job> jobClass, String jobIdentity, String triggerIdentity,
		String jobDataKey, Long id, Date startTime) {
		try {
			JobDetail jobDetail = createJobDetail(jobClass, jobIdentity, jobDataKey, id);
			Trigger trigger = createTrigger(triggerIdentity, startTime);

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(jobIdentity + " 스케줄링 실패", e);
		}
	}

	private JobDetail createJobDetail(Class<? extends Job> jobClass, String jobIdentity,
		String jobDataKey, Long id) {
		return JobBuilder.newJob(jobClass)
			.withIdentity(jobIdentity + "-" + id)
			.usingJobData(jobDataKey, id)
			.storeDurably()
			.build();
	}

	private Trigger createTrigger(String triggerIdentity, Date startTime) {
		return TriggerBuilder.newTrigger()
			.withIdentity(triggerIdentity)
			.startAt(startTime)
			.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionFireNow())
			.build();
	}

	@Override
	public void scheduleEmail(Long reservationEmailId, LocalDateTime reservationDate) {
		scheduleJob(ReservationEmailJob.class, "reservationEmailJob", "reservationEmailTrigger",
			"reservationEmailId", reservationEmailId,
			Date.from(reservationDate.atZone(ZoneId.systemDefault()).toInstant()));
	}

	@Override
	public void scheduleUpdateEventStatus(Long eventId, LocalDateTime eventEndDate) {
		scheduleJob(EventStatusUpdateJob.class, "eventJob", "eventTrigger",
			"eventId", eventId,
			Date.from(eventEndDate.atZone(ZoneId.systemDefault()).toInstant()));
	}

	@Override
	public void scheduleUpdateTicketStatus(Long ticketId, LocalDateTime ticketEndDate) {
		scheduleJob(TicketStatusUpdateJob.class, "ticketJob", "ticketTrigger",
			"ticketId", ticketId,
			Date.from(ticketEndDate.atZone(ZoneId.systemDefault()).toInstant()));
	}
}