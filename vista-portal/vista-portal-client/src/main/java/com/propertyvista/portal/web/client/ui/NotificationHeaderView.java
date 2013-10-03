/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import java.util.List;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.shared.domain.Notification;

public interface NotificationHeaderView extends IsView {

    public interface NotificationHeaderPresenter {

        void acceptMessage(Notification notification);

    }

    void setPresenter(NotificationHeaderPresenter presenter);

    void populate(List<Notification> notifications);
}
