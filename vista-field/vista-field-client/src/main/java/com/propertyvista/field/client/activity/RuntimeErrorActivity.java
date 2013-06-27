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
package com.propertyvista.field.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.field.client.FieldSite;
import com.propertyvista.field.client.ui.RuntimeErrorView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;

public class RuntimeErrorActivity extends AbstractActivity implements RuntimeErrorView.Presenter {

    private final RuntimeErrorView view;

    public RuntimeErrorActivity(Place place) {
        view = FieldViewFactory.instance(RuntimeErrorView.class);
        assert (view != null);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        view.setError(FieldSite.instance().getNotification());
        container.setWidget(view);
    }

    @Override
    public void backToOrigin() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }
}
