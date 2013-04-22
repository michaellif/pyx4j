/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.operations.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.site.client.DisplayPanel;
import com.pyx4j.site.client.RootPane;

import com.propertyvista.common.client.theme.CrmSitePanelTheme;
import com.propertyvista.operations.client.mvp.LogoActivityMapper;
import com.propertyvista.operations.client.mvp.MainDisplayActivityMapper;
import com.propertyvista.operations.client.mvp.TopRightActionsActivityMapper;
import com.propertyvista.operations.client.themes.OperationsPalette;
import com.propertyvista.operations.client.themes.OperationsTheme;
import com.propertyvista.operations.client.viewfactories.OperationsVeiwFactory;

public class OperationsRootPane extends LayoutPanel {

    public static String DEFAULT_STYLE_PREFIX = "SiteView";

    public OperationsRootPane() {

        StyleManager.installTheme(new OperationsTheme(), new OperationsPalette());

        setStyleName(CrmSitePanelTheme.StyleName.SiteView.name());

        DockLayoutPanel contentPanel = new DockLayoutPanel(Unit.EM);
        contentPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewContent.name());
        add(contentPanel);

        //============ Header Panel ============

        FlowPanel headerPanel = new FlowPanel();
        contentPanel.addNorth(headerPanel, 5);
        headerPanel.setStyleName(CrmSitePanelTheme.StyleName.SiteViewHeader.name());

        DisplayPanel logoDisplay = new DisplayPanel();
        //VS should correspond with the logo size
        logoDisplay.setSize("30%", "100%");
        logoDisplay.getElement().getStyle().setFloat(Style.Float.LEFT);
        headerPanel.add(logoDisplay);

        DisplayPanel actionsDisplay = new DisplayPanel();
        actionsDisplay.setSize("70%", "100%");
        actionsDisplay.getElement().getStyle().setFloat(Style.Float.RIGHT);
        headerPanel.add(actionsDisplay);

        DisplayPanel mainDisplay = new DisplayPanel();
        contentPanel.add(mainDisplay);

        RootPane.bind(new LogoActivityMapper(), logoDisplay);
        RootPane.bind(new TopRightActionsActivityMapper(), actionsDisplay);
        RootPane.bind(new MainDisplayActivityMapper(), mainDisplay);

        OperationsVeiwFactory.instance(MainDisplayView.class);

    }

}
