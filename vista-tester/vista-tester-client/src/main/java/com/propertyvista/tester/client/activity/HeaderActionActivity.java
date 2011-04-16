/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.tester.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.tester.client.ui.HeaderActionView;

public class HeaderActionActivity extends AbstractActivity {

    private final HeaderActionView view;

    @Inject
    public HeaderActionActivity(HeaderActionView headerview) {
        view = headerview;
    }

    public HeaderActionActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        /**
         * TODO
         * do something with the view according to its declared interface
         */
        container.setWidget(view);

    }

}
