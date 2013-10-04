/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 2, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.web.client.themes.BlockMixin;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public abstract class AbstractGadget<T extends IsWidget> extends AbstractPortalPanel {

    private ContainerPanel containerPanel;

    private final ImageResource imageResource;

    private final String title;

    private ThemeColor themeColor;

    private double themeVibrance;

    private T view;

    public AbstractGadget(T view, ImageResource imageResource, String title, ThemeColor themeColor, double themeVibrance) {
        this.view = view;
        this.imageResource = imageResource;
        this.title = title;
        this.themeColor = themeColor;
        this.themeVibrance = themeVibrance;
        setStyleName(DashboardTheme.StyleName.Gadget.name());

        containerPanel = new ContainerPanel();
        setWidget(containerPanel);
    }

    public AbstractGadget(T viewer, ThemeColor themeColor, double themeVibrance) {
        this(viewer, null, null, themeColor, themeVibrance);
    }

    public T getGadgetView() {
        return view;
    }

    protected void setActionsToolbar(Toolbar actionsToolbar) {
        containerPanel.actionsToolbarHolder.setWidget(actionsToolbar);
        containerPanel.actionsToolbarHolder.setVisible(true);
    }

    protected void setNavigationBar(Panel navigationBar) {
        containerPanel.navigationBarHolder.setWidget(navigationBar);
        containerPanel.navigationBarHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
    }

    public void setContent(IsWidget widget) {
        containerPanel.contentHolder.setWidget(widget);
    }

    class ContainerPanel extends FlowPanel {

        private final SimplePanel contentHolder;

        private final SimplePanel actionsToolbarHolder;

        private final SimplePanel navigationBarHolder;

        public ContainerPanel() {

            setStyleName(DashboardTheme.StyleName.GadgetContent.name());
            addStyleName(BlockMixin.StyleName.PortalBlock.name());
            getElement().getStyle().setProperty("borderTopWidth", "5px");
            getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, themeVibrance));

            FlowPanel containerPanel = new FlowPanel();
            containerPanel.setWidth("100%");
            containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            containerPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

            if (imageResource != null || title != null) {
                FlowPanel headerPanel = new FlowPanel();
                headerPanel.setStyleName(DashboardTheme.StyleName.GadgetHeader.name());
                headerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

                if (imageResource != null) {
                    Image icon = new Image(imageResource);
                    icon.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                    icon.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                    icon.getElement().getStyle().setMarginRight(10, Unit.PX);
                    headerPanel.add(icon);
                }
                if (title != null) {
                    HTML titleLabel = new HTML(title);
                    titleLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                    titleLabel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
                    headerPanel.add(titleLabel);
                }
                containerPanel.add(headerPanel);
            }

            FlowPanel contentPanel = new FlowPanel();
            contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

            contentHolder = new SimplePanel();
            contentHolder.getElement().getStyle().setFloat(Float.NONE);
            contentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
            contentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
            contentHolder.getElement().getStyle().setProperty("padding", "10px 0");
            contentHolder.setWidth("100%");

            contentPanel.add(contentHolder);

            navigationBarHolder = new SimplePanel();
            navigationBarHolder.getElement().getStyle().setFloat(Float.NONE);
            navigationBarHolder.getElement().getStyle().setDisplay(Display.NONE);
            navigationBarHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);

            contentPanel.add(navigationBarHolder);

            containerPanel.add(contentPanel);

            add(containerPanel);

            actionsToolbarHolder = new SimplePanel();
            actionsToolbarHolder.setVisible(false);
            add(actionsToolbarHolder);

        }

    }

}
