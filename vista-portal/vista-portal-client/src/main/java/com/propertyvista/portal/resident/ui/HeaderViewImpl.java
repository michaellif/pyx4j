/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.portal.rpc.portal.web.dto.PortalContentDTO;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class HeaderViewImpl extends SimplePanel implements HeaderView {

    private final Image bannerImage;

    public HeaderViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.PageHeader.name());

        bannerImage = new Image();
        bannerImage.setVisible(false);
        bannerImage.setWidth("100%");

        bannerImage.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        });

        bannerImage.addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());
        bannerImage.getElement().getStyle().setDisplay(Display.BLOCK);

        setWidget(bannerImage);

    }

    @Override
    public void setContent(PortalContentDTO content) {
        if (!content.portalBanner().image().isNull()) {
            bannerImage.setUrl(MediaUtils.createSiteImageResourceUrl(content.portalBanner().image()));
            bannerImage.setVisible(true);
        } else {
            bannerImage.setUrl("");
            bannerImage.setVisible(false);
        }
    }
}
