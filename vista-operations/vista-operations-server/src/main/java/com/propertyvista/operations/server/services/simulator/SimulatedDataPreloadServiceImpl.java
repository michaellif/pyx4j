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
package com.propertyvista.operations.server.services.simulator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.operations.rpc.services.simulator.SimulatedDataPreloadService;
import com.propertyvista.server.jobs.PmcProcessContext;
import com.propertyvista.server.jobs.UpdateArrearsProcess;

//TODO Remove , use data preloaders
@Deprecated
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
        if (ApplicationMode.isDevelopment()) {
            synchronized (mutex) {
                if (process == null || !process.isAlive()) {
                    process = new ArrearsGenerationProcess();
                    process.start();
                } else {
                    process.interrupt();
                }
            }
        } else {
            throw new Error("this functionality is only for development");
        }
        callback.onSuccess(null);
    }

    @Override
    public void getArrearsHistoryGenerationProgress(AsyncCallback<Vector<Integer>> callback) {
        if (ApplicationMode.isDevelopment()) {
            Vector<Integer> progress;
            synchronized (mutex) {
                if (process.isAlive()) {
                    progress = getProgress();
                } else {
                    progress = null;
                }
            }

            callback.onSuccess(progress);
        } else {
            callback.onSuccess(new Vector<Integer>());
        }
    }

    @Override
    public void generateMaintenanceRequests(AsyncCallback<VoidSerializable> callback) {
        if (ApplicationMode.isDevelopment()) {
            NamespaceManager.setNamespace("vista");

            EntityQueryCriteria<MaintenanceRequestCategory> crit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
            crit.add(PropertyCriterion.isNull(crit.proto().subCategories()));
            List<MaintenanceRequestCategory> issueClassifications = Persistence.service().query(crit);
            EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
            leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().status(), Lease.Status.Active));
            List<Lease> leases = Persistence.service().query(leaseCriteria);
            if (leases.size() > 0) {
                final int NUM_OF_DAYS_AGO = 10;
                final long TODAY = new LogicalDate().getTime();
                GregorianCalendar cal = new GregorianCalendar();
                for (cal.add(GregorianCalendar.DAY_OF_YEAR, -NUM_OF_DAYS_AGO); cal.getTimeInMillis() < TODAY; cal.add(GregorianCalendar.DAY_OF_YEAR, 1)) {
                    makeMaintenanceRequest(issueClassifications, leases.get(RandomUtil.randomInt(leases.size())), new LogicalDate(cal.getTime()));
                }
                Persistence.service().commit();
            }
        } else {
            throw new Error("this functionality is only for development");
        }

        callback.onSuccess(null);
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

    private void makeMaintenanceRequest(List<MaintenanceRequestCategory> issueClassifications, Lease lease, Date when) {
        Persistence.service().retrieveMember(lease.leaseParticipants());
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequest(lease.unit());
        maintenanceRequest.reporter().set(lease.leaseParticipants().iterator().next().<Tenant> cast());
        maintenanceRequest.submitted().setValue(when);
        maintenanceRequest.updated().setValue(when);
        maintenanceRequest.description().setValue(RandomUtil.randomLetters(50));
        maintenanceRequest.permissionToEnter().setValue(RandomUtil.randomBoolean());
        maintenanceRequest.petInstructions().setValue(RandomUtil.randomLetters(50));
        maintenanceRequest.category().set(issueClassifications.get(RandomUtil.randomInt(issueClassifications.size())));
        Persistence.service().persist(maintenanceRequest);
    }

    private class ArrearsGenerationProcess extends Thread {

        @Override
        public void run() {
            try {
                Lifecycle.startElevatedUserContext();
                NamespaceManager.setNamespace("vista");
                Persistence.service().startBackgroundProcessTransaction();

                UpdateArrearsProcess updateArrearsProcess = new UpdateArrearsProcess();
                PmcProcessContext context = new PmcProcessContext(new Date());
                GregorianCalendar cal = new GregorianCalendar();
                int numOfYearsBack = 1;
                cal.add(Calendar.YEAR, -numOfYearsBack);
                setProgress(0, numOfYearsBack * 12);
                LogicalDate today = new LogicalDate();
                LogicalDate simToday = new LogicalDate(cal.getTime());

                while (!isInterrupted() & !simToday.after(today)) {
                    boolean error = false;
                    try {
                        SystemDateManager.setDate(simToday);
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
            } finally {
                Persistence.service().endTransaction();
                Lifecycle.endContext();
            }
        }
    }
}
