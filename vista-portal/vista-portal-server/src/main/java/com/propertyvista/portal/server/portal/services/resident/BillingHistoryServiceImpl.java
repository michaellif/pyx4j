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

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.domain.dto.BillDTO;
import com.propertyvista.portal.rpc.portal.services.resident.BillingHistoryService;
import com.propertyvista.portal.server.portal.TenantAppContext;

public class BillingHistoryServiceImpl implements BillingHistoryService {

    @Override
    public void listBills(AsyncCallback<Vector<BillDTO>> callback) {
        Vector<BillDTO> bills = new Vector<BillDTO>();
        EntityQueryCriteria<Bill> criteria = EntityQueryCriteria.create(Bill.class);

        Tenant tenant = TenantAppContext.getCurrentUserTenantInLease();
        Persistence.service().retrieve(tenant.leaseV());
        Persistence.service().retrieve(tenant.leaseV().holder());

        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), tenant.leaseV().holder().billingAccount()));
        for (Bill bill : Persistence.service().query(criteria)) {
            BillDTO dto = EntityFactory.create(BillDTO.class);
            dto.setPrimaryKey(bill.getPrimaryKey());
            dto.amount().setValue(bill.totalDueAmount().getValue());
            dto.dueDate().setValue(bill.dueDate().getValue());
            dto.fromDate().setValue(bill.billingRun().executionDate().getValue());
            dto.referenceNo().setValue("View");

            bills.add(dto);
        }
        callback.onSuccess(bills);
    }
}
