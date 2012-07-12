/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.customer.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;

import com.propertyvista.crm.client.ui.crud.customer.lead.showing.ShowingListerView;
import com.propertyvista.crm.rpc.dto.tenant.ShowingDTO;
import com.propertyvista.crm.rpc.services.customer.lead.ShowingCrudService;
import com.propertyvista.domain.tenant.lead.Appointment;

public class ShowingListerActivity extends ListerActivityBase<ShowingDTO> {

    @SuppressWarnings("unchecked")
    public ShowingListerActivity(Place place, ShowingListerView view) {
        super(place, view, (AbstractCrudService<ShowingDTO>) GWT.create(ShowingCrudService.class), ShowingDTO.class);
    }

    @Override
    public void populate() {
        super.populate();

        ((ShowingCrudService) getService()).getActiveState(new DefaultAsyncCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                ((ShowingListerView) getView()).setAddNewVisible(result);
            }
        }, EntityFactory.createIdentityStub(Appointment.class, getParent()));
    }
}
