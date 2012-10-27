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
package com.propertyvista.admin.server.upgrade.u_1_0_5;

import com.propertyvista.admin.server.upgrade.UpgradeProcedure;
import com.propertyvista.portal.server.preloader.DashboardPreloader;

public class UpgradeProcedure105 implements UpgradeProcedure {

    @Override
    public int getUpgradeStepsCount() {
        return 1;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runDashboardPreload();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void runDashboardPreload() {
        new DashboardPreloader().create();
    }

}
