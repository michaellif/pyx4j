/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Mar 5, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.viewer;

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

import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class BaseFolderItemViewerDecorator extends SimplePanel implements IFolderItemViewerDecorator {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_BaseFolderItemViewerDecorator";

    private final VerticalPanel container;

    private final FlowPanel content;

    private final FlowPanel menu;

    private final Anchor viewDetailsItem;

    private final FlowPanel menuContainer;

    protected static I18n i18n = I18nFactory.getI18n(BaseFolderItemViewerDecorator.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Menu
    }

    public static enum StyleDependent implements IStyleDependent {
        hover
    }

    public BaseFolderItemViewerDecorator() {

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
                addStyleDependentName(StyleDependent.hover.name());
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

    @Override
    public void addMenuItem(Widget menuItem) {
        menu.add(menuItem);

    }

    @Override
    public void addClickHandler(ClickHandler h) {
        viewDetailsItem.addClickHandler(h);

    }

}
