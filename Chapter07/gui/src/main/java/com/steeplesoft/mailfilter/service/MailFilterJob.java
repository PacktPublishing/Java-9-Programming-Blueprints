package com.steeplesoft.mailfilter.service;

import com.steeplesoft.mailfilter.MailFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jason
 */
public class MailFilterJob implements Job {
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        MailFilter filter = new MailFilter();
        filter.run();
    }
}
