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
package com.propertyvista.portal.client.ptapp.activity;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.events.UserMessageEvent;
import com.propertyvista.portal.client.events.UserMessageHandler;
import com.propertyvista.portal.client.ptapp.ui.UserMessageView;

import com.pyx4j.site.client.place.AppPlace;

public class UserMessageActivity extends AbstractActivity implements UserMessageView.Presenter, UserMessageHandler {

    private final UserMessageView view;

    @Inject
    public UserMessageActivity(UserMessageView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public UserMessageActivity withPlace(AppPlace place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(view);

        eventBus.addHandler(UserMessageEvent.getType(), this);

    }

    @Override
    public void onUserMessage(UserMessageEvent event) {
        view.showNotes(event.getMessages());
        view.showErrors(event.getMessages());
        view.showFailures(event.getMessages());
    }

}
