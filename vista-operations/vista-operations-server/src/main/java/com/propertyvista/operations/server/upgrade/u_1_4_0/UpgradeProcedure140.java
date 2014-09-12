/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.upgrade.u_1_4_0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.operations.server.upgrade.UpgradeProcedure;

public class UpgradeProcedure140 implements UpgradeProcedure {

    private final static Logger log = LoggerFactory.getLogger(UpgradeProcedure140.class);

    @Override
    public int getUpgradeStepsCount() {
        return 1;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runLegalTermsPolicyPreloaderPolicyGeneration();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void runLegalTermsPolicyPreloaderPolicyGeneration() {
        log.info("Creating Default CrmRoles");
        CrmRolesPreloader rolesPreloader = new CrmRolesPreloader();
        log.info("Finished Roles creation {}", rolesPreloader.createDefaultRolesDefinition());
    }

}
