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
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.billing.adjustment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.billing.adjustments.LeaseAdjustmentEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.LeaseViewFactory;
import com.propertyvista.crm.rpc.services.billing.LeaseAdjustmentCrudService;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment.Status;

public class LeaseAdjustmentEditorActivity extends EditorActivityBase<LeaseAdjustment> {

    public LeaseAdjustmentEditorActivity(CrudAppPlace place) {
        super(place, LeaseViewFactory.instance(LeaseAdjustmentEditorView.class), GWT
                .<AbstractCrudService<LeaseAdjustment>> create(LeaseAdjustmentCrudService.class), LeaseAdjustment.class);
    }

    @Override
    protected void createNewEntity(AsyncCallback<LeaseAdjustment> callback) {
        LeaseAdjustment entity = EntityFactory.create(LeaseAdjustment.class);
        entity.receivedDate().setValue(new LogicalDate());
        entity.targetDate().setValue(new LogicalDate());
        entity.status().setValue(Status.draft);
        callback.onSuccess(entity);
    }
}
