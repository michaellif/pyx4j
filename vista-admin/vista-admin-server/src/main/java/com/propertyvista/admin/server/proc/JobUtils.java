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
import java.util.List;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.domain.scheduler.ScheduleType;
import com.propertyvista.admin.domain.scheduler.TriggerSchedule;

public class JobUtils {

    private static final Logger log = LoggerFactory.getLogger(JobUtils.class);

    public static JobKey getJobKey(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
        return new JobKey(trigger.getPrimaryKey().toString(), "VistaJob");
    }

    public static TriggerKey getTriggerKey(TriggerSchedule schedule) {
        return new TriggerKey(schedule.getPrimaryKey().toString(), "VistaTrigger");
    }

    public static JobDetail getJobDetail(com.propertyvista.admin.domain.scheduler.Trigger trigger) throws SchedulerException {
        return SchedulerHelper.getScheduler().getJobDetail(getJobKey(trigger));
    }

    public static JobDetail createJobDetail(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
        JobDetail job = JobBuilder.newJob(PmcProcessDispatcherJob.class).withIdentity(JobUtils.getJobKey(trigger)).storeDurably()
                .usingJobData(PmcProcessDispatcherJob.newJobDataMap(trigger)).build();
        try {
            SchedulerHelper.getScheduler().addJob(job, false);
        } catch (SchedulerException e) {
            log.error("Error", e);
            throw new UserRuntimeException(e.getMessage());
        }
        return job;
    }

    public static void runNow(com.propertyvista.admin.domain.scheduler.Trigger trigger, Date executionDate) {
        try {
            JobDetail jobDetail = getJobDetail(trigger);
            if (jobDetail == null) {
                jobDetail = createJobDetail(trigger);
            }
            TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger().forJob(jobDetail).startNow();
            if (executionDate != null) {
                tb.usingJobData(JobData.forDate.name(), executionDate.getTime());
            }
            SchedulerHelper.getScheduler().scheduleJob(tb.build());
        } catch (SchedulerException e) {
            log.error("Error", e);
            throw new UserRuntimeException(e.getMessage());
        }
    }

    private static Trigger buildTrigger(TriggerSchedule schedule, JobKey keyOfJobToFire) {
        TriggerBuilder<Trigger> builder = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(schedule)).forJob(keyOfJobToFire);

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
        try {
            JobDetail jobDetail = getJobDetail(updateTrigger);
            if (jobDetail == null) {
                jobDetail = createJobDetail(updateTrigger);
            }

            for (TriggerSchedule updateSchedule : updateTrigger.schedules()) {
                TriggerSchedule origSchedule = null;
                if (origTrigger != null) {
                    origSchedule = origTrigger.schedules().get(updateSchedule);
                }
                if (origSchedule == null) {
                    createTrigger(updateSchedule, jobDetail.getKey());
                } else {
                    updateTrigger(origSchedule, updateSchedule, jobDetail.getKey());
                }
            }

            // Remove removed triggers
            if (origTrigger != null) {
                for (TriggerSchedule origSchedule : origTrigger.schedules()) {
                    if (!updateTrigger.schedules().contains(origSchedule)) {
                        removeTrigger(origSchedule, jobDetail.getKey());
                    }
                }
            }

        } catch (SchedulerException e) {
            log.error("Error", e);
            throw new UserRuntimeException(e.getMessage());
        }
    }

    private static void createTrigger(TriggerSchedule updateSchedule, JobKey keyOfJobToFire) throws SchedulerException {
        if (updateSchedule.repeatType().getValue() != ScheduleType.Manual) {
            Trigger quartzTrigger = buildTrigger(updateSchedule, keyOfJobToFire);
            SchedulerHelper.getScheduler().scheduleJob(quartzTrigger);
        }
    }

    private static void removeTrigger(TriggerSchedule schedule, JobKey keyOfJobToFire) throws SchedulerException {
        SchedulerHelper.getScheduler().unscheduleJob(getTriggerKey(schedule));
    }

    private static void updateTrigger(TriggerSchedule origSchedule, TriggerSchedule updateSchedule, JobKey keyOfJobToFire) throws SchedulerException {
        if (!EntityGraph.fullyEqualValues(origSchedule, updateSchedule)) {
            SchedulerHelper.getScheduler().unscheduleJob(getTriggerKey(updateSchedule));
            if (updateSchedule.repeatType().getValue() != ScheduleType.Manual) {
                Trigger quartzTrigger = buildTrigger(updateSchedule, keyOfJobToFire);
                SchedulerHelper.getScheduler().scheduleJob(quartzTrigger);
            }
        }
    }

    public static void getScheduleDetails(com.propertyvista.admin.domain.scheduler.Trigger trigger) {
        List<? extends Trigger> triggers;
        try {
            triggers = SchedulerHelper.getScheduler().getTriggersOfJob(getJobKey(trigger));
        } catch (SchedulerException e) {
            return;
        }
        for (TriggerSchedule schedule : trigger.schedules()) {
            schedule.nextFireTime().setValue(null);
            if (schedule.repeatType().getValue() != ScheduleType.Manual) {
                for (Trigger quartzTrigger : triggers) {
                    if (quartzTrigger.getKey().equals(getTriggerKey(schedule))) {
                        Date nft = quartzTrigger.getNextFireTime();
                        if ((nft != null) && (schedule.nextFireTime().isNull() || nft.before(schedule.nextFireTime().getValue()))) {
                            schedule.nextFireTime().setValue(nft);
                        }
                    }
                }
            }
        }
    }
}
