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

import com.pyx4j.site.client.ui.layout.ResponsiveLayoutPanel.DisplayType;

import com.propertyvista.portal.prospect.mvp.ContentActivityMapper;
import com.propertyvista.portal.prospect.mvp.MenuActivityMapper;
import com.propertyvista.portal.prospect.mvp.RentalSummaryActivityMapper;
import com.propertyvista.portal.prospect.mvp.ToolbarActivityMapper;
import com.propertyvista.portal.shared.mvp.FooterActivityMapper;
import com.propertyvista.portal.shared.mvp.HeaderActivityMapper;
import com.propertyvista.portal.shared.mvp.NotificationActivityMapper;
import com.propertyvista.portal.shared.ui.PortalRootPane;

public class ProspectPortalRootPane extends PortalRootPane {

    public ProspectPortalRootPane() {
        super();

        bind(new HeaderActivityMapper(), asWidget().getDisplay(DisplayType.header));
        bind(new ToolbarActivityMapper(), asWidget().getDisplay(DisplayType.toolbar));
        bind(new MenuActivityMapper(), asWidget().getDisplay(DisplayType.menu));
        bind(new ContentActivityMapper(), asWidget().getDisplay(DisplayType.content));
        bind(new FooterActivityMapper(), asWidget().getDisplay(DisplayType.footer));
        bind(new RentalSummaryActivityMapper(), asWidget().getDisplay(DisplayType.extra));
        bind(new NotificationActivityMapper(), asWidget().getDisplay(DisplayType.notification));
    }

}
