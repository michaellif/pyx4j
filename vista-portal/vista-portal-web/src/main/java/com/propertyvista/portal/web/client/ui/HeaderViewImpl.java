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
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.propertyvista.common.client.theme.HorizontalAlignCenterMixin;
import com.propertyvista.common.client.ui.components.MediaUtils;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class HeaderViewImpl extends SimplePanel implements HeaderView {

    public HeaderViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.PageHeader.name());

        SimplePanel logoHolder = new SimplePanel();
        logoHolder.getElement().getStyle().setProperty("backgroundImage", "url('" + MediaUtils.createSiteLogoUrl() + "')");
        logoHolder.getElement().getStyle().setProperty("backgroundSize", "contain");
        logoHolder.getElement().getStyle().setProperty("backgroundRepeat", "no-repeat");
        logoHolder.getElement().getStyle().setProperty("borderLeft", "4px solid #666");
        logoHolder.getElement().getStyle().setProperty("borderRight", "4px solid #666");
        logoHolder.setWidth(new Image(MediaUtils.createSiteLogoUrl()).getWidth() + "px");
        logoHolder.setHeight(new Image(MediaUtils.createSiteLogoUrl()).getHeight() + "px");

        logoHolder.addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());
        setWidget(logoHolder);

    }

}
