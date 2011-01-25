/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-01-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

public class VistaSiteDispatcher implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(VistaSiteDispatcher.class);

    @Override
    public void onModuleLoad() {
        ClientLogger.setDebugOn(true);
        StyleManger.installTheme(new WindowsTheme());

        SimplePanel contentPanel = new SimplePanel();
        RootPanel.get().add(contentPanel);

        contentPanel.add(new HTML("The Property Vista portal would be here!"));

        log.debug("Log should work; But This app is empty");
    }

}
