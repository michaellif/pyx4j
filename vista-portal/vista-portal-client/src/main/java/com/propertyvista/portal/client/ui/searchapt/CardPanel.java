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

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderItemViewerDecorator;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class CardPanel extends FlowPanel {

    public static String DEFAULT_STYLE_PREFIX = "cardPanel";

    private final SimplePanel header;

    private final SimplePanel imageHolder;

    private final FlowPanel contentHolder;

    private final SimplePanel content;

    protected static I18n i18n = I18nFactory.getI18n(BaseFolderItemViewerDecorator.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Header, Content, Image
    }

    public CardPanel() {
        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        getElement().getStyle().setProperty("minHeight", "130px");

        header = new SimplePanel();
        header.setSize("100%", "15%");
        header.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Header);

        contentHolder = new FlowPanel();
        contentHolder.setSize("100%", "85%");

        SimplePanel imgEnvelope = new SimplePanel();
        //   envelope.setSize("28%", "100%");
        // envelope.setSize("150px", "100px");
        imgEnvelope.getElement().getStyle().setHeight(100, Unit.PX);
        imgEnvelope.getElement().getStyle().setWidth(150, Unit.PX);
        imgEnvelope.getElement().getStyle().setFloat(Float.LEFT);
        imageHolder = new SimplePanel();
        imageHolder.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Image);
        imageHolder.setSize("100%", "100%");
        imageHolder.getElement().getStyle().setProperty("minHeight", "100px");
        imgEnvelope.setWidget(imageHolder);

        SimplePanel cEnvelope = new SimplePanel();
        cEnvelope.getElement().getStyle().setHeight(100, Unit.PCT);
        cEnvelope.getElement().getStyle().setFloat(Float.LEFT);
        content = new SimplePanel();
        content.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content);
        content.setHeight("100%");
        content.getElement().getStyle().setMarginLeft(20, Unit.PX);
        cEnvelope.setWidget(content);
        //content.setSize("70%", "100%");
        //content.getElement().getStyle().setFloat(Float.LEFT);
        contentHolder.add(imgEnvelope);
        contentHolder.add(cEnvelope);

        add(header);
        add(contentHolder);

    }

    public void setCardContent(Widget w) {
        content.setWidget(w);
    }

    public void setCardContent(IsWidget w) {
        content.setWidget(w.asWidget());
    }

    public void setCardImage(Widget w) {
        imageHolder.setWidget(w);
    }

    public void setCardImage(IsWidget w) {
        imageHolder.setWidget(w.asWidget());
    }

    public void setCardHeader(Widget w) {
        header.setWidget(w);
    }

    public void setCardHeader(IsWidget w) {
        setCardHeader(w.asWidget());
    }
}
