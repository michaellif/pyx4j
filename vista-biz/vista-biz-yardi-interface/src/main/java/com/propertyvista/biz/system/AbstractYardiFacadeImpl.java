/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 7, 2014
 * @author vlads
 */
package com.propertyvista.biz.system;

import java.util.Collection;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.yardi.YardiCredentials;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;

public class AbstractYardiFacadeImpl {

    public static PmcYardiCredential getPmcYardiCredential(Lease leaseId) {
        return getPmcYardiCredential(ServerSideFactory.create(LeaseFacade.class).getLeaseBuilding(leaseId));
    }

    public static PmcYardiCredential getPmcYardiCredential(Building buildingId) {
        return YardiCredentials.get(buildingId);
    }

    public static Collection<PmcYardiCredential> getPmcYardiCredentials() {
        return YardiCredentials.getAll();
    }
}
