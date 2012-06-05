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
package com.propertyvista.crm.client.activity.crud.billing;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.dto.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillPrintService;
import com.propertyvista.domain.financial.billing.Bill;

public class BillViewerActivity extends CrmViewerActivity<BillDataDTO> implements BillViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public BillViewerActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(BillViewerView.class), (AbstractCrudService<BillDataDTO>) GWT.create(BillCrudService.class));
    }

    @Override
    public void confirm() {
        ((BillCrudService) getService()).confirm(new DefaultAsyncCallback<BillDataDTO>() {
            @Override
            public void onSuccess(BillDataDTO result) {
                populateView(result);
            }
        }, getEntityId());
    }

    @Override
    public void reject(String reason) {
        ((BillCrudService) getService()).reject(new DefaultAsyncCallback<BillDataDTO>() {
            @Override
            public void onSuccess(BillDataDTO result) {
                populateView(result);
            }
        }, getEntityId(), reason);
    }

    @Override
    public void print() {
        EntityQueryCriteria<Bill> criteria = new EntityQueryCriteria<Bill>(Bill.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), getEntityId()));
        new ReportDialog("Bill", "Creating Bill...").start(GWT.<BillPrintService> create(BillPrintService.class), criteria);
    }
}
