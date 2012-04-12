/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.tenant.lease.bill.BillViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillPrintService;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.dto.BillDTO;

public class BillViewerActivity extends CrmViewerActivity<BillDTO> implements BillViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public BillViewerActivity(Place place) {
        super(place, TenantViewFactory.instance(BillViewerView.class), (AbstractCrudService<BillDTO>) GWT.create(BillCrudService.class));
    }

    @Override
    public void confirm() {
        ((BillCrudService) service).confirm(new DefaultAsyncCallback<BillDTO>() {
            @Override
            public void onSuccess(BillDTO result) {
                view.populate(result);
            }
        }, entityId);
    }

    @Override
    public void reject(String reason) {
        ((BillCrudService) service).reject(new DefaultAsyncCallback<BillDTO>() {
            @Override
            public void onSuccess(BillDTO result) {
                view.populate(result);
            }
        }, entityId, reason);
    }

    @Override
    public void print() {
        EntityQueryCriteria<Bill> criteria = new EntityQueryCriteria<Bill>(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), entityId));
        new ReportDialog("Bill", "Creating Bill...").start(GWT.<BillPrintService> create(BillPrintService.class), criteria);
    }
}
