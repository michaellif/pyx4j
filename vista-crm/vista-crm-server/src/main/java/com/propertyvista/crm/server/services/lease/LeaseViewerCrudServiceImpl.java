/*
 *
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import java.rmi.RemoteException;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.AndCriterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.services.lease.LeaseViewerCrudService;
import com.propertyvista.crm.server.services.lease.common.LeaseTermCrudServiceImpl;
import com.propertyvista.crm.server.services.lease.common.LeaseViewerCrudServiceBaseImpl;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.LeaseDTO;
import com.propertyvista.dto.LeaseTermDTO;

public class LeaseViewerCrudServiceImpl extends LeaseViewerCrudServiceBaseImpl<LeaseDTO> implements LeaseViewerCrudService {

    private final static I18n i18n = I18n.get(LeaseViewerCrudServiceImpl.class);

    public LeaseViewerCrudServiceImpl() {
        super(LeaseDTO.class);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Lease> dbCriteria, EntityListCriteria<LeaseDTO> dtoCriteria) {
        PropertyCriterion papCriteria = dtoCriteria.getCriterion(dtoCriteria.proto().papPresent());
        if (papCriteria != null) {
            dtoCriteria.getFilters().remove(papCriteria);

            AndCriterion notDeleted = new AndCriterion();
            notDeleted.eq(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments().$().isDeleted(), Boolean.FALSE);
            if (papCriteria.getValue() == Boolean.FALSE) {
                dbCriteria.notExists(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments(), notDeleted);
            } else {
                dbCriteria.add(notDeleted);
                dbCriteria.isNotNull(dbCriteria.proto().currentTerm().version().tenants().$().leaseParticipant().preauthorizedPayments());
            }
        }
        super.enhanceListCriteria(dbCriteria, dtoCriteria);
    }

    @Override
    protected void enhanceListRetrieved(Lease in, LeaseDTO dto) {
        super.enhanceListRetrieved(in, dto);

        {
            EntityQueryCriteria<PreauthorizedPayment> criteria = EntityQueryCriteria.create(PreauthorizedPayment.class);
            criteria.eq(criteria.proto().tenant().lease(), in);
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
            dto.papPresent().setValue(Persistence.service().count(criteria) != 0);
        }
    }

    @Override
    protected void enhanceRetrieved(Lease in, LeaseDTO dto, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, dto, retrieveTarget);

        if (!dto.billingAccount().isNull()) {
            dto.transactionHistory().set(ServerSideFactory.create(ARFacade.class).getTransactionHistory(dto.billingAccount()));
            dto.carryforwardBalance().setValue(dto.billingAccount().carryforwardBalance().getValue());
        }

        dto.isMoveOutWithinNextBillingCycle().setValue(ServerSideFactory.create(LeaseFacade.class).isMoveOutWithinNextBillingCycle(in));
    }

    @Override
    public void createCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, Lease.CompletionType completionType, LogicalDate eventDate,
            LogicalDate moveOutDate, LogicalDate leseEndDate) {
        ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(EntityFactory.createIdentityStub(Lease.class, entityId), completionType, eventDate,
                moveOutDate, leseEndDate);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void isCancelCompletionEventAvailable(AsyncCallback<CancelMoveOutConstraintsDTO> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId);
        CancelMoveOutConstraintsDTO result = ServerSideFactory.create(OccupancyFacade.class).getCancelMoveOutConstraints(lease.unit().getPrimaryKey());
        if (!result.leaseStub().isNull()) {
            Persistence.service().retrieve(result.leaseStub());
        }
        callback.onSuccess(result);
    }

    @Override
    public void cancelCompletionEvent(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(EntityFactory.createIdentityStub(Lease.class, entityId),
                CrmAppContext.getCurrentUserEmployee(), decisionReason);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void moveOut(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).moveOut(leaseId, new LogicalDate(SystemDateManager.getDate()));

        // complete actually, if it already finished:
        Lease lease = Persistence.secureRetrieve(Lease.class, entityId);
        if (!lease.leaseTo().isNull() && lease.leaseTo().getValue().before(new LogicalDate(SystemDateManager.getDate()))) {
            ServerSideFactory.create(LeaseFacade.class).complete(leaseId);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void sendMail(AsyncCallback<String> callback, Key entityId, Vector<LeaseTermParticipant<?>> users, EmailTemplateType emailType) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId);
        if ((lease == null) || (lease.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(dboClass).getCaption() + "' " + entityId + " NotFound");
        }
        if (!lease.status().getValue().isCurrent()) {
            throw new UserRuntimeException(i18n.tr("Can't send tenant email for inactive Lease"));
        }
        if (users.isEmpty()) {
            throw new UserRuntimeException(i18n.tr("No customer was selected to send email"));
        }

        // check that all lease participants have an associated user entity (email)
        for (LeaseTermParticipant<?> user : users) {
            if (user.leaseParticipant().customer().user().isNull()) {
                throw new UserRuntimeException(i18n.tr("''Send Email'' operation failed, email of lease participant {0} was not found", user.leaseParticipant()
                        .customer().person().name().getStringView()));
            }
        }

        if (emailType == EmailTemplateType.TenantInvitation) {
            // check that selected users can be used for this template
            for (LeaseTermParticipant<?> user : users) {
                if (user.isInstanceOf(LeaseTermGuarantor.class)) {
                    throw new UserRuntimeException(i18n.tr(
                            "''Send Mail'' operation failed: can''t send \"{0}\" for Guarantor. Please re-send e-mail for all valid recipients.",
                            EmailTemplateType.TenantInvitation));
                }
            }

            // send e-mails
            CommunicationFacade commFacade = ServerSideFactory.create(CommunicationFacade.class);
            for (LeaseTermParticipant<?> user : users) {
                if (user.isInstanceOf(LeaseTermTenant.class)) {
                    LeaseTermTenant tenant = user.duplicate(LeaseTermTenant.class);
                    commFacade.sendTenantInvitation(tenant);
                }
            }
        } else {
            new Error(SimpleMessageFormat.format("sending mails for {0} is not yet implemented", emailType));
        }

        Persistence.service().commit();
        String message = users.size() > 1 ? i18n.tr("Emails were sent successfully") : i18n.tr("Email was sent successfully");
        callback.onSuccess(message);
    }

    @Override
    public void activate(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).approve(leaseId, null, null);

        // activate actually, if it already runs:
        if (!Persistence.secureRetrieve(Lease.class, entityId).leaseFrom().getValue().after(new LogicalDate(SystemDateManager.getDate()))) {
            ServerSideFactory.create(LeaseFacade.class).activate(leaseId);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void closeLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).close(leaseId);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void cancelLease(AsyncCallback<VoidSerializable> callback, Key entityId, String decisionReason) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).cancelLease(leaseId, CrmAppContext.getCurrentUserEmployee(), decisionReason);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void createOffer(AsyncCallback<LeaseTermDTO> callback, Key entityId, Type type) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);
        LeaseTerm term = ServerSideFactory.create(LeaseFacade.class).createOffer(leaseId, type);

        LeaseTermDTO termDto = EntityFactory.create(LeaseTermDTO.class);
        termDto.setValue(term.getValue());
        new LeaseTermCrudServiceImpl().update(term, termDto);

        callback.onSuccess(termDto);
    }

    @Override
    public void updateFromYardi(AsyncCallback<VoidSerializable> callback, Key entityId) {
        Lease lease = Persistence.service().retrieve(dboClass, entityId);

        try {
            ServerSideFactory.create(YardiARFacade.class).updateLease(lease);
        } catch (RemoteException e) {
            throw new UserRuntimeException(i18n.tr("Yardi connection problem"), e);
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(i18n.tr("Error updating lease form Yardi"), e);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    @Override
    public void simpleLeaseRenew(AsyncCallback<VoidSerializable> callback, Key entityId, LogicalDate leaseEndDate) {
        Lease leaseId = EntityFactory.createIdentityStub(Lease.class, entityId);

        ServerSideFactory.create(LeaseFacade.class).simpleLeaseRenew(leaseId, leaseEndDate);

        Persistence.service().commit();
        callback.onSuccess(null);
    }

}