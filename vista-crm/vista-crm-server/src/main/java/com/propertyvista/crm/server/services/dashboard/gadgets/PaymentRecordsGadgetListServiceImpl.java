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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.PaymentRecordsGadgetListService;
import com.propertyvista.crm.server.services.dashboard.util.Util;
import com.propertyvista.domain.dashboard.gadgets.payments.PaymentRecordForReportDTO;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.property.asset.building.Building;

public class PaymentRecordsGadgetListServiceImpl extends AbstractListServiceDtoImpl<PaymentRecord, PaymentRecordForReportDTO> implements
        PaymentRecordsGadgetListService {

    public PaymentRecordsGadgetListServiceImpl() {
        super(PaymentRecord.class, PaymentRecordForReportDTO.class);
    }

    @Override
    protected void bind() {
        bind(dtoProto.merchantAccount(), dboProto.merchantAccount().accountNumber());
        bind(dtoProto.building(), dboProto.billingAccount().lease().unit().building().propertyCode());
        bind(dtoProto.lease(), dboProto.billingAccount().lease().leaseId());
        bind(dtoProto.tenant(), dboProto.paymentMethod().customer());
        bind(dtoProto.method(), dboProto.paymentMethod().type());
        bind(dtoProto.status(), dboProto.paymentStatus());
        bind(dtoProto.created(), dboProto.createdDate());
        bind(dtoProto.received(), dboProto.receivedDate());
        bind(dtoProto.finalized(), dboProto.finalizeDate());
        bind(dtoProto.target(), dboProto.targetDate());
        bind(dtoProto.amount(), dboProto.amount());
        bind(dtoProto.lastStatusChangeDate(), dboProto.lastStatusChangeDate());

        bind(dtoProto.buildingFilterAnchor(), dboProto.billingAccount().lease().unit().building());
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
    public void list(AsyncCallback<EntitySearchResult<PaymentRecordForReportDTO>> callback, EntityListCriteria<PaymentRecordForReportDTO> dtoCriteria) {
        Vector<Building> bulidings = Util.enforcePortfolio(new Vector<Building>());
        if (bulidings.isEmpty()) {
            //TODO Return nothing!  Fix security properly
            dtoCriteria.eq(dtoCriteria.proto().buildingFilterAnchor(), -1L);
        } else {
            dtoCriteria.add(PropertyCriterion.in(dtoCriteria.proto().buildingFilterAnchor(), bulidings));
        }
        super.list(callback, dtoCriteria);
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
