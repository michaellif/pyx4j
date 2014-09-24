/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 29, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageKeyDTO;

class SystemHealthMonitor {

    private final ExecutionMonitor executionMonitor;

    SystemHealthMonitor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    void healthMonitor(LogicalDate forDate) {
        verifyStuckProcesses();
        verifyEncryptedStorage();
        verifyTriggerMisfire(forDate);
    }

    private void verifyStuckProcesses() {
        {
            EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
            criteria.eq(criteria.proto().status(), RunStatus.Running);
            criteria.isNotNull(criteria.proto().trigger().runTimeout());
            for (Run run : Persistence.service().query(criteria)) {
                Date cutOffDate = DateUtils.addMinutes(run.started().getValue(), run.trigger().runTimeout().getValue());
                if (cutOffDate.after(SystemDateManager.getDate())) {
                    ServerSideFactory.create(OperationsAlertFacade.class).record(run, "There are Stuck Run for Trigger {0}, Terminating its execution",
                            run.trigger().name());
                    executionMonitor.addErredEvent("StuckTriggerRun", run.trigger().name().getStringView());
                    ServerSideFactory.create(OperationsTriggerFacade.class).stopRun(run);
                }
            }
        }
    }

    private void verifyEncryptedStorage() {
        EncryptedStorageDTO state = ServerSideFactory.create(EncryptedStorageFacade.class).getSystemState();
        for (EncryptedStorageKeyDTO key : state.keys()) {
            if (!key.decryptionEnabled().getValue() && (key.recordsCount().getValue() > 0)) {
                executionMonitor.addFailedEvent("EncryptedStorage", "EncryptedStorageKey " + key.name().getStringView() + " decryption not active!");
                ServerSideFactory.create(OperationsAlertFacade.class).sendEmailAlert(
                        "Encrypted Storage", //
                        "Encrypted Storage Decryption not active for key {0}, affected records {1}\nAsk SecurityAdmin to activate ASAP", key.name(),
                        key.recordsCount());
            }
        }
    }

    private void verifyTriggerMisfire(LogicalDate forDate) {
        EntityQueryCriteria<Trigger> criteria = EntityQueryCriteria.create(Trigger.class);
        criteria.eq(criteria.proto().scheduleSuspended(), false);
        criteria.in(criteria.proto().triggerType(), PmcProcessType.requiredDaily());
        for (Trigger trigger : Persistence.service().query(criteria)) {
            Date verifyDay = DateUtils.addDays(forDate, -7);
            while (verifyDay.before(forDate)) {
                Calendar dayStart = new GregorianCalendar();
                dayStart.setTime(verifyDay);
                com.pyx4j.gwt.server.DateUtils.dayStart(dayStart);

                Calendar dayEnd = new GregorianCalendar();
                dayEnd.setTime(verifyDay);
                com.pyx4j.gwt.server.DateUtils.dayEnd(dayEnd);

                EntityQueryCriteria<Run> criteria2 = EntityQueryCriteria.create(Run.class);
                criteria2.eq(criteria2.proto().status(), RunStatus.Completed);
                criteria2.ge(criteria2.proto().forDate(), dayStart);
                criteria2.le(criteria2.proto().forDate(), dayEnd);
                criteria2.eq(criteria2.proto().trigger(), trigger);

                if (!Persistence.service().exists(criteria2)) {
                    executionMonitor.addFailedEvent("MissingTriggerRun", "Trigger {0} misfire occurred on {1,date,EEEE, MMMM d, yyyy}", trigger.name(),
                            verifyDay);
                    ServerSideFactory.create(OperationsAlertFacade.class).sendEmailAlert("MissingTriggerRun", //
                            "Trigger {0} misfire occurred on {1,date,EEEE, MMMM d, yyyy}", trigger.name(), verifyDay);
                }

                verifyDay = DateUtils.addDays(verifyDay, 1);
            }

        }
    }

}
