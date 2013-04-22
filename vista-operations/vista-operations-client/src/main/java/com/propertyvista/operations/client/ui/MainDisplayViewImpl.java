/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 18, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.operations.client.ui;

import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.RootPane;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.operations.client.mvp.ContentActivityMapper;
import com.propertyvista.operations.client.mvp.FooterActivityMapper;
import com.propertyvista.operations.client.mvp.NavigActivityMapper;
import com.propertyvista.operations.client.mvp.ShortCutsActivityMapper;

public class MainDisplayViewImpl extends SplitLayoutPanel implements MainDisplayView {

    private MainDisplayPresenter presenter;

    public MainDisplayViewImpl() {
        super(4);

        //================ Main application area - splitter with navig menu and content =======

        ensureDebugId("splitPanel");
        setSize("100%", "100%");

        //============ Left Panel ============

        VerticalPanel leftPanel = new VerticalPanel();
        leftPanel.setSize("100%", "100%");

        addWest(leftPanel, 200);
        setWidgetMinSize(leftPanel, 150);

        DisplayPanel navigDisplay = new DisplayPanel();
        leftPanel.add(navigDisplay);
        navigDisplay.setSize("100%", "100%");

        DisplayPanel shortcutsDisplay = new DisplayPanel();
        shortcutsDisplay.setSize("100%", "100%");
        leftPanel.add(shortcutsDisplay);

        // here goes truncated Footer Panel:
        DisplayPanel footerDisplay = new DisplayPanel();
        leftPanel.add(footerDisplay);
        footerDisplay.setSize("100%", "100%");

        // layout:
        leftPanel.setCellWidth(navigDisplay, "100%");
        leftPanel.setCellHeight(navigDisplay, "65%");

        leftPanel.setCellWidth(shortcutsDisplay, "100%");
        leftPanel.setCellHeight(shortcutsDisplay, "35%");

        leftPanel.setCellWidth(footerDisplay, "100%");
        leftPanel.setCellHeight(footerDisplay, "40px");

        leftPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewNavigContainer.name());

        //============ Main Panel ============

        DisplayPanel contentDisplay = new DisplayPanel();
        add(contentDisplay);

        RootPane.bind(new FooterActivityMapper(), footerDisplay);
        RootPane.bind(new NavigActivityMapper(), navigDisplay);
        RootPane.bind(new ShortCutsActivityMapper(), shortcutsDisplay);

        RootPane.bind(new ContentActivityMapper(), contentDisplay);

    }

    @Override
    public void setPresenter(MainDisplayPresenter presenter) {
        this.presenter = presenter;
    }

}
