/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.decorations;

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
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class BasicCardDecorator<E extends IEntity> extends SimplePanel implements IFolderItemViewerDecorator<E> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_BaseFolderItemViewerDecorator";

    private final VerticalPanel container;

    private final SimplePanel content;

    private final FlowPanel menu;

    private final Anchor viewDetailsItem;

    private final FlowPanel menuContainer;

    private CEntityFolderItemViewer<E> viewer;

    protected static I18n i18n = I18nFactory.getI18n(BasicCardDecorator.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Menu, MenuItem, Content
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
        menu.getElement().getStyle().setPaddingTop(5, Unit.PX);
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

        content = new SimplePanel();
        content.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content);
        content.setSize("100%", "80%");
        content.getElement().getStyle().setFloat(Float.RIGHT);

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
    public void setFolderItem(CEntityFolderItemViewer<E> viewer) {
        this.viewer = viewer;
        content.setWidget(viewer.getContainer());
    }

    public CEntityFolderItemViewer<E> getFolderItem() {
        return viewer;
    }

    public void addMenuItem(Anchor anchor, ImageResource imageResource) {
        anchor.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MenuItem);
        anchor.getElement().getStyle().setFontWeight(FontWeight.BOLD);

        HorizontalPanel item = new HorizontalPanel();
        Image image = new Image(imageResource);
        item.add(image);
        item.add(anchor);
        menu.add(item);

    }

    public HandlerRegistration addViewDetailsClickHandler(ClickHandler h) {
        return viewDetailsItem.addClickHandler(h);
    }

}
