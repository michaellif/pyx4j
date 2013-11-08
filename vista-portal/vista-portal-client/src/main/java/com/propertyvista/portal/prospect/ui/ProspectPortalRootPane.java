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
package com.propertyvista.portal.prospect.ui;

import com.propertyvista.portal.prospect.mvp.BreadcrumbsActivityMapper;
import com.propertyvista.portal.prospect.mvp.ContentActivityMapper;
import com.propertyvista.portal.prospect.mvp.ToolbarActivityMapper;
import com.propertyvista.portal.shared.mvp.FooterActivityMapper;
import com.propertyvista.portal.shared.mvp.HeaderActivityMapper;
import com.propertyvista.portal.shared.mvp.NotificationActivityMapper;
import com.propertyvista.portal.shared.ui.PortalRootPane;

public class ProspectPortalRootPane extends PortalRootPane {

    public ProspectPortalRootPane() {
        super();

        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new ToolbarActivityMapper(), asWidget().getToolbarDisplay());

        bind(new ContentActivityMapper(), asWidget().getContentDisplay());

        bind(new FooterActivityMapper(), asWidget().getFooterDisplay());
        bind(new NotificationActivityMapper(), asWidget().getNotificationDisplay());
        bind(new BreadcrumbsActivityMapper(), asWidget().getBreadcrumbsDisplay());

    }

}
