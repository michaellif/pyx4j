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
package com.propertyvista.portal.web.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.web.client.ui.FooterView;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;

public class FooterActivity extends AbstractActivity {

    private final FooterView view;

    public FooterActivity(Place place) {
        view = PortalWebViewFactory.instance(FooterView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

}
