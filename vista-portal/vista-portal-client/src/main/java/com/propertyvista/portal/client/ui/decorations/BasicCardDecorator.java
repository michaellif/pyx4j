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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.widgets.client.style.IStyleDependent;
import com.pyx4j.widgets.client.style.IStyleSuffix;

import com.propertyvista.portal.client.resources.PortalImages;

public class BasicCardDecorator<E extends IEntity> extends SimplePanel implements IFolderItemViewerDecorator<E> {

    public static String DEFAULT_STYLE_PREFIX = "pyx4j_BaseFolderItemViewerDecorator";

    private final DockPanel container;

    private final SimplePanel content;

    private final FlowPanel menu;

    private final Anchor viewDetailsItem;

    private final VerticalPanel menuContainer;

    private CEntityFolderItemViewer<E> viewer;

    protected static I18n i18n = I18nFactory.getI18n(BasicCardDecorator.class);

    public static enum StyleSuffix implements IStyleSuffix {
        Menu, MenuItem, MenuItemLine, Content
    }

    public static enum StyleDependent implements IStyleDependent {
        hover
    }

    public BasicCardDecorator() {

        setStyleName(DEFAULT_STYLE_PREFIX);
        setSize("100%", "100%");
        container = new DockPanel();
        container.setSize("100%", "100%");
        container.setSpacing(5);

        menuContainer = new VerticalPanel();
        menuContainer.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Menu);
        menuContainer.setHeight("100%");
        menuContainer.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        menuContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        menu = new FlowPanel();
        menuContainer.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        menuContainer.add(menu);

        viewDetailsItem = new Anchor(i18n.tr("Details"));
        //TODO change the image
        addMenuItem(viewDetailsItem, PortalImages.INSTANCE.map());

        content = new SimplePanel();
        content.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Content);

        container.add(content, DockPanel.CENTER);
        container.add(menuContainer, DockPanel.EAST);

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

    public void setContentWidth(String width) {
        content.setWidth(width);
    }

    public void setMenuWidth(String width) {
        menuContainer.setWidth(width);
    }

    public void addMenuItem(Anchor anchor, ImageResource imageResource) {
        anchor.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MenuItem);
        HorizontalPanel item = new HorizontalPanel();
        Image image = new Image(imageResource);
        item.add(image);
        item.add(anchor);
        item.setCellHorizontalAlignment(anchor, HasHorizontalAlignment.ALIGN_LEFT);
        item.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.MenuItemLine);
        int i = menu.getWidgetCount();
        if (i == 0) {
            menu.add(item);
        } else {
            menu.insert(item, i - 1);
        }
    }

    public HandlerRegistration addViewDetailsClickHandler(ClickHandler h) {
        return viewDetailsItem.addClickHandler(h);
    }

}
