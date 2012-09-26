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
package com.propertyvista.crm.client.activity.crud.customer.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.lead.LeadEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.customer.lead.LeadCrudService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.Status;

public class LeadEditorActivity extends CrmEditorActivity<Lead> implements LeadEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public LeadEditorActivity(CrudAppPlace place) {
        super(place, MarketingViewFactory.instance(LeadEditorView.class), (AbstractCrudService<Lead>) GWT.create(LeadCrudService.class), Lead.class);
    }

    @Override
    public void setSelectedFloorplan(Floorplan selected) {
        ((LeadCrudService) getService()).updateValue(new DefaultAsyncCallback<Floorplan>() {
            @Override
            public void onSuccess(Floorplan item) {
                ((LeadEditorView) getView()).setFloorplanData(item);
            }
        }, selected.getPrimaryKey());
    }

    @Override
    protected void createNewEntity(AsyncCallback<Lead> callback) {
        Lead entity = EntityFactory.create(getEntityClass());

        entity.status().setValue(Status.active);

        callback.onSuccess(entity);
    }
}
