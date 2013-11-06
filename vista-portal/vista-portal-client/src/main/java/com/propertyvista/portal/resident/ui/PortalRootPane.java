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

import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.RootPane;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel;

import com.propertyvista.portal.resident.mvp.CommunicationActivityMapper;
import com.propertyvista.portal.resident.mvp.ContentActivityMapper;
import com.propertyvista.portal.resident.mvp.ExtraActivityMapper;
import com.propertyvista.portal.resident.mvp.FooterActivityMapper;
import com.propertyvista.portal.resident.mvp.HeaderActivityMapper;
import com.propertyvista.portal.resident.mvp.MenuActivityMapper;
import com.propertyvista.portal.resident.mvp.NotificationActivityMapper;
import com.propertyvista.portal.resident.mvp.StickyHeaderActivityMapper;

public class PortalRootPane extends RootPane<ResponsiveLayoutPanel> {

    public PortalRootPane() {
        super(new ResponsiveLayoutPanel());

        bind(new HeaderActivityMapper(), asWidget().getHeaderDisplay());
        bind(new StickyHeaderActivityMapper(), asWidget().getToolbarDisplay());
        bind(new MenuActivityMapper(), asWidget().getMenuDisplay());
        bind(new CommunicationActivityMapper(), asWidget().getCommDisplay());
        bind(new ContentActivityMapper(), asWidget().getContentDisplay());
        bind(new FooterActivityMapper(), asWidget().getFooterDisplay());
        bind(new ExtraActivityMapper(), asWidget().getExtraDisplay());
        bind(new NotificationActivityMapper(), asWidget().getNotificationDisplay());

    }

    @Override
    protected void onPlaceChange(Place place) {
        asWidget().scrollToTop();
    }
}
