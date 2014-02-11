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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.system.AbstractYardiFacadeImpl;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.services.YardiGuestManagementService;

public class YardiApplicationFacadeImpl extends AbstractYardiFacadeImpl implements YardiApplicationFacade {

    @Override
    public void createApplication(final Lease leaseId) throws YardiServiceException {

        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {

                if (!lease.leaseId().isNull()) {
                    throw new UserRuntimeException("New Application should not have id: " + lease.leaseId().getValue());
                }
                PmcYardiCredential yc = getPmcYardiCredential(lease);
                String pId = YardiGuestManagementService.getInstance().createNewProspect(yc, lease);
                lease.leaseApplication().yardiApplicationId().setValue(pId);

                // Consider ...
                //ServerSideFactory.create(LeaseFacade.class).persist(lease);
                Persistence.service().persist(lease.leaseApplication());

                return null;
            }

        });
    }

    @Override
    public void holdUnit(Lease lease) throws YardiServiceException {
        if (lease.leaseId().isNull() || !lease.leaseId().getValue().startsWith("p")) {
            throw new UserRuntimeException("Invalid lease id: " + lease.leaseId().getValue());
        }

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        PmcYardiCredential yc = getPmcYardiCredential(lease);
        YardiGuestManagementService.getInstance().holdUnit(yc, lease);
    }

    @Override
    public void unreserveUnit(Lease leaseId) throws YardiServiceException {
        // TODO
    }

    @Override
    public Lease approveApplication(Lease lease) throws YardiServiceException {
        if (lease.leaseId().isNull() || !lease.leaseId().getValue().startsWith("p")) {
            throw new UserRuntimeException("Invalid lease id: " + lease.leaseId().getValue());
        }

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        PmcYardiCredential yc = getPmcYardiCredential(lease);
        String tId = YardiGuestManagementService.getInstance().signLease(yc, lease);
        lease.leaseId().setValue(tId);
        return lease;
    }

    @Override
    public void validateApplicationAcceptance(Building buildingId) throws UserRuntimeException {
        Building building = Persistence.service().retrieve(Building.class, buildingId.getPrimaryKey());
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(building);
        try {
            YardiGuestManagementService.getInstance().validateSettings(yc, building.propertyCode().getValue());
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(e.getMessage());
        }
    }

}
