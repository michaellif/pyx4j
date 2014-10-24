/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.shared.config.VistaFeatures;

class TenantAccess {

    private final static Logger log = LoggerFactory.getLogger(TenantAccess.class);

    /**
     * @param tenant
     * @return May return null if there are no email of communication to this tenant is disabled.
     */
    static String getActiveEmail(LeaseParticipant<?> tenant) {
        if (!VistaFeatures.instance().tenantEmailEnabled()) {
            log.info("Email will not be sent to LeaseParticipant {} because Tenant communication is disabled on PMC level", tenant.getPrimaryKey());
            return null;
        }

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().units().$().leases().$().leaseParticipants(), tenant);
        if (!ServerSideFactory.create(BuildingFacade.class).isSuspend(criteria)) {
            log.info("Email will not be sent to LeaseParticipant {} because Building is Suspend", tenant.getPrimaryKey());
            return null;
        }
        Persistence.ensureRetrieve(tenant.customer().user(), AttachLevel.Attached);
        // tenant.customer().person().email().getValue(); // This is the same.
        return tenant.customer().user().email().getValue();
    }
}
