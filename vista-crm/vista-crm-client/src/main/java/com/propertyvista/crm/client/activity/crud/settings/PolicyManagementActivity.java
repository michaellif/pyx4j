/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.client.ui.crud.settings.policymanagement.PolicyManagementView;
import com.propertyvista.crm.client.ui.crud.settings.policymanagement.PolicyManagementViewImpl;
import com.propertyvista.crm.rpc.services.policy.PolicyManagerService;
import com.propertyvista.domain.policy.AllowedIDs;
import com.propertyvista.domain.policy.Policy;

public class PolicyManagementActivity extends AbstractActivity implements PolicyManagementView.Presenter {
    protected final PolicyManagementView view;

    private final PolicyManagerService service;

    public PolicyManagementActivity(Place place) {
        // TODO make instantiation via factory
        view = new PolicyManagementViewImpl();
        service = GWT.create(PolicyManagerService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void edit() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void populate() {
        service.getUnitPolicy(new AsyncCallback<Policy>() {

            @Override
            public void onSuccess(Policy result) {
                view.populate(result);

            }

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }
        }, null, EntityFactory.getEntityPrototype(AllowedIDs.class));
    }

}
