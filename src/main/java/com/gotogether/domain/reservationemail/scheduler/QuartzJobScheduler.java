package com.gotogether.domain.reservationemail.scheduler;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import com.gotogether.domain.reservationemail.scheduler.job.ReservationEmailJob;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuartzJobScheduler implements EmailScheduler {
	private final Scheduler scheduler;

	@Override
	public void scheduleEmail(Long reservationEmailId, Date reservationDate) {
		try {
			JobDetail jobDetail = JobBuilder.newJob(ReservationEmailJob.class)
				.withIdentity("reservationEmailJob-" + reservationEmailId)
				.usingJobData("reservationEmailId", reservationEmailId)
				.storeDurably()
				.build();

			Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("reservationEmailTrigger-" + reservationEmailId)
				.startAt(reservationDate)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
				.build();

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException("예약 메일 스케줄링 실패", e);
		}
	}
}