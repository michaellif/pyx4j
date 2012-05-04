/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.proc;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.quartz.SchedulerHelper;

import com.propertyvista.admin.domain.proc.Process;
import com.propertyvista.admin.domain.proc.ScheduleType;

public class JobUtils {

    private static final Logger log = LoggerFactory.getLogger(JobUtils.class);

    private static JobKey processJobKey(Process process) {
        return new JobKey(process.id().getStringView(), "ProcessStarter");
    }

    private static Trigger buildTrigger(Process process) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger().withIdentity(process.id().getStringView(), "ProcessStarter");
        builder.forJob(processJobKey(process));

        Calendar startTime = new GregorianCalendar();
        startTime.setTime(process.schedule().startsOn().getValue());

        if (!process.schedule().time().isNull()) {
            DateUtils.setTime(startTime, process.schedule().time().getValue());
        }
        builder.startAt(startTime.getTime());

        if (!process.schedule().endsOn().isNull()) {
            builder.endAt(process.schedule().endsOn().getValue());
        }
        switch (process.schedule().repeatType().getValue()) {
        case Daily:
            CalendarIntervalScheduleBuilder daily = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            daily.withIntervalInDays(process.schedule().repeatEvery().getValue());
            builder.withSchedule(daily);
            break;
        case Weekly:
            CalendarIntervalScheduleBuilder weekly = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            weekly.withIntervalInWeeks(process.schedule().repeatEvery().getValue());
            builder.withSchedule(weekly);
            break;
        case Monthly:
            CalendarIntervalScheduleBuilder monthly = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            monthly.withIntervalInMonths(process.schedule().repeatEvery().getValue());
            builder.withSchedule(monthly);
            break;
        case Minute:
            CalendarIntervalScheduleBuilder minute = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            minute.withIntervalInMinutes(process.schedule().repeatEvery().getValue());
            builder.withSchedule(minute);
            break;
        case Hourly:
            CalendarIntervalScheduleBuilder hourly = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            hourly.withIntervalInHours(process.schedule().repeatEvery().getValue());
            builder.withSchedule(hourly);
            break;
        }
        return builder.build();
    }

    public static void updateSchedule(Process origProcess, Process updateProcess) {
        if (origProcess == null) {
            if (updateProcess.schedule().repeatType().getValue() != ScheduleType.Manual) {
                Trigger trigger = buildTrigger(updateProcess);
                JobDetail job = JobBuilder.newJob(ProcessStarterJob.class).withIdentity(trigger.getJobKey())
                        .usingJobData(ProcessStarterJob.newJobDataMap(updateProcess)).build();
                try {
                    SchedulerHelper.getScheduler().scheduleJob(job, trigger);
                } catch (SchedulerException e) {
                    log.error("Error", e);
                    throw new Error(e);
                }
            }
        } else if (!EntityGraph.fullyEqualValues(origProcess.schedule(), updateProcess.schedule())) {
            try {
                if (updateProcess.schedule().repeatType().getValue() == ScheduleType.Manual) {
                    SchedulerHelper.getScheduler().deleteJob(processJobKey(updateProcess));
                } else {
                    Trigger trigger = buildTrigger(updateProcess);

                    if (SchedulerHelper.getScheduler().getJobDetail(processJobKey(updateProcess)) != null) {
                        SchedulerHelper.getScheduler().rescheduleJob(trigger.getKey(), trigger);
                    } else {
                        JobDetail job = JobBuilder.newJob(ProcessStarterJob.class).withIdentity(trigger.getJobKey())
                                .usingJobData(ProcessStarterJob.newJobDataMap(updateProcess)).build();
                        SchedulerHelper.getScheduler().scheduleJob(job, trigger);
                    }

                }
            } catch (SchedulerException e) {
                log.error("Error", e);
                throw new Error(e);
            }
        }
    }

    public static void getScheduleDetails(Process process) {
        process.schedule().nextFireTime().setValue(null);
        if (process.schedule().repeatType().getValue() != ScheduleType.Manual) {
            try {
                for (Trigger trigger : SchedulerHelper.getScheduler().getTriggersOfJob(processJobKey(process))) {
                    Date nft = trigger.getNextFireTime();
                    if ((nft != null) && (process.schedule().nextFireTime().isNull() || nft.before(process.schedule().nextFireTime().getValue()))) {
                        process.schedule().nextFireTime().setValue(nft);
                    }
                }
            } catch (SchedulerException e) {
                log.error("Error", e);
            }
        }
    }
}
