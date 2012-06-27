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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.server.common.util.AddressRetriever;

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

        dto.participants().addAll(retrieveUsers(dto.billingAccount().lease()));
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit());
        Persistence.service().retrieve(dto.billingAccount().lease().unit().building());
        Persistence.service().retrieve(dto.leaseParticipant());
        Persistence.service().retrieve(dto.paymentMethod());

        dto.leaseId().set(dto.billingAccount().lease().leaseId());
        dto.leaseStatus().set(dto.billingAccount().lease().version().status());
        dto.propertyCode().set(dto.billingAccount().lease().unit().building().propertyCode());
        dto.unitNumber().set(dto.billingAccount().lease().unit().info().number());
    }

    @Override
    protected void persist(PaymentRecord entity, PaymentRecordDTO dto) {
        if (dto.addThisPaymentMethodToProfile().isBooleanTrue() && PaymentType.avalableInProfile().contains(dto.paymentMethod().type().getValue())) {
            entity.paymentMethod().customer().set(dto.leaseParticipant().customer());

            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._Units().$()._Leases().$().billingAccount(), entity.billingAccount()));
            ServerSideFactory.create(PaymentFacade.class).persistPaymentMethod(Persistence.service().retrieve(criteria), entity.paymentMethod());
        }

        if (dto.paymentMethod().type().getValue() == PaymentType.Echeck) {
            entity.paymentMethod().isOneTimePayment().setValue(Boolean.FALSE);
        }
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
        Persistence.service().retrieve(billingAccount.lease().unit().building());

        PaymentRecordDTO dto = EntityFactory.create(PaymentRecordDTO.class);

        dto.billingAccount().set(billingAccount);
        dto.leaseId().set(billingAccount.lease().leaseId());
        dto.leaseStatus().set(billingAccount.lease().version().status());
        dto.propertyCode().set(billingAccount.lease().unit().building().propertyCode());
        dto.unitNumber().set(billingAccount.lease().unit().info().number());
        dto.participants().addAll(retrieveUsers(billingAccount.lease()));

        // some default values:
        dto.paymentStatus().setValue(PaymentStatus.Submitted);
        dto.createdDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(billingAccount));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        callback.onSuccess(dto);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback, LeaseParticipant participant) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, participant);
    }

    @Override
    public void getDefaultPaymentMethod(AsyncCallback<PaymentMethod> callback, LeaseParticipant payer) {
        Persistence.service().retrieve(payer);
        if ((payer == null) || (payer.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseParticipant.class).getCaption() + "' " + payer.getPrimaryKey()
                    + " NotFound");
        }

        PaymentMethod method = null;
        if (payer.isInstanceOf(Tenant.class)) {
            method = ((Tenant) payer).preauthorizedPayment();
        }
        callback.onSuccess(method); // null - means there is no default one!..
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<PaymentMethod>> callback, LeaseParticipant payer) {
        Persistence.service().retrieve(payer);
        if ((payer == null) || (payer.isNull())) {
            throw new RuntimeException("Entity '" + EntityFactory.getEntityMeta(LeaseParticipant.class).getCaption() + "' " + payer.getPrimaryKey()
                    + " NotFound");
        }

        callback.onSuccess(new Vector<PaymentMethod>(ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(payer)));
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
    private List<LeaseParticipant> retrieveUsers(Lease lease) {
        List<LeaseParticipant> users = new LinkedList<LeaseParticipant>();

        Persistence.service().retrieve(lease.version().tenants());
        for (Tenant tenant : lease.version().tenants()) {
            switch (tenant.role().getValue()) {
            case Applicant:
            case CoApplicant:
                users.add(tenant);
            }
        }

        return users;
    }
}
