/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-29
 * @author vlads
 */
package com.propertyvista.preloader;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.server.jobs.UpdateArrearsProcess;

public class UpdateArrearsHistoryDevPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        if (!config().mockupData) {
            return null;
        }

        UpdateArrearsProcess updateArrearsProcess = new UpdateArrearsProcess();
        PmcProcessContext context = new PmcProcessContext(new Date());
        GregorianCalendar cal = new GregorianCalendar();

        cal.add(Calendar.YEAR, -config().updateArrearsHistorNumOfYearsBack);
        LogicalDate today = new LogicalDate();
        LogicalDate simToday = new LogicalDate(cal.getTime());

        while (!simToday.after(today)) {
            SystemDateManager.setDate(simToday);
            updateArrearsProcess.executePmcJob(context);
            cal.add(Calendar.MONTH, 1);
            simToday = new LogicalDate(cal.getTime());
        }
        SystemDateManager.resetDate();

        return "ArearsHistory Updated";
    }

    @Override
    public String delete() {
        return null;
    }

}
