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
package com.propertyvista.portal.resident.ui.extra;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;

public class QuickTipViewImpl extends SimplePanel implements QuickTipView {

    public QuickTipViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.QuickTipGadget.name());
    }

    @Override
    public void setQuickTip(HTMLPanel contentPanel, ThemeColor background) {
        setWidget(contentPanel);

        if (background != null) {
            getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(background, 1));
            getElement().getStyle().setProperty("color", StyleManager.getPalette().getThemeColor(background, 0.1));
        }
    }

}
