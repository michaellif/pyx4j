/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 26, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.client.SecurityControllerEvent;
import com.pyx4j.security.client.SecurityControllerHandler;

public abstract class SecurityAwareActivity extends AbstractActivity {

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        eventBus.addHandler(SecurityControllerEvent.getType(), new SecurityControllerHandler() {
            @Override
            public void onSecurityContextChange(SecurityControllerEvent event) {
                if (!ClientContext.isAuthenticated()) {
                    panel.setWidget(null);
                }

            }
        });

    }

}
