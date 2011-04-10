/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 8, 2011
 * @author sergei
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.server.scheduler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.server.maintenance.CleanOrphanApplicationDocumentDataRecordsJob;
import com.propertyvista.server.common.security.DevelopmentSecurity;
import com.pyx4j.config.shared.ApplicationMode;

public class JobScheduler extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private SchedulerFactory schedulerFactory = null;

    private Scheduler scheduler = null;

    private final static Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    @Override
    public void init() throws ServletException {
        logger.info("JobScheduler.init() called");
        try {
            schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            JobDetail job = JobBuilder.newJob(CleanOrphanApplicationDocumentDataRecordsJob.class)
                    .withIdentity("CleanOrphanApplicationDocumentDataRecordsJob", "Maintenance").build();

            //TODO do proper schedule setup
            boolean isTesting = false;
            SimpleTrigger simpleTrigger;
            if (isTesting) {
                // testing schedule
                simpleTrigger = TriggerBuilder.newTrigger().withIdentity("testing", "Maintenance").startAt(DateBuilder.futureDate(15, IntervalUnit.SECOND))
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(30)).build();
            } else {
                // production schedule
                simpleTrigger = TriggerBuilder.newTrigger().withIdentity("dailyRun", "Maintenance").startAt(DateBuilder.tomorrowAt(4, 0, 0)) //start at 4am
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInHours(24)).build();
            }
            scheduler.scheduleJob(job, simpleTrigger);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
