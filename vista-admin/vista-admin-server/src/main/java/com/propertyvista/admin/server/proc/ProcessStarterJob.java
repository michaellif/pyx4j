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

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.admin.domain.proc.Process;

public class ProcessStarterJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(ProcessStarterJob.class);

    public static JobDataMap newJobDataMap(final Process process) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobData.Process.name(), process.getPrimaryKey().asLong());
        return jobDataMap;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub
    }

}
