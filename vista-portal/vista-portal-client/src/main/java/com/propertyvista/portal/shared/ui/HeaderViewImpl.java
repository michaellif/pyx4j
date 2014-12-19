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
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.frontoffice.ui.layout.RequiresScroll;
import com.pyx4j.widgets.client.style.theme.HorizontalAlignCenterMixin;

import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class HeaderViewImpl extends SimplePanel implements HeaderView, RequiresScroll {

    private final Image bannerImage;

    public HeaderViewImpl() {

        setStyleName(PortalRootPaneTheme.StyleName.PageHeader.name());

        bannerImage = new Image();
        bannerImage.getElement().getStyle().setDisplay(Display.NONE);
        bannerImage.setWidth("100%");

        bannerImage.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        });

        bannerImage.addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());

        if (!PortalSite.getSiteDefinitions().portalBanner().image().isNull()) {
            bannerImage.setUrl(MediaUtils.createSiteImageResourceUrl(PortalSite.getSiteDefinitions().portalBanner().image()));
            bannerImage.getElement().getStyle().setDisplay(Display.BLOCK);
        } else {
            bannerImage.setUrl("");
            bannerImage.getElement().getStyle().setDisplay(Display.NONE);
        }

        setWidget(bannerImage);

    }

    @Override
    public void onScroll(int scrollPosition) {
        if (scrollPosition <= getOffsetHeight()) {
            getElement().getStyle().setOpacity(1 - Math.pow((double) scrollPosition / getOffsetHeight(), 4));
            getElement().getStyle().setProperty("transform", "translate(0px, " + scrollPosition / 2 + "px)");
        } else {
            getElement().getStyle().setOpacity(1);
        }
    }

}
