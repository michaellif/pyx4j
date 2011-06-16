/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.common.client.events.UserMessageEvent;
import com.propertyvista.common.client.events.UserMessageHandler;
import com.propertyvista.portal.ptapp.client.ui.UserMessageView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;

public class UserMessageActivity extends AbstractActivity implements UserMessageView.Presenter, UserMessageHandler {

    private final UserMessageView view;

    public UserMessageActivity(Place place) {
        view = (UserMessageView) PtAppViewFactory.instance(UserMessageView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public UserMessageActivity withPlace(Place place) {
        view.hideAll();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(view);
        eventBus.addHandler(UserMessageEvent.getType(), this);
    }

    @Override
    public void onUserMessage(UserMessageEvent event) {
        view.hideAll();
        if (event.getMessageType() != null) {
            view.show(event.getUserMessage(), event.getDebugMessage(), event.getMessageType());
        }
    }

}
