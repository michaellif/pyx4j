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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.crm.server.services.Commons;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.dto.PaymentRecordDTO.PaymentSelect;

public class PaymentCrudServiceImpl extends AbstractCrudServiceDtoImpl<PaymentRecord, PaymentRecordDTO> implements PaymentCrudService {

    public PaymentCrudServiceImpl() {
        super(PaymentRecord.class, PaymentRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceRetrieved(entity, dto);
        enhanceListRetrieved(entity, dto);

        dto.participants().addAll(retrieveUsers(entity.billingAccount().lease()));

        dto.paymentSelect().setValue(PaymentSelect.New);
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.service().retrieve(entity.billingAccount());
        Persistence.service().retrieve(entity.billingAccount().lease());
        Persistence.service().retrieve(entity.billingAccount().lease().unit());
        Persistence.service().retrieve(entity.billingAccount().lease().unit().belongsTo());

        dto.propertyCode().set(entity.billingAccount().lease().unit().belongsTo().propertyCode());
        dto.unitNumber().set(entity.billingAccount().lease().unit().info().number());
        dto.leaseId().set(entity.billingAccount().lease().leaseId());
        dto.leaseStatus().set(entity.billingAccount().lease().version().status());

        Persistence.service().retrieve(dto.paymentMethod());
        Persistence.service().retrieve(dto.paymentMethod().leaseParticipant());
        dto.leaseParticipant().set(dto.paymentMethod().leaseParticipant());
    }

    @Override
    protected void persist(PaymentRecord entity, PaymentRecordDTO dto) {
        entity.paymentMethod().leaseParticipant().set(dto.leaseParticipant());
        ServerSideFactory.create(PaymentFacade.class).persistPayment(entity);
    }

    @Override
    public void initNewEntity(AsyncCallback<PaymentRecordDTO> callback, Key parentId) {
        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, parentId);
        if ((billingAccount == null) || (billingAccount.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(BillingAccount.class).getCaption() + "' " + parentId + " NotFound");
        }

        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().unit());
        Persistence.service().retrieve(billingAccount.lease().unit().belongsTo());

        PaymentRecordDTO dto = EntityFactory.create(PaymentRecordDTO.class);
        dto.propertyCode().set(billingAccount.lease().unit().belongsTo().propertyCode());
        dto.unitNumber().set(billingAccount.lease().unit().info().number());
        dto.leaseId().set(billingAccount.lease().leaseId());
        dto.leaseStatus().set(billingAccount.lease().version().status());
        dto.participants().addAll(retrieveUsers(billingAccount.lease()));

        // some default values:
        dto.paymentStatus().setValue(PaymentStatus.Submitted);
        dto.paymentSelect().setValue(PaymentSelect.New);
        dto.receivedDate().setValue(new LogicalDate());

        dto.paymentMethod().type().setValue(PaymentType.Echeck);

        callback.onSuccess(dto);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseParticipant participant) {
        Commons.getLeaseParticipantCurrentAddress(callback, participant);
    }

    @Override
    public void getProfiledPaymentMethod(AsyncCallback<PaymentMethod> callback, LeaseParticipant payer) {
        Persistence.service().retrieve(payer);
        if ((payer == null) || (payer.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseParticipant.class).getCaption() + "' " + payer.getPrimaryKey()
                    + " NotFound");
        }

        PaymentMethod method = null;
        Persistence.service().retrieve(payer.paymentMethods());
        for (PaymentMethod pm : payer.paymentMethods()) {
            if (pm.isDefault().isBooleanTrue()) {
                method = pm;
            }
        }
        callback.onSuccess(method); // null - means there is no default one!..
    }

    // Payment operations:

    @Override
    public void processPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        Persistence.secureSave(ServerSideFactory.create(PaymentFacade.class).processPayment(EntityFactory.createIdentityStub(PaymentRecord.class, entityId)));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void clearPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        Persistence.secureSave(ServerSideFactory.create(PaymentFacade.class).clear(EntityFactory.createIdentityStub(PaymentRecord.class, entityId)));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void rejectPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        Persistence.secureSave(ServerSideFactory.create(PaymentFacade.class).reject(EntityFactory.createIdentityStub(PaymentRecord.class, entityId)));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    @Override
    public void cancelPayment(AsyncCallback<PaymentRecordDTO> callback, Key entityId) {
        Persistence.secureSave(ServerSideFactory.create(PaymentFacade.class).cancel(EntityFactory.createIdentityStub(PaymentRecord.class, entityId)));
        Persistence.service().commit();
        retrieve(callback, entityId, RetrieveTraget.View);
    }

    // internals:
    private List<LeaseParticipant> retrieveUsers(Lease lease) {
        List<LeaseParticipant> users = new LinkedList<LeaseParticipant>();

        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenant : lease.version().tenants()) {
            Persistence.service().retrieve(tenant);
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            }
        }

        Persistence.service().retrieve(lease.version().guarantors());
        for (Guarantor guarantor : lease.version().guarantors()) {
            users.add(guarantor);
        }

        return users;
    }
}
