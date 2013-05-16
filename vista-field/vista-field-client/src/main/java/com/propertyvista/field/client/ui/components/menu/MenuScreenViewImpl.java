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

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CollapsablePanel;
import com.pyx4j.widgets.client.event.shared.ToggleEvent;
import com.pyx4j.widgets.client.event.shared.ToggleHandler;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.field.client.theme.FieldTheme;

public class MenuScreenViewImpl extends VerticalPanel implements MenuScreenView {

    private static final I18n i18n = I18n.get(MenuScreenViewImpl.class);

    public MenuScreenViewImpl() {
        setSize("100%", "100%");

        Label home = createLabel("Home");
        Label profile = createLabel("Profile");
        Label settings = createLabel("Settings");
        Label notifications = createLabel("Notifications");
        Label recentlyVisited = createLabel("Recently Visited");
        Label arrears = createLabel("Arrears");
        Label maintenance = createLabel("Maintenance");

        final VerticalPanel collapsableContent = new VerticalPanel();
        collapsableContent.setHeight("100%");
        collapsableContent.add(arrears);
        collapsableContent.add(maintenance);

        CollapsablePanel collapsable = new CollapsablePanel(VistaImages.INSTANCE);
        collapsable.setWidget(collapsableContent);
        collapsable.addToggleHandler(new ToggleHandler() {

            @Override
            public void onToggle(ToggleEvent event) {
                collapsableContent.setVisible(event.isToggleOn());
            }
        });

        add(home);
        add(collapsable);
        add(profile);
        add(settings);
        add(notifications);
        add(recentlyVisited);
    }

    private Label createLabel(String text) {
        Label label = new Label();
        label.setText(i18n.tr(text));
        label.setStyleName(FieldTheme.StyleName.MenuScreenItem.name());
        return label;
    }
}
