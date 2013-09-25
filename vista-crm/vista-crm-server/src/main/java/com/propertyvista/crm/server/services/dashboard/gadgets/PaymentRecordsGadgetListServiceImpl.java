/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.LinkedList;

import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsGadgetListService;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.financial.PaymentRecord;

public class PaymentRecordsGadgetListServiceImpl extends AbstractListServiceDtoImpl<PaymentRecord, PaymentRecordForReportDTO> implements
        PaymentRecordsGadgetListService {

    public PaymentRecordsGadgetListServiceImpl() {
        super(PaymentRecord.class, PaymentRecordForReportDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.merchantAccount(), boProto.merchantAccount().accountNumber());
        bind(toProto.building(), boProto.billingAccount().lease().unit().building().propertyCode());
        bind(toProto.lease(), boProto.billingAccount().lease().leaseId());
        bind(toProto.tenant(), boProto.paymentMethod().customer());
        bind(toProto.method(), boProto.paymentMethod().type());
        bind(toProto.status(), boProto.paymentStatus());
        bind(toProto.created(), boProto.createdDate());
        bind(toProto.received(), boProto.receivedDate());
        bind(toProto.finalized(), boProto.finalizeDate());
        bind(toProto.target(), boProto.targetDate());
        bind(toProto.amount(), boProto.amount());
        bind(toProto.lastStatusChangeDate(), boProto.lastStatusChangeDate());
        bind(toProto.buildingFilterAnchor(), boProto.billingAccount().lease().unit().building());
    }

    @Override
    protected void enhanceListRetrieved(PaymentRecord entity, PaymentRecordForReportDTO dto) {
        super.enhanceListRetrieved(entity, dto);
        Persistence.service().retrieve(entity.paymentMethod().customer());
        dto.tenant().set(entity.paymentMethod().customer());

        Persistence.service().retrieve(entity.billingAccount());
        Persistence.service().retrieve(entity.billingAccount().lease());
        Persistence.service().retrieve(entity.billingAccount().lease().unit());
        Persistence.service().retrieve(entity.billingAccount().lease().unit().building());

        dto.building().setValue(entity.billingAccount().lease().unit().building().propertyCode().getValue());
        dto.lease().setValue(entity.billingAccount().lease().leaseId().getValue());

        Persistence.service().retrieve(entity.merchantAccount());
        dto.merchantAccount().setValue(entity.merchantAccount().accountNumber().getValue());
    }

    @Override
    protected boolean retriveDetachedMember(IEntity dboMember) {
        LinkedList<IEntity> detachedParents = new LinkedList<IEntity>();
        IEntity member = dboMember.getOwner();

        while (member != null && member.isValueDetached()) {
            detachedParents.push(member);
            member = member.getOwner();
        }
        while (!detachedParents.isEmpty()) {
            Persistence.service().retrieve(detachedParents.pop());
        }
        return Persistence.service().retrieve(dboMember);
    }

}
