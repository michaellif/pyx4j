/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.server.services.sim;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.scheduler.RunStats;
import com.propertyvista.admin.rpc.services.sim.SimulatedDataPreloadService;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.server.jobs.UpdateArrearsProcess;

public class SimulatedDataPreloadServiceImpl implements SimulatedDataPreloadService {

    private static final Logger log = LoggerFactory.getLogger(SimulatedDataPreloadServiceImpl.class);

    private static Object mutex = new Object();

    private final Object statusMutex = new Object();

    private static boolean run = false;

    private static int current = 0;

    private static int max = 0;

    private static ArrearsGenerationProcess process;

    @Override
    public void generateArrearsHistory(AsyncCallback<VoidSerializable> callback) {
        if (false) {
            try {
                NamespaceManager.setNamespace("vista");
                if (!run) {
                    run = true;

                    setProgress(0, 365);

                    UpdateArrearsProcess updateArrearsProcess = new UpdateArrearsProcess();
                    PmcProcessContext context = new PmcProcessContext(EntityFactory.create(RunStats.class), new Date());
                    GregorianCalendar cal = new GregorianCalendar();
                    cal.add(Calendar.YEAR, -2);
                    LogicalDate today = new LogicalDate();
                    LogicalDate simToday = new LogicalDate(cal.getTime());

                    while (run & !simToday.after(today)) {
                        boolean error = false;
                        try {
                            Persistence.service().setTransactionSystemTime(simToday);
                            updateArrearsProcess.executePmcJob(context);
                        } catch (Throwable caught) {
                            caught.printStackTrace();
                        }

                        if (!error) {
                            log.info("simulated data preloader: finished update arrears for {}", new LogicalDate(cal.getTime()));
                        } else {
                            log.error("simulated data preloader: failed up update arrears for {}", new LogicalDate(cal.getTime()));
                        }
                        cal.add(Calendar.MONTH, 1);
                        simToday = new LogicalDate(cal.getTime());
                        incProgress();
                    }
                    run = false;
                    Persistence.service().setTransactionSystemTime(null);

                } else {
                    run = false;
                }

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
            }
        }
        synchronized (mutex) {
            if (process == null || !process.isAlive()) {
                process = new ArrearsGenerationProcess();
                process.start();
            } else {
                process.interrupt();
            }
        }
        callback.onSuccess(null);
    }

    @Override
    public void getArrearsHistoryGenerationProgress(AsyncCallback<Vector<Integer>> callback) {
        Vector<Integer> progress;
        synchronized (mutex) {
            if (process.isAlive()) {
                progress = getProgress();
            } else {
                progress = null;
            }
        }

        callback.onSuccess(progress);
    }

    private void setProgress(int current, int max) {
        synchronized (statusMutex) {
            SimulatedDataPreloadServiceImpl.current = current;
            SimulatedDataPreloadServiceImpl.max = max;
        }
    }

    private void incProgress() {
        synchronized (statusMutex) {
            ++current;
        }
    }

    private Vector<Integer> getProgress() {
        synchronized (statusMutex) {
            Vector<Integer> progress = new Vector<Integer>(2);
            progress.add(current);
            progress.add(max);
            return progress;
        }
    }

    private class ArrearsGenerationProcess extends Thread {

        @Override
        public void run() {
            try {
                Lifecycle.startElevatedUserContext();
                NamespaceManager.setNamespace("vista");
                Persistence.service().startBackgroundProcessTransaction();

                UpdateArrearsProcess updateArrearsProcess = new UpdateArrearsProcess();
                PmcProcessContext context = new PmcProcessContext(EntityFactory.create(RunStats.class), new Date());
                GregorianCalendar cal = new GregorianCalendar();
                int numOfYearsBack = 3;
                cal.add(Calendar.YEAR, -numOfYearsBack);
                setProgress(0, numOfYearsBack * 12);
                LogicalDate today = new LogicalDate();
                LogicalDate simToday = new LogicalDate(cal.getTime());

                while (!isInterrupted() & !simToday.after(today)) {
                    boolean error = false;
                    try {
                        Persistence.service().setTransactionSystemTime(simToday);
                        updateArrearsProcess.executePmcJob(context);
                    } catch (Throwable caught) {
                        caught.printStackTrace();
                    }

                    if (!error) {
                        log.info("simulated data preloader: finished update arrears for {}", new LogicalDate(cal.getTime()));
                    } else {
                        log.error("simulated data preloader: failed up update arrears for {}", new LogicalDate(cal.getTime()));
                    }
                    cal.add(Calendar.MONTH, 1);
                    simToday = new LogicalDate(cal.getTime());
                    incProgress();
                }
                Persistence.service().setTransactionSystemTime(null);
            } finally {
                Persistence.service().endTransaction();
                Lifecycle.endContext();
            }
        }
    }
}
