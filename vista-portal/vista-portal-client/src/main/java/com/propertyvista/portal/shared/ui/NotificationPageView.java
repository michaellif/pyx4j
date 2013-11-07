/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.pyx4j.site.client.IsView;
import com.pyx4j.site.shared.domain.Notification;

public interface NotificationPageView extends IsView {

    interface NotificationPagePresenter {

        void acceptMessage();

    }

    void setPresenter(NotificationPagePresenter presenter);

    void populate(Notification userMessage);

}
