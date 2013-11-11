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
package com.propertyvista.operations.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.NavigFolder.Type;
import com.propertyvista.operations.client.event.CrudNavigateEvent;
import com.propertyvista.operations.client.event.CrudNavigateHandler;
import com.propertyvista.operations.client.ui.ShortCutsView;
import com.propertyvista.operations.client.ui.ShortCutsView.ShortCutsPresenter;

public class ShortCutsActivity extends AbstractActivity implements ShortCutsPresenter, CrudNavigateHandler {

    private static final I18n i18n = I18n.get(ShortCutsActivity.class);

    private final ShortCutsView view;

    public ShortCutsActivity() {
        view = OperationsSite.getViewFactory().getView(ShortCutsView.class);
        view.setPresenter(this);
    }

    public ShortCutsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setNavigationFolders(createFolders());

        panel.setWidget(view);
        eventBus.addHandler(CrudNavigateEvent.getType(), this);
    }

    @Override
    public void onCrudNavigate(CrudNavigateEvent event) {
        view.updateShortcutFolder(event.getPlace(), event.getValue());
    }

    private List<NavigFolder> createFolders() {
        List<NavigFolder> navigfolders = new ArrayList<NavigFolder>();

        navigfolders.add(new NavigFolder(Type.Shortcuts, i18n.tr("Shortcuts")));

        return navigfolders;
    }
}
