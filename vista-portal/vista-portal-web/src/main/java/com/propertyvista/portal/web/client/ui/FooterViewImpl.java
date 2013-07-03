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
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class FooterViewImpl extends FlowPanel implements FooterView {

    private final BrandPanel brandPanel;

    public FooterViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.PageFooter.name());
        getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);

        SimplePanel pmcInfoPanel = new SimplePanel(new HTML("PMC Info"));
        pmcInfoPanel.getElement().getStyle().setFloat(Float.LEFT);
        pmcInfoPanel.setWidth("50%");

        FlowPanel actionsPanel = new FlowPanel();
        actionsPanel.getElement().getStyle().setFloat(Float.LEFT);
        actionsPanel.setWidth("50%");

        SimplePanel linksPanel = new SimplePanel(new HTML("Terms of Use - Privacy - FAQ"));
        linksPanel.getElement().getStyle().setFloat(Float.LEFT);
        linksPanel.getElement().getStyle().setProperty("textAlign", "center");
        linksPanel.setWidth("100%");

        SimplePanel followUsPanel = new SimplePanel(new HTML("Follow Us"));
        followUsPanel.getElement().getStyle().setFloat(Float.LEFT);
        followUsPanel.setWidth("30%");

        SimplePanel langSelectorPanel = new SimplePanel(new HTML("Languages"));
        langSelectorPanel.getElement().getStyle().setFloat(Float.LEFT);
        langSelectorPanel.setWidth("30%");

        brandPanel = new BrandPanel();
        brandPanel.getElement().getStyle().setFloat(Float.RIGHT);
        brandPanel.setWidth("40%");

        actionsPanel.add(langSelectorPanel);
        actionsPanel.add(followUsPanel);
        actionsPanel.add(brandPanel);

        add(pmcInfoPanel);
        add(actionsPanel);
        add(linksPanel);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
        brandPanel.doLayout(layoutType);
    }

    class BrandPanel extends FlowPanel {
        private final Image brandImage;

        public BrandPanel() {

            brandImage = new Image(PortalImages.INSTANCE.brandFooter());
            brandImage.getElement().getStyle().setProperty("margin", "-5px 5px 0 0");
            brandImage.getElement().getStyle().setFloat(Float.RIGHT);

            add(brandImage);
            add(new HTML("Vista"));

        }

        private void doLayout(LayoutType layoutType) {
            switch (layoutType) {
            case phonePortrait:
            case phoneLandscape:
            case tabletPortrait:
                brandImage.setResource(PortalImages.INSTANCE.brand());
                brandImage.getElement().getStyle().setProperty("margin", "5px 5px 0 0");
                brandImage.getElement().getStyle().setProperty("borderRadius", "4px");
                break;
            case tabletLandscape:
            case monitor:
            case huge:
                brandImage.setResource(PortalImages.INSTANCE.brandFooter());
                brandImage.getElement().getStyle().setProperty("margin", "-5px 5px 0 0");
                brandImage.getElement().getStyle().setProperty("borderRadius", "0");
                break;
            }
        }
    }

}
