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

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingEditorView;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Appointment;

public class ShowingEditorActivity extends CrmEditorActivity<ShowingDTO> implements ShowingEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public ShowingEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(ShowingEditorView.class), (AbstractCrudService<ShowingDTO>) GWT.create(ShowingCrudService.class),
                ShowingDTO.class);
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((ShowingCrudService) getService()).updateValue(new DefaultAsyncCallback<AptUnit>() {
            @Override
            public void onSuccess(AptUnit result) {
                ((ShowingEditorView) getView()).setUnitData(result);
            }
        }, selected.getPrimaryKey());
    }

    @Override
    protected void createNewEntity(final AsyncCallback<ShowingDTO> callback) {
        super.createNewEntity(new DefaultAsyncCallback<ShowingDTO>() {
            @Override
            public void onSuccess(ShowingDTO result) {
                ((ShowingCrudService) getService()).createNew(callback, EntityFactory.createIdentityStub(Appointment.class, getParentId()));
            }
        });
    }
}
