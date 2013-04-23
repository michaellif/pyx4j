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
package com.propertyvista.crm.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class NotificationsViewImpl extends FlowPanel implements NotificationsView {

    public NotificationsViewImpl() {

    }

    @Override
    public void showNotifications(List<String> notifications) {
        for (String string : notifications) {
            HTML label = new HTML(string);
            label.setSize("100%", CrmRootPane.NOTIFICATION_HEIGHT + "px");
            add(label);
        }
    }
}
