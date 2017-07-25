package com.steeplesoft.mailfilter.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author jason
 */
    public class MailFilterService {
        public static void main(String[] args) {
            try {
                final Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                final JobDetail job = JobBuilder.newJob(MailFilterJob.class).build();
                final Trigger trigger = TriggerBuilder.newTrigger()
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(15)
                                .repeatForever())
                        .build();
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException ex) {
                Logger.getLogger(MailFilterService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
