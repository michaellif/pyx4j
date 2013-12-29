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
package com.propertyvista.crm.client.activity.crud.billing.bill;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.bill.BillViewerView;
import com.propertyvista.crm.rpc.dto.billing.BillDataDTO;
import com.propertyvista.crm.rpc.services.billing.BillCrudService;
import com.propertyvista.crm.rpc.services.billing.BillPrintService;
import com.propertyvista.domain.financial.billing.Bill;

public class BillViewerActivity extends CrmViewerActivity<BillDataDTO> implements BillViewerView.Presenter {

    public BillViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(BillViewerView.class), GWT.<BillCrudService> create(BillCrudService.class));
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
