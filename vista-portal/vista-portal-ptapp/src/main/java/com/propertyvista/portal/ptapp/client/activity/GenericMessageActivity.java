/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.common.client.Message;
import com.propertyvista.portal.ptapp.client.PtAppSite;
import com.propertyvista.portal.ptapp.client.ui.GenericMessageView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;

/**
 * 
 * Shows dialog style message on full screen
 * 
 */
public class GenericMessageActivity extends AbstractActivity implements GenericMessageView.Presenter {

    enum Params {
        TITLE, MESSAGE, BUTTON_LABEL
    }

    private final GenericMessageView view;

    private Message message;

    public GenericMessageActivity(Place place) {
        view = (GenericMessageView) PtAppViewFactory.instance(GenericMessageView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public GenericMessageActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        message = PtAppSite.instance().getMessage();
        view.setMessage(message);
    }

    @Override
    public void action() {
        if (message != null && message.getCommand() != null) {
            message.getCommand().execute();
        }
    }
}
