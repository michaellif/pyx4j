/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.billing;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.server.common.util.AddressRetriever;

public class PaymentCrudServiceImpl extends AbstractCrudServiceDtoImpl<PaymentRecord, PaymentRecordDTO> implements PaymentCrudService {

    private static final I18n i18n = I18n.get(PaymentCrudServiceImpl.class);

    public PaymentCrudServiceImpl() {
        super(PaymentRecord.class, PaymentRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(PaymentRecord entity, PaymentRecordDTO dto, RetrieveTraget retrieveTraget) {
        super.enhanceRetrieved(entity, dto, retrieveTraget);
        enhanceListRetrieved(entity, dto);

        dto.participants().addAll(retrievePayableUsers(dto.billingAccount().lease()));
        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsAllowed(dto.billingAccount()));
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit());
        Persistence.service().retrieve(dto.billingAccount().lease().unit().building());
        Persistence.service().retrieve(dto.leaseTermParticipant());
        Persistence.service().retrieve(dto.paymentMethod());

        dto.leaseId().set(dto.billingAccount().lease().leaseId());
        dto.leaseStatus().set(dto.billingAccount().lease().status());
        dto.propertyCode().set(dto.billingAccount().lease().unit().building().propertyCode());
        dto.unitNumber().set(dto.billingAccount().lease().unit().info().number());
        dto.addThisPaymentMethodToProfile().setValue(!entity.paymentMethod().isOneTimePayment().getValue());
    }

    @Override
    protected void persist(PaymentRecord entity, PaymentRecordDTO dto) {
        entity.paymentMethod().customer().set(dto.leaseTermParticipant().leaseParticipant().customer());

        // Do not change profile methods
        if (entity.paymentMethod().id().isNull()) {
            if (dto.addThisPaymentMethodToProfile().isBooleanTrue() && PaymentType.avalableInProfile().contains(dto.paymentMethod().type().getValue())) {
                entity.paymentMethod().isOneTimePayment().setValue(Boolean.FALSE);
            } else {
                entity.paymentMethod().isOneTimePayment().setValue(Boolean.TRUE);
            }

            // some corrections for particular method types: 
            if (dto.paymentMethod().type().getValue() == PaymentType.Echeck) {
                entity.paymentMethod().isOneTimePayment().setValue(Boolean.FALSE);
            }
        }

        ServerSideFactory.create(PaymentFacade.class).persistPayment(entity);
    }

    @Override
    public void initNewEntity(AsyncCallback<PaymentRecordDTO> callback, Key parentId) {
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, parentId);
        if ((billingAccount == null) || (billingAccount.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(BillingAccount.class).getCaption() + "' " + parentId + " NotFound");
        }

        if (!ServerSideFactory.create(PaymentFacade.class).isPaymentsAllowed(billingAccount)) {
            throw new UserRuntimeException(i18n.tr("No merchantAccount assigned to building to create the payment"));
        }

        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().unit());
        Persistence.service().retrieve(billingAccount.lease().unit().building());

        PaymentRecordDTO dto = EntityFactory.create(PaymentRecordDTO.class);

        dto.billingAccount().set(billingAccount);
        dto.leaseId().set(billingAccount.lease().leaseId());
        dto.leaseStatus().set(billingAccount.lease().status());
        dto.propertyCode().set(billingAccount.lease().unit().building().propertyCode());
        dto.unitNumber().set(billingAccount.lease().unit().info().number());
        dto.participants().addAll(retrievePayableUsers(billingAccount.lease()));

        // some default values:
        dto.paymentStatus().setValue(PaymentStatus.Submitted);
        dto.createdDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        dto.electronicPaymentsAllowed().setValue(ServerSideFactory.create(PaymentFacade.class).isElectronicPaymentsAllowed(billingAccount));

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(billingAccount));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        callback.onSuccess(dto);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseTermParticipant participant) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, participant);
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<LeasePaymentMethod>> callback, LeaseTermParticipant payer) {
        Persistence.service().retrieve(payer);
        if ((payer == null) || (payer.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseTermParticipant.class).getCaption() + "' " + payer.getPrimaryKey()
                    + " NotFound");
        }
        callback.onSuccess(new Vector<LeasePaymentMethod>(ServerSideFactory.create(PaymentMethodFacade.class).retrieveLeasePaymentMethods(payer)));
    }

    // Payment operations:

    @Override
    public void schedulePayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).schedulePayment(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void processPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).processPayment(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void clearPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).clear(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void rejectPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).reject(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void cancelPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        ServerSideFactory.create(PaymentFacade.class).cancel(EntityFactory.createIdentityStub(PaymentRecord.class, entityId));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    // internals:
    @SuppressWarnings("incomplete-switch")
    private List<LeaseTermParticipant<?>> retrievePayableUsers(Lease lease) {
        List<LeaseTermParticipant<?>> users = new LinkedList<LeaseTermParticipant<?>>();

        // add payable tenants:
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            }
        }

        // add guarantors:
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        users.addAll(lease.currentTerm().version().guarantors());

        return users;
    }
}
