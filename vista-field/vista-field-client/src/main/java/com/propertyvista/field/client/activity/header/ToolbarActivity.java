/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.field.client.activity.header;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.PageOrientation;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;

import com.propertyvista.common.client.events.ChangePageOrientationEvent;
import com.propertyvista.common.client.events.ChangePageOrientationHandler;
import com.propertyvista.field.client.event.ChangeHeaderEvent;
import com.propertyvista.field.client.event.ChangeHeaderHandler;
import com.propertyvista.field.client.event.HeaderAction;
import com.propertyvista.field.client.ui.components.header.ToolbarView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;

public class ToolbarActivity extends AbstractActivity implements ChangeHeaderHandler, ChangePageOrientationHandler {

    private final ToolbarView view;

    public ToolbarActivity(Place place) {
        view = FieldViewFactory.instance(ToolbarView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        eventBus.addHandler(ChangeHeaderEvent.getType(), this);
        eventBus.addHandler(ChangePageOrientationEvent.getType(), this);
    }

    @Override
    public void onChangeHeader(ChangeHeaderEvent event) {
        view.showNavigationDetails(event.getAction() == HeaderAction.ShowNavigDetails);
    }

    @Override
    public void onChangePageOrientation(ChangePageOrientationEvent event) {
        view.setPageOrientation(event.getPageOrientation());
        view.showNavigationDetails(isViewerPlace(AppSite.getWhere()) && event.getPageOrientation() == PageOrientation.Vertical);
    }

    private static boolean isViewerPlace(Place place) {
        return place instanceof CrudAppPlace && ((CrudAppPlace) place).getType() == Type.viewer;
    }

}
