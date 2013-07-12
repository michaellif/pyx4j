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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.web.client.themes.DashboardTheme;

public abstract class AbstractGadget<T extends IsWidget> extends SimplePanel {

    private ContainerPanel containerPanel;

    private final ImageResource imageResource;

    private final String title;

    private ThemeColor themeColor;

    private T viewer;

    public AbstractGadget(T viewer, ImageResource imageResource, String title, ThemeColor themeColor) {
        this.viewer = viewer;
        this.imageResource = imageResource;
        this.title = title;
        this.themeColor = themeColor;
        setStyleName(DashboardTheme.StyleName.Gadget.name());

        containerPanel = new ContainerPanel();
        setWidget(containerPanel);
    }

    public AbstractGadget(T viewer, ThemeColor themeColor) {
        this(viewer, null, null, themeColor);
    }

    public T getGadgetViewer() {
        return viewer;
    }

    protected void setActionsToolbar(Toolbar actionsToolbar) {
        containerPanel.setActionsToolbar(actionsToolbar);
    }

    public void setContent(IsWidget widget) {
        containerPanel.setContentPanel(widget);
    }

    class ContainerPanel extends SimplePanel {

        private final FlowPanel mainPanel;

        private final SimplePanel contentHolder;

        private final SimplePanel actionsToolbarHolder;

        public ContainerPanel() {
            asWidget().setStyleName(DashboardTheme.StyleName.GadgetDecorator.name());

            mainPanel = new FlowPanel();
            mainPanel.setStyleName(DashboardTheme.StyleName.GadgetContent.name());
            mainPanel.getElement().getStyle().setProperty("borderTopWidth", "5px");
            mainPanel.getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

            FlowPanel containerPanel = new FlowPanel();
            containerPanel.setWidth("100%");
            containerPanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

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

            contentHolder = new SimplePanel();
            containerPanel.add(contentHolder);

            mainPanel.add(containerPanel);

            actionsToolbarHolder = new SimplePanel();
            mainPanel.add(actionsToolbarHolder);

            add(mainPanel);
        }

        public void setActionsToolbar(Toolbar actionsToolbar) {
            actionsToolbarHolder.setWidget(actionsToolbar);
        }

        public void setContentPanel(IsWidget widget) {
            contentHolder.setWidget(widget);
        }

    }
}
