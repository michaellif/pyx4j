/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.security.rpc.AbstractPasswordResetService;

import com.propertyvista.common.client.ui.components.security.AbstractPasswordResetActivity;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.operations.client.viewfactories.SecurityViewFactory;
import com.propertyvista.operations.rpc.services.AdminPasswordResetService;

public class PasswordResetActivity extends AbstractPasswordResetActivity {

    public PasswordResetActivity(Place place) {
        super(place, SecurityViewFactory.instance(PasswordResetView.class), GWT.<AbstractPasswordResetService> create(AdminPasswordResetService.class));
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPasswordStrengthRule(new DefaultPasswordStrengthRule());
        super.start(panel, eventBus);
    }

}
