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
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.services.YardiGuestManagementService;

public class YardiApplicationFacadeImpl implements YardiApplicationFacade {

    @Override
    public Lease createApplication(Lease lease) throws YardiServiceException {
        if (!lease.leaseId().isNull()) {
            throw new UserRuntimeException("New Application should not have id: " + lease.leaseId().getValue());
        }
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(lease.unit().building());
        String pId = YardiGuestManagementService.getInstance().createNewProspect(yc, lease);
        lease.leaseId().setValue(pId);
        return lease;
    }

    @Override
    public void holdUnit(Lease lease) throws YardiServiceException {
        if (lease.leaseId().isNull() || !lease.leaseId().getValue().startsWith("p")) {
            throw new UserRuntimeException("Invalid lease id: " + lease.leaseId().getValue());
        }
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(lease.unit().building());
        YardiGuestManagementService.getInstance().holdUnit(yc, lease);
    }

    @Override
    public Lease approveApplication(Lease lease) throws YardiServiceException {
        if (lease.leaseId().isNull() || !lease.leaseId().getValue().startsWith("p")) {
            throw new UserRuntimeException("Invalid lease id: " + lease.leaseId().getValue());
        }
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(lease.unit().building());
        String tId = YardiGuestManagementService.getInstance().signLease(yc, lease);
        lease.leaseId().setValue(tId);
        return lease;
    }

}
