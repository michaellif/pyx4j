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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.RuntimeErrorView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;

public class RuntimeErrorActivity extends AbstractActivity implements RuntimeErrorView.Presenter {

    private final RuntimeErrorView view;

    public RuntimeErrorActivity(Place place) {
        view = CrmVeiwFactory.instance(RuntimeErrorView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        view.setError(CrmSite.instance().getUserMessage());
        container.setWidget(view);
    }

    @Override
    public void backToOrigin() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }
}
