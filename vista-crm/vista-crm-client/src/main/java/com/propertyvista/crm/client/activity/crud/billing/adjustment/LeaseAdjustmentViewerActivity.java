/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.adjustment;

import java.math.BigDecimal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentViewerView;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentViewerActivity extends CrmViewerActivity<LeaseAdjustment> implements LeaseAdjustmentViewerView.Presenter {

    public LeaseAdjustmentViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(LeaseAdjustmentViewerView.class),

        GWT.<AbstractCrudService<LeaseAdjustment>> create(LeaseAdjustmentCrudService.class));
    }

    @Override
    public void calculateTax(AsyncCallback<BigDecimal> callback, LeaseAdjustment currentValue) {
        ((LeaseAdjustmentCrudService) getService()).calculateTax(callback, currentValue);
    }

    @Override
    public void submitAdjustment() {
        ((LeaseAdjustmentCrudService) getService()).submitAdjustment(new DefaultAsyncCallback<LeaseAdjustment>() {
            @Override
            public void onSuccess(LeaseAdjustment result) {
                populateView(result);
            }
        }, getEntityId());
    }
}
