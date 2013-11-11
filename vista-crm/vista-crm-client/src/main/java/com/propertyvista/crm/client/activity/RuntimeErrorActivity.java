/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.NotificationAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.RuntimeErrorView;

public class RuntimeErrorActivity extends AbstractActivity implements RuntimeErrorView.Presenter {

    private final RuntimeErrorView view;

    private final NotificationAppPlace place;

    public RuntimeErrorActivity(NotificationAppPlace place) {
        this.place = place;
        view = CrmSite.getViewFactory().getView(RuntimeErrorView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        view.setError(place.getNotification());
        container.setWidget(view);
    }

    @Override
    public void backToOrigin() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }
}
