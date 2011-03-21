/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.tester;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.ClientEntityFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.log4gwt.rpcappender.RPCAppender;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.unit.client.ui.TestRunnerDialog;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.Theme;
import com.pyx4j.widgets.client.style.ThemeColor;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public class TesterClientEntryPoint extends AppSite {

    @Override
    public void onModuleLoad() {

        ClientEntityFactory.ensureIEntityImplementations();
        UnrecoverableErrorHandlerDialog.register();
        Theme theme = new WindowsTheme();
        theme.putThemeColor(ThemeColor.OBJECT_TONE1, 0xFFFFFF);
        StyleManger.installTheme(theme);

        hideLoadingIndicator();

        ClientLogger.addAppender(new RPCAppender());
        ClientLogger.setDebugOn(true);
        RPCManager.enableAppEngineUsageStats();

        VerticalPanel menu = new VerticalPanel();
        RootPanel.get().add(menu, 30, 30);
        menu.add(new HTML("This version compiled with GWT <b>" + GWT.getVersion() + "</b>"));

        final Button startButton = new Button("Start Client Side (GWT) Tests");
        startButton.ensureDebugId("startClientTests");
        menu.add(startButton);
        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestRunnerDialog.createAsync();
            }
        });

        HorizontalPanel logos = new HorizontalPanel();
        menu.add(logos);

        logos.add(new Image("vista_logo-48x48.png"));
        logos.add(new Anchor("propertyvista.jira.com", "http://propertyvista.jira.com"));
        logos.add(new HTML(CommonsStringUtils.NO_BREAK_SPACE_HTML));
        logos.add(new Image("pyx_logo-32x32.png"));
        logos.add(new Anchor("code.pyx4j.com", "http://code.pyx4j.com"));
        logos.add(new HTML(CommonsStringUtils.NO_BREAK_SPACE_HTML));
        logos.add(new Image("gwt-logo-30x30.png"));

    }

    @Override
    public void onSiteLoad() {
    }

}
