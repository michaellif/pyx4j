/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 19, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.field.client.activity.menu;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRerquestEvent.ChangeType;

import com.propertyvista.field.client.ui.components.menu.MenuView;
import com.propertyvista.field.client.ui.viewfactories.FieldViewFactory;

public class MenuActivity extends AbstractActivity {

    private final MenuView view;

    public MenuActivity(Place place) {
        view = FieldViewFactory.instance(MenuView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRerquestEvent(ChangeType.resizeComponents));
    }

}
