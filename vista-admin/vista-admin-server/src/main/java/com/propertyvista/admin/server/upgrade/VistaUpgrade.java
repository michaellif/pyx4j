/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.admin.server.upgrade;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ApplicationVersion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.server.jobs.TaskRunner;

public class VistaUpgrade {

    private final static Logger log = LoggerFactory.getLogger(VistaUpgrade.class);

    public static void upgradePmcData(Pmc pmc) {
        UpgradeProcedure procedure = VistaUpgradeVersionSelector.getUpgradeProcedure(pmc.schemaVersion().getValue());
        if (procedure != null) {
            int stepsTotal = procedure.getUpgradeStepsCount();
            int startFrom = pmc.schemaDataUpgradeSteps().getValue(0) + 1;
            for (int step = startFrom; step <= stepsTotal; step++) {
                try {
                    runOneStepInTransaction(pmc, procedure, step);
                } catch (Throwable e) {
                    log.error("upgrade error", e);
                    if (e instanceof UserRuntimeException) {
                        throw (UserRuntimeException) e;
                    } else {
                        throw new UserRuntimeException(e.getMessage());
                    }
                }
            }
        }
    }

    public static Integer getPreloadSchemaDataUpgradeSteps() {
        UpgradeProcedure procedure = VistaUpgradeVersionSelector.getUpgradeProcedure(ApplicationVersion.getProductVersion());
        if (procedure != null) {
            return procedure.getUpgradeStepsCount();
        } else {
            return null;
        }
    }

    private static void runOneStepInTransaction(final Pmc pmc, final UpgradeProcedure procedure, final int step) {
        TaskRunner.runAutonomousTransation(pmc.namespace().getValue(), new Callable<Void>() {
            @Override
            public Void call() {
                Persistence.service().startBackgroundProcessTransaction();
                log.info("Execute upgrade Step #{}", step);
                Lifecycle.startElevatedUserContext();
                procedure.runUpgradeStep(step);
                NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
                pmc.schemaDataUpgradeSteps().setValue(step);
                Persistence.service().persist(pmc);
                Persistence.service().commit();
                return null;
            }
        });
    }
}
