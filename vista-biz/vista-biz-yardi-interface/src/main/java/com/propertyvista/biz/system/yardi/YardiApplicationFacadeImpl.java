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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.Validate;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.CompensationHandler;
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
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.yardi.services.YardiGuestManagementService;
import com.propertyvista.yardi.services.YardiGuestManagementService.SignLeaseResults;

public class YardiApplicationFacadeImpl extends AbstractYardiFacadeImpl implements YardiApplicationFacade {

    private final static Logger log = LoggerFactory.getLogger(YardiApplicationFacadeImpl.class);

    @Override
    public void createApplication(final Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                PmcYardiCredential yc = getPmcYardiCredential(lease);
                final Key yardiInterfaceId = yc.getPrimaryKey();
                String pID = YardiGuestManagementService.getInstance().createNewProspect(yc, lease);
                Validate.notNull(pID, "ApplicationId is null");
                // save primary tenant pID as yardiApplicationId
                // We should not copy pID to tenant.yardiApplicantId() since this is indicator if we sent applicants to yardi or not
                lease.leaseApplication().yardiApplicationId().setValue(pID);
                lease.integrationSystemId().setValue(yardiInterfaceId);
                Persistence.service().persist(lease);

                return null;
            }

        });
    }

    @Override
    public void holdUnit(Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        YardiGuestManagementService.getInstance().holdUnit(getPmcYardiCredential(lease), lease);
    }

    @Override
    public void unreserveUnit(Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        YardiGuestManagementService.getInstance().releaseUnit(getPmcYardiCredential(lease), lease);
    }

    @Override
    public void addLeaseParticipants(final Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                Map<Key, String> participants = YardiGuestManagementService.getInstance().addLeaseParticipants(getPmcYardiCredential(lease), lease);

                // save lease participants ids
                Persistence.ensureRetrieve(lease.leaseParticipants(), AttachLevel.Attached);
                for (LeaseParticipant<?> participant : lease.leaseParticipants()) {
                    String yardiApplicantId = participants.get(participant.getPrimaryKey());
                    Validate.notNull(yardiApplicantId, "yardiApplicantId is null");
                    participant.yardiApplicantId().setValue(yardiApplicantId);
                    Persistence.service().persist(participant);
                }

                return null;
            }

        });
    }

    private Lease saveLeaseId(final Lease leaseId, SignLeaseResults signLeaseResults) {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        lease.leaseId().setValue(signLeaseResults.getLeaseId());
        Persistence.service().persist(lease);

        log.info("leaseId assigned {} for {}", lease.leaseId(), lease.leaseApplication().yardiApplicationId());

        Persistence.ensureRetrieve(lease.leaseParticipants(), AttachLevel.Attached);
        for (LeaseParticipant<?> participant : lease.leaseParticipants()) {
            // application must be updated (yardi sync) before approval
            String participantId = signLeaseResults.getParticipants().get(participant.getPrimaryKey());
            Validate.notNull(participantId, "ParticipantId  is null");
            participant.participantId().setValue(participantId);
            Persistence.service().persist(participant);
            log.info("participantId assigned {} for {} in leaseId {}", participant.participantId(), participant.yardiApplicantId(), lease.leaseId());
        }
        return lease;
    }

    @Override
    public Lease approveApplication(final Lease lease) throws YardiServiceException {
        if (!lease.leaseApplication().yardiApplicationId().getValue("").startsWith("p")) {
            throw new UserRuntimeException("Invalid Lease Application id: " + lease.leaseApplication().yardiApplicationId().getValue());
        }

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        final SignLeaseResults signLeaseResults = YardiGuestManagementService.getInstance().signLease(getPmcYardiCredential(lease), lease);

        // Save even if external transaction failed
        UnitOfWork.addTransactionCompensationHandler(new CompensationHandler() {

            @Override
            public Void execute() throws RuntimeException {
                saveLeaseId(lease, signLeaseResults);
                return null;
            }
        });

        return saveLeaseId(lease, signLeaseResults);
    }

    @Override
    public void validateApplicationAcceptance(Building buildingId) throws UserRuntimeException {
        Building building = Persistence.service().retrieve(Building.class, buildingId.getPrimaryKey());
        PmcYardiCredential yc = VistaDeployment.getPmcYardiCredential(building);
        try {
            YardiGuestManagementService.getInstance().validateSettings(yc, building.propertyCode().getValue());
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        }
    }

}
