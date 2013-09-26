/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.residents.usermessage.UserMessageView;

public class UserMessageActivity extends AbstractActivity implements UserMessageView.Presenter {

    private final UserMessageView view;

    public UserMessageActivity(Place place) {
        view = PortalSite.getViewFactory().instantiate(UserMessageView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.populate(PortalSite.instance().getNotification());
        panel.setWidget(view);

    }

    @Override
    public void acceptMessage() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }

}
