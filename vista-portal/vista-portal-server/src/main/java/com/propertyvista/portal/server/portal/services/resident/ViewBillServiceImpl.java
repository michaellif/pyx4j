/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.services.resident;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.portal.rpc.portal.services.resident.ViewBillService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class ViewBillServiceImpl extends AbstractCrudServiceDtoImpl<Bill, BillDTO> implements ViewBillService {

    public ViewBillServiceImpl() {
        super(Bill.class, BillDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    public void retrieve(AsyncCallback<BillDTO> callback, Key entityId, RetrieveTraget retrieveTraget) {
        if (entityId == null) {
            // find current bill key instead:
            Tenant tenant = TenantAppContext.getCurrentUserTenantInLease();
            Persistence.service().retrieve(tenant.leaseV());
            Persistence.service().retrieve(tenant.leaseV().holder());
            entityId = ServerSideFactory.create(BillingFacade.class).getLatestBill(tenant.leaseV().holder()).getPrimaryKey();
        }
        super.retrieve(callback, entityId, retrieveTraget);
    }

    @Override
    protected void enhanceRetrieved(Bill entity, BillDTO dto) {
        // load detached entities:
        Persistence.service().retrieve(dto.lineItems());
        Persistence.service().retrieve(dto.billingAccount());
        Persistence.service().retrieve(dto.billingAccount().lease());
        Persistence.service().retrieve(dto.billingCycle().building(), AttachLevel.ToStringMembers);
        BillingUtils.enhanceBillDto(entity, dto);
    }

    @Override
    protected void persist(Bill entity, BillDTO dto) {
        throw new IllegalArgumentException();
    }
}
