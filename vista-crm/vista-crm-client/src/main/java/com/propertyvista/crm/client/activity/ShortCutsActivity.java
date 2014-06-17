/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.event.CrudNavigateEvent;
import com.propertyvista.crm.client.event.CrudNavigateHandler;
import com.propertyvista.crm.client.ui.ShortCutsView;
import com.propertyvista.crm.client.ui.ShortCutsView.ShortCutsPresenter;

public class ShortCutsActivity extends AbstractActivity implements ShortCutsPresenter, CrudNavigateHandler {

    private final ShortCutsView view;

    public ShortCutsActivity() {
        view = CrmSite.getViewFactory().getView(ShortCutsView.class);
        view.setPresenter(this);
    }

    public ShortCutsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        eventBus.addHandler(CrudNavigateEvent.getType(), this);
    }

    @Override
    public void onCrudNavigate(CrudNavigateEvent event) {
        view.updateShortcutFolder(event.getPlace(), event.getValue());
    }

}
