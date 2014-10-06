/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.FeedbackView;
import com.propertyvista.crm.client.ui.FeedbackView.FeedbackPresenter;

public class FeedbackActivity extends AbstractActivity implements FeedbackPresenter {

    private final FeedbackView view;

    public FeedbackActivity() {
        view = CrmSite.getViewFactory().getView(FeedbackView.class);
        view.setPresenter(this);
    }

    public FeedbackActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        eventBus.addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                //TODO: view.updateContextHelp();
            }
        });
    }

}
