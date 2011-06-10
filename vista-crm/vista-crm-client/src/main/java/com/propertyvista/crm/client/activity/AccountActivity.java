/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.propertyvista.crm.client.ui.AccountView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;

public class AccountActivity extends AbstractActivity {

    private final AccountView view;

    @Inject
    public AccountActivity(Place place) {
        this.view = (AccountView) CrmVeiwFactory.instance(AccountView.class);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        container.setWidget(view);

    }

    public AccountActivity withPlace(Place place) {
        return this;
    }

}
