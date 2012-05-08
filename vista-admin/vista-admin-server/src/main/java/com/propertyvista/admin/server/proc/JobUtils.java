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

import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.domain.scheduler.TriggerSchedule;

public class JobUtils {

    private static final Logger log = LoggerFactory.getLogger(JobUtils.class);

    public static JobKey getJobKey(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
        return new JobKey(trigger.getPrimaryKey().toString(), "VistaJob");
    }

    public static JobDetail getJobDetail(com.propertyvista.admin.domain.scheduler.Trigger trigger) throws SchedulerException {
        return SchedulerHelper.getScheduler().getJobDetail(getJobKey(trigger));
    }

    public static void createJobDetail(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
        JobDetail job = JobBuilder.newJob(RunDispatcherJob.class).withIdentity(JobUtils.getJobKey(trigger)).storeDurably()
                .usingJobData(RunDispatcherJob.newJobDataMap(trigger)).build();
        try {
            SchedulerHelper.getScheduler().addJob(job, false);
        } catch (SchedulerException e) {
            log.error("Error", e);
            throw new UserRuntimeException(e.getMessage());
        }
    }

    public static void runNow(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
        try {
            JobDetail jobDetail = SchedulerHelper.getScheduler().getJobDetail(getJobKey(trigger));
            SchedulerHelper.getScheduler().scheduleJob(TriggerBuilder.newTrigger().forJob(jobDetail).startNow().build());
        } catch (SchedulerException e) {
            log.error("Error", e);
            throw new UserRuntimeException(e.getMessage());
        }
    }

    private static Trigger buildTrigger(TriggerSchedule schedule) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger().withIdentity(schedule.id().getStringView(), "ProcessStarter");

        Calendar startTime = new GregorianCalendar();
        startTime.setTime(schedule.startsOn().getValue());

        if (!schedule.time().isNull()) {
            DateUtils.setTime(startTime, schedule.time().getValue());
        }
        builder.startAt(startTime.getTime());

        if (!schedule.endsOn().isNull()) {
            builder.endAt(schedule.endsOn().getValue());
        }
        switch (schedule.repeatType().getValue()) {
        case Daily:
            CalendarIntervalScheduleBuilder daily = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            daily.withIntervalInDays(schedule.repeatEvery().getValue());
            builder.withSchedule(daily);
            break;
        case Weekly:
            CalendarIntervalScheduleBuilder weekly = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            weekly.withIntervalInWeeks(schedule.repeatEvery().getValue());
            builder.withSchedule(weekly);
            break;
        case Monthly:
            CalendarIntervalScheduleBuilder monthly = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            monthly.withIntervalInMonths(schedule.repeatEvery().getValue());
            builder.withSchedule(monthly);
            break;
        case Minute:
            CalendarIntervalScheduleBuilder minute = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            minute.withIntervalInMinutes(schedule.repeatEvery().getValue());
            builder.withSchedule(minute);
            break;
        case Hourly:
            CalendarIntervalScheduleBuilder hourly = CalendarIntervalScheduleBuilder.calendarIntervalSchedule();
            hourly.withIntervalInHours(schedule.repeatEvery().getValue());
            builder.withSchedule(hourly);
            break;
        }
        return builder.build();
    }

    public static void updateSchedule(com.propertyvista.admin.domain.scheduler.Trigger origTrigger,
            com.propertyvista.admin.domain.scheduler.Trigger updateTrigger) {
//        if (origTrigger == null) {
//            if (updateTrigger.schedule().repeatType().getValue() != ScheduleType.Manual) {
//                Trigger quartzTrigger = buildTrigger(updateTrigger);
//                JobDetail job = JobBuilder.newJob(RunDispatcherJob.class).withIdentity(quartzTrigger.getJobKey())
//                        .usingJobData(RunDispatcherJob.newJobDataMap(updateTrigger)).build();
//                try {
//                    SchedulerHelper.getScheduler().scheduleJob(job, quartzTrigger);
//                } catch (SchedulerException e) {
//                    log.error("Error", e);
//                    throw new Error(e);
//                }
//            }
//        } else if (!EntityGraph.fullyEqualValues(origTrigger.schedule(), updateTrigger.schedule())) {
//            try {
//                if (updateTrigger.schedule().repeatType().getValue() == ScheduleType.Manual) {
//                    SchedulerHelper.getScheduler().deleteJob(processJobKey(updateTrigger));
//                } else {
//                    Trigger quartzTrigger = buildTrigger(updateTrigger);
//
//                    if (SchedulerHelper.getScheduler().getJobDetail(processJobKey(updateTrigger)) != null) {
//                        SchedulerHelper.getScheduler().rescheduleJob(quartzTrigger.getKey(), quartzTrigger);
//                    } else {
//                        JobDetail job = JobBuilder.newJob(RunDispatcherJob.class).withIdentity(quartzTrigger.getJobKey())
//                                .usingJobData(RunDispatcherJob.newJobDataMap(updateTrigger)).build();
//                        SchedulerHelper.getScheduler().scheduleJob(job, quartzTrigger);
//                    }
//
//                }
//            } catch (SchedulerException e) {
//                log.error("Error", e);
//                throw new Error(e);
//            }
//        }
    }

    public static void getScheduleDetails(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
//        trigger.schedule().nextFireTime().setValue(null);
//        if (trigger.schedule().repeatType().getValue() != ScheduleType.Manual) {
//            try {
//                for (Trigger quartzTrigger : SchedulerHelper.getScheduler().getTriggersOfJob(processJobKey(trigger))) {
//                    Date nft = quartzTrigger.getNextFireTime();
//                    if ((nft != null) && (trigger.schedule().nextFireTime().isNull() || nft.before(trigger.schedule().nextFireTime().getValue()))) {
//                        trigger.schedule().nextFireTime().setValue(nft);
//                    }
//                }
//            } catch (SchedulerException e) {
//                log.error("Error", e);
//            }
//        }
    }
}
