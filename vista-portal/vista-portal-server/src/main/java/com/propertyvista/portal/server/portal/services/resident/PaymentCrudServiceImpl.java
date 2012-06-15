/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-01
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import java.math.BigDecimal;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.biz.financial.payment.PaymentFacade;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.PaymentRecord.PaymentStatus;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PaymentRecordDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PaymentCrudService;
import com.propertyvista.portal.server.portal.TenantAppContext;
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
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit());
        Persistence.service().retrieve(dto.billingAccount().lease().unit().belongsTo());

        dto.leaseId().set(dto.billingAccount().lease().leaseId());
        dto.leaseStatus().set(dto.billingAccount().lease().version().status());
        dto.propertyCode().set(dto.billingAccount().lease().unit().belongsTo().propertyCode());
        dto.unitNumber().set(dto.billingAccount().lease().unit().info().number());

        Persistence.service().retrieve(dto.paymentMethod());
        Persistence.service().retrieve(dto.paymentMethod().customer());
        Persistence.service().retrieve(dto.leaseParticipant());
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
    public void initNew(AsyncCallback<PaymentRecordDTO> callback) {
        Tenant tenant = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenant.leaseV());
        Persistence.service().retrieve(tenant.leaseV().holder());

        Lease lease = tenant.leaseV().holder();
        Persistence.service().retrieve(lease.unit());
        Persistence.service().retrieve(lease.unit().belongsTo());

        PaymentRecordDTO dto = EntityFactory.create(PaymentRecordDTO.class);

        dto.leaseParticipant().set(tenant);
        dto.billingAccount().set(lease.billingAccount());
        dto.leaseId().set(lease.leaseId());
        dto.leaseStatus().set(lease.version().status());
        dto.propertyCode().set(lease.unit().belongsTo().propertyCode());
        dto.unitNumber().set(lease.unit().info().number());

        // some default values:
        dto.paymentStatus().setValue(PaymentStatus.Submitted);
        dto.createdDate().setValue(new LogicalDate());

        // calculate current balance:
        dto.amount().setValue(ServerSideFactory.create(ARFacade.class).getCurrentBalance(lease.billingAccount()));
        if (dto.amount().isNull() || dto.amount().getValue().signum() == -1) {
            dto.amount().setValue(new BigDecimal("0.00"));
        }

        callback.onSuccess(dto);
    }

    @Override
    public void getCurrentAddress(AsyncCallback<AddressStructured> callback) {
        AddressRetriever.getLeaseParticipantCurrentAddress(callback, TenantAppContext.getCurrentUserTenantInLease());
    }

    @Override
    public void getDefaultPaymentMethod(AsyncCallback<PaymentMethod> callback) {
        PaymentMethod method = null;
        for (PaymentMethod pm : ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(TenantAppContext.getCurrentUserTenantInLease())) {
            if (pm.isDefault().isBooleanTrue()) {
                method = pm;
            }
        }
        callback.onSuccess(method); // null - means there is no default one!..
    }

    @Override
    public void getProfiledPaymentMethods(AsyncCallback<Vector<PaymentMethod>> callback) {
        callback.onSuccess(new Vector<PaymentMethod>(ServerSideFactory.create(PaymentFacade.class).retrievePaymentMethods(
                TenantAppContext.getCurrentUserTenantInLease())));
    }
}
