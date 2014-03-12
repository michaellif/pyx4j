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
 * Created on Feb 11, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.devconsole;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.resources.SiteImages;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Toolbar;

public class ResponsiveLayoutDevConsole extends AbstractDevConsole {

    private final Image deviceImage;

    private final ResponsiveLayoutPanel responsiveLayoutPanel;

    public ResponsiveLayoutDevConsole(final ResponsiveLayoutPanel responsiveLayoutPanel) {
        this.responsiveLayoutPanel = responsiveLayoutPanel;

        getElement().getStyle().setPadding(20, Unit.PX);

        Toolbar toolbar = new Toolbar();
        add(toolbar);
        toolbar.addItem(new SetMocksButton());

        deviceImage = new Image();
        SimplePanel deviceImageHolder = new SimplePanel(deviceImage);
        deviceImageHolder.getElement().getStyle().setProperty("padding", "5px");
        deviceImageHolder.getElement().getStyle().setProperty("marginRight", "15px");
        deviceImageHolder.getElement().getStyle().setProperty("borderRadius", "5px");
        deviceImageHolder.getElement().getStyle().setProperty("background", "white");
        toolbar.addItem(deviceImageHolder);

        AppSite.getEventBus().addHandler(LayoutChangeEvent.TYPE, new LayoutChangeHandler() {

            @Override
            public void onLayoutChangeRerquest(LayoutChangeEvent event) {
                doLayout(event.getLayoutType());
            }

        });
    }

    @Override
    void setMockValues() {
        setMockValues(responsiveLayoutPanel);
    }

    private void doLayout(LayoutType layoutType) {
        if (ApplicationMode.isDevelopment()) {
            switch (layoutType) {
            case phonePortrait:
                deviceImage.setResource(SiteImages.INSTANCE.phone());
                break;
            case phoneLandscape:
                deviceImage.setResource(SiteImages.INSTANCE.phoneL());
                break;
            case tabletPortrait:
                deviceImage.setResource(SiteImages.INSTANCE.tablet());
                break;
            case tabletLandscape:
                deviceImage.setResource(SiteImages.INSTANCE.tabletL());
                break;
            case monitor:
                deviceImage.setResource(SiteImages.INSTANCE.monitor());
                break;
            case huge:
                deviceImage.setResource(SiteImages.INSTANCE.huge());
                break;
            }
        }
    }

}
