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
 */
package com.propertyvista.biz.system.yardi;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.system.AbstractYardiFacadeImpl;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.yardi.services.YardiGuestManagementService;

public class YardiLeaseApplicationFacadeImpl extends AbstractYardiFacadeImpl implements YardiLeaseApplicationFacade {

    private final static Logger log = LoggerFactory.getLogger(YardiLeaseApplicationFacadeImpl.class);

    @Override
    public void createApplication(final Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        final PmcYardiCredential yc = getPmcYardiCredential(lease);

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                try {
                    String pID = YardiGuestManagementService.getInstance().createNewProspect(yc, lease);
                    Validate.notNull(pID, "ApplicationId is null");
                    // save primary tenant pID as yardiApplicationId
                    // pID will be saved in tenant.yardiApplicantId() on the next step after we sent all applicants to yardi
                    lease.leaseApplication().yardiApplicationId().setValue(pID);
                    lease.integrationSystemId().setValue(yc.getPrimaryKey());
                    Persistence.service().persist(lease);
                    log.info("prospect ID assigned {} for application {}", pID, lease.leaseApplication().yardiApplicationId().getValue());
                    return null;
                } catch (RemoteException e) {
                    throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
                }
            }

        });
    }

    @Override
    public void holdUnit(Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        try {
            YardiGuestManagementService.getInstance().holdUnit(getPmcYardiCredential(lease), lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }

    @Override
    public void unreserveUnit(Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        try {
            YardiGuestManagementService.getInstance().releaseUnit(getPmcYardiCredential(lease), lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelApplication(Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        try {
            YardiGuestManagementService.getInstance().cancelApplication(getPmcYardiCredential(lease), lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }

    @Override
    public void declineApplication(Lease leaseId) throws YardiServiceException {
        final Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        try {
            YardiGuestManagementService.getInstance().declineApplication(getPmcYardiCredential(lease), lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }

    @Override
    public void addLeaseParticipants(final Lease leaseId) throws YardiServiceException {
        // load lease to access participants
        final Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        // post lease participants, save participant ids
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                try {
                    Map<Key, String> participants = YardiGuestManagementService.getInstance().addLeaseParticipants(getPmcYardiCredential(lease), lease);

                    // save ids
                    for (LeaseTermParticipant<?> participant : CollectionUtils.union(lease.currentTerm().version().tenants(), lease.currentTerm().version()
                            .guarantors())) {
                        Persistence.ensureRetrieve(participant.leaseParticipant(), AttachLevel.Attached);
                        LeaseParticipant<?> lp = participant.leaseParticipant();
                        String yardiApplicantId = participants.get(lp.getPrimaryKey());
                        Validate.notNull(yardiApplicantId, "yardiApplicantId is null");
                        lp.yardiApplicantId().setValue(yardiApplicantId);
                        Persistence.service().persist(lp);
                    }
                    log.info("lease participant IDs assigned {} for application {}", Arrays.toString(participants.values().toArray()), lease.leaseApplication()
                            .yardiApplicationId().getValue());
                    return null;
                } catch (RemoteException e) {
                    throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
                }
            }

        });
    }

    @Override
    public void approveApplication(final Lease leaseId) throws YardiServiceException {
        final Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);

        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        // sign lease, save lease id
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                try {
                    lease.leaseId().setValue(YardiGuestManagementService.getInstance().signLease(getPmcYardiCredential(lease), lease));
                    Persistence.service().persist(lease);
                    log.info("lease ID assigned {} for application {}", lease.leaseId().getValue(), lease.leaseApplication().yardiApplicationId().getValue());
                    return null;
                } catch (RemoteException e) {
                    throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
                }
            }
        });

        // save lease participant ids
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                try {
                    final Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);
                    Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
                    Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);

                    Map<Key, String> participantIds = YardiGuestManagementService.getInstance().retrieveLeaseResidentIds(getPmcYardiCredential(lease), lease);
                    for (LeaseTermParticipant<?> termParticipant : CollectionUtils.union(lease.currentTerm().version().tenants(), lease.currentTerm().version()
                            .guarantors())) {
                        Persistence.ensureRetrieve(termParticipant.leaseParticipant(), AttachLevel.Attached);
                        LeaseParticipant<?> participant = termParticipant.leaseParticipant();
                        String participantId = participantIds.get(participant.getPrimaryKey());
                        Validate.notNull(participantId, "ParticipantId  is null");
                        participant.participantId().setValue(participantId);
                        Persistence.service().persist(participant);
                        log.info("participantId assigned {} for {} in leaseId {}", participant.participantId(), participant.yardiApplicantId(), lease.leaseId());
                    }
                    return null;
                } catch (RemoteException e) {
                    throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public boolean isLeaseSigned(Lease leaseId) throws YardiServiceException {
        final Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        return YardiGuestManagementService.getInstance().isLeaseSigned(getPmcYardiCredential(lease), lease);
    }

    @Override
    public String getLeaseId(Lease leaseId) throws YardiServiceException {
        final Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, false);
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.ToStringMembers);
        validateApplicationAcceptance(lease.unit().building());

        try {
            return YardiGuestManagementService.getInstance().getLeaseId(getPmcYardiCredential(lease), lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateApplicationAcceptance(Building buildingId) throws UserRuntimeException {
        Building building = Persistence.service().retrieve(Building.class, buildingId.getPrimaryKey());
        PmcYardiCredential yc = getPmcYardiCredential(building);
        try {
            YardiGuestManagementService.getInstance().validateSettings(yc, building.propertyCode().getValue());
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(e.getMessage(), e);
        } catch (RemoteException e) {
            throw new UserRuntimeException("Yardi communication error: " + e.getMessage(), e);
        }
    }
}
