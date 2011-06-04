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
package com.propertyvista.portal.client.decorators;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.flex.viewer.BaseFolderItemViewerDecorator;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class BasicCardDecorator extends BaseFolderItemViewerDecorator {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_BaseFolderItemViewerDecorator";

    private final VerticalPanel container;

    private final FlowPanel content;

    private final FlowPanel menu;

    private final Anchor viewDetailsItem;

    private final FlowPanel menuContainer;

    protected static I18n i18n = I18nFactory.getI18n(BaseFolderItemViewerDecorator.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Menu, MenuItem
    }

    public static enum StyleDependent implements IStyleDependent {
        hover
    }

    public BasicCardDecorator() {

        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        container = new VerticalPanel();
        container.setSize("100%", "100%");
        container.getElement().getStyle().setPadding(5, Unit.PX);

        menuContainer = new FlowPanel();
        menuContainer.setSize("100%", "20%");
        menuContainer.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Menu);

        SimplePanel menuPanel = new SimplePanel();
        menuPanel.setHeight("100%");
        menuPanel.getElement().getStyle().setFloat(Float.LEFT);
        menu = new FlowPanel();
        menuPanel.setWidget(menu);
        menu.setHeight("100%");
        menuContainer.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        menuContainer.add(menuPanel);

        viewDetailsItem = new Anchor(i18n.tr("View Details"));
        viewDetailsItem.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MenuItem);
        viewDetailsItem.getElement().getStyle().setFloat(Float.RIGHT);
        viewDetailsItem.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        SimplePanel viewDetailsPanel = new SimplePanel();
        viewDetailsPanel.setSize("10em", "100%");
        viewDetailsPanel.getElement().getStyle().setFloat(Float.RIGHT);
        viewDetailsPanel.add(viewDetailsItem);
        menuContainer.add(viewDetailsPanel);

        content = new FlowPanel();
        content.setSize("100%", "80%");

        container.add(content);
        container.add(menuContainer);

        addDomHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                setStyleDependentName(StyleDependent.hover.name(), true);
                menuContainer.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
            }
        }, MouseOverEvent.getType());
        addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                removeStyleDependentName(StyleDependent.hover.name());
                menuContainer.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }, MouseOutEvent.getType());

        setWidget(container);

    }

    @Override
    public void setFolderItemContainer(Widget w) {
        content.clear();
        content.add(w);
    }

    public void addMenuItem(Anchor menuItem) {
        menuItem.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MenuItem);
        menu.add(menuItem);

    }

    public void addClickHandler(ClickHandler h) {
        viewDetailsItem.addClickHandler(h);

    }

}
