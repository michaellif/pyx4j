/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2014
 * @author michaellif
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.images.ButtonImages;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class NotNavigableMenuItem extends MenuItem<Image> {

    private final ButtonImages images;

    public NotNavigableMenuItem(final String caption, ButtonImages images, ThemeColor color, Command command) {
        super(caption, command, new Image(images.regular()), color);
        this.images = images;

        getIcon().setStyleName(PortalRootPaneTheme.StyleName.MainMenuIcon.name());

    }

    @Override
    public void setSelected(boolean select) {
        super.setSelected(select);
        if (select) {
            getIcon().setResource(images.active());
        } else {
            getIcon().setResource(images.regular());
        }
    }
}
