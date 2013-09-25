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
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.dto.BillDTO;
import com.propertyvista.portal.rpc.portal.services.resident.ViewBillService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class ViewBillServiceImpl extends AbstractCrudServiceDtoImpl<Bill, BillDTO> implements ViewBillService {

    public ViewBillServiceImpl() {
        super(Bill.class, BillDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void retrieve(AsyncCallback<BillDTO> callback, Key entityId, RetrieveTarget retrieveTarget) {
        if (entityId == null) {
            // find current bill key instead:
            LeaseTermTenant tenant = TenantAppContext.getCurrentUserTenantInLease();
            Persistence.service().retrieve(tenant.leaseTermV());
            Persistence.service().retrieve(tenant.leaseTermV().holder().lease());

            entityId = ServerSideFactory.create(BillingFacade.class).getLatestConfirmedBill(tenant.leaseTermV().holder().lease()).getPrimaryKey();
        }
        super.retrieve(callback, entityId, retrieveTarget);
    }

    @Override
    protected void enhanceRetrieved(Bill bo, BillDTO to, RetrieveTarget retrieveTarget) {
        // load detached entities:
        Persistence.service().retrieve(to.lineItems());
        Persistence.service().retrieve(to.billingAccount());
        Persistence.service().retrieve(to.billingAccount().lease());
        Persistence.service().retrieve(to.billingCycle().building(), AttachLevel.ToStringMembers);
        BillingUtils.enhanceBillDto(bo, to);
    }

    @Override
    protected void persist(Bill bo, BillDTO to) {
        throw new IllegalArgumentException();
    }
}
