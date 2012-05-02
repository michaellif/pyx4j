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
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.billing.PaymentCrudService;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
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

        dto.participants().addAll(retrieveUsers(dto.billingAccount().lease()));

        dto.paymentType().set(dto.paymentMethod().type());

        dto.paymentSelect().setValue(PaymentSelect.New);
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingAccount().lease().unit());
        Persistence.service().retrieve(dto.billingAccount().lease().unit().belongsTo());

        dto.propertyCode().set(dto.billingAccount().lease().unit().belongsTo().propertyCode());
        dto.unitNumber().set(dto.billingAccount().lease().unit().info().number());
        dto.leaseId().set(dto.billingAccount().lease().leaseId());
        dto.leaseStatus().set(dto.billingAccount().lease().version().status());

        Persistence.service().retrieve(dto.paymentMethod().leaseParticipant());
        dto.leaseParticipant().set(dto.paymentMethod().leaseParticipant());
    }

    @Override
    protected void persist(PaymentRecord entity, PaymentRecordDTO dto) {

        entity.paymentMethod().leaseParticipant().set(dto.leaseParticipant());
        entity.paymentMethod().type().set(dto.paymentType());

        Persistence.service().persist(entity.paymentMethod());
        super.persist(entity, dto);
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

        callback.onSuccess(dto);
    }

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
