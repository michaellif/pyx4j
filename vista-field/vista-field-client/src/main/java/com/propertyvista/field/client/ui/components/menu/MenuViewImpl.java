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
package com.propertyvista.field.client.ui.components.menu;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

public class MenuViewImpl extends FlowPanel implements MenuView {

    private static final I18n i18n = I18n.get(MenuViewImpl.class);

    public MenuViewImpl() {

        setSize("100%", "100%");

        Label home = createLabel("Home");
        Label profile = createLabel("Profile");
        Label settings = createLabel("Settings");
        Label notifications = createLabel("Notifications");
        Label recentlyVisited = createLabel("Recently Visited");

        add(home);
        add(profile);
        add(settings);
        add(notifications);
        add(recentlyVisited);

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
            getElement().getStyle().setBackgroundColor("#abc");
            setWidth("auto");
            break;
        case tabletPortrait:
        case tabletLandscape:
            getElement().getStyle().setBackgroundColor("#caa");
            setWidth("50px");
            break;
        default:
            getElement().getStyle().setBackgroundColor("#caa");
            setWidth("200px");
            break;
        }
    }

    private Label createLabel(String text) {
        Label label = new Label();
        label.setText(i18n.tr(text));

        return label;
    }

}
