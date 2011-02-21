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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent;
import com.propertyvista.portal.client.ptapp.events.UserMessageEvent.UserMessageType;
import com.propertyvista.portal.client.ptapp.events.UserMessageHandler;
import com.propertyvista.portal.client.ptapp.ui.UserMessageView;

import com.pyx4j.site.rpc.AppPlace;

public class UserMessageActivity extends AbstractActivity implements UserMessageView.Presenter, UserMessageHandler {

    private final UserMessageView view;

    @Inject
    public UserMessageActivity(UserMessageView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public UserMessageActivity withPlace(AppPlace place) {
        for (UserMessageType type : UserMessageType.values()) {
            view.hide(type);
        }
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(view);
        eventBus.addHandler(UserMessageEvent.getType(), this);
    }

    @Override
    public void onUserMessage(UserMessageEvent event) {
        if (event.getMessageType() != UserMessageEvent.UserMessageType.DEBUG) {
            for (UserMessageType type : UserMessageType.values()) {
                view.hide(type);
            }
        }
        view.show(event.getMessage(), event.getMessageType());
    }

}
