/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import com.propertyvista.portal.resident.mvp.CommunicationActivityMapper;
import com.propertyvista.portal.resident.mvp.ContentActivityMapper;
import com.propertyvista.portal.resident.mvp.ExtraActivityMapper;
import com.propertyvista.portal.resident.mvp.MenuActivityMapper;
import com.propertyvista.portal.resident.mvp.NotificationActivityMapper;
import com.propertyvista.portal.resident.mvp.ToolbarActivityMapper;
import com.propertyvista.portal.shared.mvp.FooterActivityMapper;
import com.propertyvista.portal.shared.mvp.HeaderActivityMapper;
import com.propertyvista.portal.shared.ui.PortalRootPane;

public class ResidentPortalRootPane extends PortalRootPane {

    public ResidentPortalRootPane() {
        super();

        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new ToolbarActivityMapper(), asWidget().getToolbarDisplay());
        bind(new MenuActivityMapper(), asWidget().getMenuDisplay());
        bind(new CommunicationActivityMapper(), asWidget().getCommDisplay());
        bind(new ContentActivityMapper(), asWidget().getContentDisplay());
        bind(new FooterActivityMapper(), asWidget().getFooterDisplay());
        bind(new ExtraActivityMapper(), asWidget().getExtraDisplay());
        bind(new NotificationActivityMapper(), asWidget().getNotificationDisplay());

    }

}
