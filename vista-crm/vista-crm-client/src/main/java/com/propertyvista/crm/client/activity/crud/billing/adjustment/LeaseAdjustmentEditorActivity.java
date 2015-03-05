/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.activity.crud.billing.adjustment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentEditorView;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;

public class LeaseAdjustmentEditorActivity extends CrmEditorActivity<LeaseAdjustment> implements LeaseAdjustmentEditorView.Presenter {

    public LeaseAdjustmentEditorActivity(CrudAppPlace place) {
        super(LeaseAdjustment.class, place, CrmSite.getViewFactory().getView(LeaseAdjustmentEditorView.class), GWT
                .<AbstractCrudService<LeaseAdjustment>> create(LeaseAdjustmentCrudService.class));
    }

    @Override
    public void calculateTax(AsyncCallback<LeaseAdjustment> callback, LeaseAdjustment currentValue) {
        ((LeaseAdjustmentCrudService) getService()).calculateTax(callback, currentValue);
    }

}
