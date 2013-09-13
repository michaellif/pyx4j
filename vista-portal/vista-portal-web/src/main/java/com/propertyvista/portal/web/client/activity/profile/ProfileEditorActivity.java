/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PersonalInfoCrudService;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.profile.ProfileEditorView;
import com.propertyvista.portal.web.client.ui.profile.ProfileEditorView.ProfileEditorPresenter;

public class ProfileEditorActivity extends AbstractEditorActivity<ResidentDTO> implements ProfileEditorPresenter {

    public ProfileEditorActivity(Place place) {
        super(ProfileEditorView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(getView());

        PersonalInfoCrudService srv = GWT.create(PersonalInfoCrudService.class);
        srv.retrieve(new DefaultAsyncCallback<ResidentDTO>() {
            @Override
            public void onSuccess(ResidentDTO result) {
                getView().populate(result);
            }
        }, null, AbstractCrudService.RetrieveTarget.View);
    }

}
