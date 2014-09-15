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

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.shared.themes.PortalRootPaneTheme;
import com.propertyvista.portal.shared.ui.PointerId;

public class TipOfTheDayViewImpl extends FlowPanel implements TipOfTheDayView {

    private static final I18n i18n = I18n.get(TipOfTheDayViewImpl.class);

    public TipOfTheDayViewImpl() {
        setStyleName(PortalRootPaneTheme.StyleName.ExtraGadget.name());
    }

    @Override
    public void setTipOfTheDay(String text, ThemeColor color, PointerId pointerId, Command command) {
        clear();

        getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(color, 1));
        getElement().getStyle().setProperty("color", StyleManager.getPalette().getThemeColor(color, 0.1));
        getElement().getStyle().setTextAlign(TextAlign.LEFT);

        HTML textHTML = new HTML(text);
        add(textHTML);

    }

}
