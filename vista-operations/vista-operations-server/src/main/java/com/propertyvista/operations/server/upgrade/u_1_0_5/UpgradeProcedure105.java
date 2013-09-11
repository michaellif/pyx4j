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
package com.propertyvista.operations.server.upgrade.u_1_0_5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.operations.server.upgrade.UpgradeProcedure;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.server.preloader.DashboardPreloader;

public class UpgradeProcedure105 implements UpgradeProcedure {

    private final static Logger log = LoggerFactory.getLogger(UpgradeProcedure105.class);

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
        ICursorIterator<Lease> cursor = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (cursor.hasNext()) {
                Lease lease = cursor.next();
                try {
                    ServerSideFactory.create(LeaseFacade.class).updateLeaseDates(lease);
                } catch (Throwable e) {
                    log.error("Error migrating lease {}", lease, e);
                    throw new UserRuntimeException("Error in lease " + NamespaceManager.getNamespace() + "." + lease.getPrimaryKey() + "; " + e.getClass()
                            + " " + e.getMessage());
                }
                Persistence.service().merge(lease);
            }
        } finally {
            cursor.close();
        }
    }
}
