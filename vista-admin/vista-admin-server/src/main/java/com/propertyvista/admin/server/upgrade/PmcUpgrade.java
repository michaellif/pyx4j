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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.VistaNamespace;

public class PmcUpgrade {

    private final static Logger log = LoggerFactory.getLogger(PmcUpgrade.class);

    public static void upgradeDate(Pmc pmc) {
        UpgradeProcedure procedure = getUpgradeProcedure(pmc.schemaVersion().getValue());
        if (procedure != null) {
            int stepsTotal = procedure.getUpgradeStepsCount();
            int startFrom = pmc.schemaDataUpgradeSteps().getValue(1);
            for (int step = startFrom; step <= stepsTotal; step++) {
                if (!runOneStepInTransaction(pmc, procedure, step)) {
                    break;
                }
            }
        }
    }

    private static boolean runOneStepInTransaction(Pmc pmc, UpgradeProcedure procedure, int step) {
        try {
            NamespaceManager.setNamespace(pmc.namespace().getValue());

            procedure.runUpgradeStep(step);

            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            pmc.schemaDataUpgradeSteps().setValue(step);
            Persistence.service().commit();

            return true;
        } catch (Throwable e) {
            log.error("runUpgradeStep error", e);
            Persistence.service().rollback();
            return false;
        }
    }

    private static UpgradeProcedure getUpgradeProcedure(String schemaVersion) {
        // TODO Auto-generated method stub
        return null;
    }
}
