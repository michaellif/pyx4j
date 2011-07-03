/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 3, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.searchapt;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.style.IStyleSuffix;

public class CardPanel extends DockPanel {

    public static String DEFAULT_STYLE_PREFIX = "cardPanel";

    private final SimplePanel minorContent;

    private final SimplePanel imageHolder;

    private final SimplePanel majorContent;

    protected static I18n i18n = I18nFactory.getI18n(CardPanel.class);

    public static enum StyleSuffix implements IStyleSuffix {
        MinorContent, Content, Image
    }

    public CardPanel() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        getElement().getStyle().setProperty("minHeight", "130px");

        minorContent = new SimplePanel();
        minorContent.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MinorContent);

        imageHolder = new SimplePanel();
        imageHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Image);
        imageHolder.getElement().getStyle().setHeight(100, Unit.PX);
        imageHolder.getElement().getStyle().setWidth(150, Unit.PX);
        imageHolder.getElement().getStyle().setProperty("minHeight", "100px");

        majorContent = new SimplePanel();
        majorContent.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content);
        majorContent.getElement().getStyle().setHeight(100, Unit.PCT);
        majorContent.setHeight("100%");
        majorContent.getElement().getStyle().setMarginLeft(20, Unit.PX);

        add(minorContent, DockPanel.EAST);
        add(imageHolder, DockPanel.WEST);
        add(majorContent, DockPanel.CENTER);

    }

    public void setMajorContent(Widget w) {
        majorContent.setWidget(w);
    }

    public void setMajorContent(IsWidget w) {
        majorContent.setWidget(w.asWidget());
    }

    public void setCardImage(Widget w) {
        imageHolder.setWidget(w);
    }

    public void setCardImage(IsWidget w) {
        imageHolder.setWidget(w.asWidget());
    }

    public void setMinorContent(Widget w) {
        minorContent.setWidget(w);
    }

    public void setMinorContent(IsWidget w) {
        setMinorContent(w.asWidget());
    }
}
