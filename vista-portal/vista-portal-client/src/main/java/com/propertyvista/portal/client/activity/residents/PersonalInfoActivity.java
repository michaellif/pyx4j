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
package com.propertyvista.portal.client.activity.residents;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.ResidentDTO;
import com.propertyvista.portal.rpc.portal.services.resident.PersonalInfoCrudService;

public class PersonalInfoActivity extends SecurityAwareActivity implements PersonalInfoView.Presenter {

    private final PersonalInfoView view;

    PersonalInfoCrudService srv;

    public PersonalInfoActivity(Place place) {
        this.view = PortalViewFactory.instance(PersonalInfoView.class);
        this.view.setPresenter(this);
        withPlace(place);
        srv = GWT.create(PersonalInfoCrudService.class);
    }

    public PersonalInfoActivity withPlace(Place place) {
        return this;
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

        }, null, AbstractCrudService.RetrieveTraget.View);

    }

    @Override
    public void save(ResidentDTO info) {
        srv.save(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                srv.retrieve(new DefaultAsyncCallback<ResidentDTO>() {
                    @Override
                    public void onSuccess(ResidentDTO result) {
                        view.populate(result);
                        view.showNote("Operation completed successfully.");
                    }

                }, null, AbstractCrudService.RetrieveTraget.View);
            }

        }, info);
    }

    @Override
    public void cancel() {
        History.back();
    }
}
