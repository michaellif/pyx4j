/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.communication.Message;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.CommunicationView;

public class CommunicationActivity extends AbstractActivity implements CommunicationView.CommunicationPresenter {

    private static List<Message> messages = new ArrayList<Message>();

    static {
        for (int i = 0; i < 6; i++) {

            Message message = EntityFactory.create(Message.class);
            message.subject().setValue("Message #" + i);
            message.text().setValue("This is Communication Message #" + i);
            message.date().setValue(new Date(System.currentTimeMillis() - i * 24 * 60 * 60 * 1000));

            messages.add(message);
        }

    }

    private final CommunicationView view;

    public CommunicationActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(CommunicationView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.populate(messages);
    }
}
