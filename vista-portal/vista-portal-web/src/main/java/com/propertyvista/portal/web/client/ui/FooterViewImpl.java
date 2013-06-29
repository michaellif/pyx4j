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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
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

public class FooterViewImpl extends FlexTable implements FooterView {

    public FooterViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.PageFooter.name());

        SimplePanel pmcInfoPanel = new SimplePanel(new HTML("PMC Info"));
        SimplePanel followUsPanel = new SimplePanel(new HTML("Follow Us"));
        BrandPanel brandPanel = new BrandPanel();
        SimplePanel langSelectorPanel = new SimplePanel(new HTML("Languages"));
        SimplePanel linksPanel = new SimplePanel(new HTML("Terms of Use - Privacy - FAQ"));

        setWidget(0, 0, pmcInfoPanel);
        setWidget(0, 1, langSelectorPanel);
        setWidget(0, 2, followUsPanel);
        setWidget(0, 3, brandPanel);
        setWidget(1, 0, linksPanel);

        doLayout(LayoutType.getLayoutType(Window.getClientWidth()));

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    private void doLayout(LayoutType layoutType) {
    }

    class BrandPanel extends FlowPanel {
        public BrandPanel() {

            Image brandImage = new Image(PortalImages.INSTANCE.brand());
            brandImage.getElement().getStyle().setProperty("margin", "5px");
            brandImage.getElement().getStyle().setProperty("borderRadius", "4px");

            add(brandImage);
            add(new HTML("My Community + Vista"));

        }

    }

}
