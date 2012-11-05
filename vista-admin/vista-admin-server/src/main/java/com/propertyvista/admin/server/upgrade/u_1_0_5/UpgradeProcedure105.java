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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.admin.server.upgrade.UpgradeProcedure;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.server.preloader.DashboardPreloader;

public class UpgradeProcedure105 implements UpgradeProcedure {

    @Override
    public int getUpgradeStepsCount() {
        return 2;
    }

    @Override
    public void runUpgradeStep(int upgradeStep) {
        switch (upgradeStep) {
        case 1:
            runDashboardPreload();
            break;
        case 2:
            updateLeaseDates();
            break;
        default:
            throw new IllegalArgumentException();
        }
    }

    private void runDashboardPreload() {
        new DashboardPreloader().create();
    }

    private void updateLeaseDates() {
        EntityQueryCriteria<Lease> criteria = new EntityQueryCriteria<Lease>(Lease.class);
        for (Lease lease : Persistence.service().query(criteria)) {
            ServerSideFactory.create(LeaseFacade.class).updateLeaseDates(lease);
            Persistence.service().merge(lease);
        }
    }
}
