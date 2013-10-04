/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.site.client;

import com.google.gwt.user.client.Window;

import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.common.client.handlers.VistaUnrecoverableErrorHandler;

public class VistaPortalWicketFragmentUnrecoverableErrorHandler extends VistaUnrecoverableErrorHandler {

    public VistaPortalWicketFragmentUnrecoverableErrorHandler() {
        super();
    }

    @Override
    protected void showMessage(String userMessage, String title, String systemInfo, NotificationType messageType) {
        Window.alert(userMessage);
    }

}
