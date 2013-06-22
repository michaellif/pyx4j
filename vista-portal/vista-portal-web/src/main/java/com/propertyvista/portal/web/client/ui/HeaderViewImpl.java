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

        Image logoImage = new Image(MediaUtils.createSiteLogoUrl());

        SimplePanel logoHolder = new SimplePanel(logoImage);
        logoHolder.setWidth("200px");

        logoHolder.addStyleName(HorizontalAlignCenterMixin.StyleName.HorizontalAlignCenter.name());
        setWidget(logoHolder);

    }

}
