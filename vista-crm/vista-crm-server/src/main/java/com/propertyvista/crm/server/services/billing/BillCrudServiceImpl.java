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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillStatus;

public class BillCrudServiceImpl extends AbstractCrudServiceDtoImpl<Bill, BillDataDTO> implements BillCrudService {

    public BillCrudServiceImpl() {
        super(Bill.class, BillDataDTO.class);
    }

    @Override
    protected void bind() {
        bind(dboClass, dtoProto.bill(), dboProto);
    }

    @Override
    protected void enhanceRetrieved(Bill entity, BillDataDTO dto, RetrieveTarget retrieveTarget) {
        // load detached entities:
        Persistence.service().retrieve(dto.bill().lineItems());
        Persistence.service().retrieve(dto.bill().billingAccount());
        Persistence.service().retrieve(dto.bill().billingAccount().lease());
        Persistence.service().retrieve(dto.bill().billingCycle().building(), AttachLevel.ToStringMembers);
        BillingUtils.enhanceBillDto(entity, dto.bill());
    }

    @Override
    protected void persist(Bill entity, BillDataDTO dto) {
        throw new IllegalArgumentException();
    }

    @Override
    public void confirm(AsyncCallback<BillDataDTO> callback, Key entityId) {
        ServerSideFactory.create(BillingFacade.class).confirmBill(EntityFactory.createIdentityStub(Bill.class, entityId));
        Persistence.service().commit();
        super.retrieve(callback, entityId, RetrieveTarget.View);
    }

    @Override
    public void confirm(AsyncCallback<VoidSerializable> callback, Vector<BillDataDTO> bills) {
        for (BillDataDTO billData : bills) {
            if (billData.bill().billStatus().getValue() == BillStatus.Finished) {
                ServerSideFactory.create(BillingFacade.class).confirmBill(EntityFactory.createIdentityStub(Bill.class, billData.bill().getPrimaryKey()));
            }
        }
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void reject(AsyncCallback<BillDataDTO> callback, Key entityId, String reason) {
        ServerSideFactory.create(BillingFacade.class).rejectBill(EntityFactory.createIdentityStub(Bill.class, entityId), reason);
        Persistence.service().commit();
        super.retrieve(callback, entityId, RetrieveTarget.View);
    }
}
