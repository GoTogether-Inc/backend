package com.gotogether.global.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
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
			Trigger trigger = createTrigger(triggerIdentity, id, startTime);

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

	private Trigger createTrigger(String triggerIdentity, Long id, Date startTime) {
		return TriggerBuilder.newTrigger()
			.withIdentity(triggerIdentity + "-" + id)
			.startAt(startTime)
			.withSchedule(SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionFireNow())
			.build();
	}

	private void deleteScheduledJob(String jobPrefix, String triggerPrefix, Long id) {
		try {
			JobKey jobKey = JobKey.jobKey(jobPrefix + "-" + id);
			TriggerKey triggerKey = TriggerKey.triggerKey(triggerPrefix + "-" + id);

			if (scheduler.checkExists(triggerKey)) {
				scheduler.unscheduleJob(triggerKey);
			}
			if (scheduler.checkExists(jobKey)) {
				scheduler.deleteJob(jobKey);
			}
		} catch (SchedulerException e) {
			throw new RuntimeException(jobPrefix + " 스케줄 삭제 실패: " + id, e);
		}
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

	@Override
	public void deleteScheduledEmailJob(Long emailId) {
		deleteScheduledJob("reservationEmailJob", "reservationEmailTrigger", emailId);
	}

	@Override
	public void deleteScheduledEventJob(Long eventId) {
		deleteScheduledJob("eventJob", "eventTrigger", eventId);
	}

	@Override
	public void deleteScheduledTicketJob(Long ticketId) {
		deleteScheduledJob("ticketJob", "ticketTrigger", ticketId);
	}
}