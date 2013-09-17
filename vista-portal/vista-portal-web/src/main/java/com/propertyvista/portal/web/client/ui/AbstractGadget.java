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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.web.client.themes.BlockMixin;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public abstract class AbstractGadget<T extends IsWidget> extends AbstractPortalPanel {

    private ContainerPanel containerPanel;

    private final ImageResource imageResource;

    private final String title;

    private ThemeColor themeColor;

    private T view;

    public AbstractGadget(T view, ImageResource imageResource, String title, ThemeColor themeColor) {
        this.view = view;
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

    public T getGadgetView() {
        return view;
    }

    protected void setActionsToolbar(Toolbar actionsToolbar) {
        containerPanel.actionsToolbarHolder.setWidget(actionsToolbar);
    }

    protected void setNavigationBar(Panel navigationBar) {
        containerPanel.navigationBarHolder.setWidget(navigationBar);
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
            getElement().getStyle().setProperty("borderTopColor", StyleManager.getPalette().getThemeColor(themeColor, 1));

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
            contentHolder.getElement().getStyle().setProperty("padding", "10px 0");

            contentPanel.add(contentHolder);

            navigationBarHolder = new SimplePanel();
            contentPanel.add(navigationBarHolder);

            containerPanel.add(contentPanel);

            add(containerPanel);

            actionsToolbarHolder = new SimplePanel();
            add(actionsToolbarHolder);

            doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

            AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

                @Override
                public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                    doLayout(event.getLayoutType());
                }

            });
        }

        private void doLayout(LayoutType layoutType) {
            switch (layoutType) {
            case phonePortrait:
            case phoneLandscape:
            case tabletPortrait:
                contentHolder.getElement().getStyle().setFloat(Float.NONE);
                contentHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                contentHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
                contentHolder.setWidth("100%");

                navigationBarHolder.getElement().getStyle().setFloat(Float.NONE);
                navigationBarHolder.getElement().getStyle().setMarginTop(10, Unit.PX);
                navigationBarHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                navigationBarHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
                navigationBarHolder.setWidth("100%");

                break;
            default:
                contentHolder.getElement().getStyle().setDisplay(Display.BLOCK);
                contentHolder.getElement().getStyle().setFloat(Float.LEFT);
                contentHolder.setWidth("70%");

                navigationBarHolder.getElement().getStyle().setDisplay(Display.BLOCK);
                navigationBarHolder.getElement().getStyle().setFloat(Float.RIGHT);
                navigationBarHolder.setWidth("30%");

                break;
            }
        }
    }

}
