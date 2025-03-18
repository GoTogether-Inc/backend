package com.gotogether.global.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class QuartzConfig {
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
		var schedulerFactoryBean = new SchedulerFactoryBean();
		var jobFactory = new SpringBeanJobFactory();

		jobFactory.setApplicationContext(applicationContext);

		schedulerFactoryBean.setJobFactory(jobFactory);
		schedulerFactoryBean.setApplicationContext(applicationContext);

		schedulerFactoryBean.setOverwriteExistingJobs(false);
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);

		return schedulerFactoryBean;
	}

	@Bean
	public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
		Scheduler scheduler = factory.getScheduler();
		scheduler.start();
		return scheduler;
	}
}