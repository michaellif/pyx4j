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
package com.propertyvista.portal.web.client.activity.residents.personalinfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.ProfileViewer;
import com.propertyvista.portal.rpc.portal.services.resident.PersonalInfoCrudService;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.personalinfo.PersonalInfoEdit;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;

public class PersonalInfoEditActivity extends SecurityAwareActivity implements PersonalInfoEdit.Presenter {

    private final PersonalInfoEdit view;

    PersonalInfoCrudService srv;

    public PersonalInfoEditActivity(Place place) {
        this.view = PortalWebViewFactory.instance(PersonalInfoEdit.class);
        this.view.setPresenter(this);
        srv = GWT.create(PersonalInfoCrudService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        srv.retrieve(new DefaultAsyncCallback<ResidentDTO>() {
            @Override
            public void onSuccess(ResidentDTO result) {
                view.populate(result);
            }

        }, null, AbstractCrudService.RetrieveTarget.Edit);
    }

    @Override
    public void save(ResidentDTO info) {
        srv.save(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                AppSite.getPlaceController().goTo(new ProfileViewer());
            }
        }, info);
    }

    @Override
    public void cancel() {
        History.back();
    }
}
