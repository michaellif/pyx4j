/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.ui.flex.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.ui.flex.demo.client.domain.DomainFactory;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.dialog.UnrecoverableErrorHandlerDialog;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WidgetsDemo implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(WidgetsDemo.class);

    @Override
    public void onModuleLoad() {

        StyleManger.installTheme(new WindowsTheme());

        ClientLogger.setDebugOn(true);
        ClientLogger.setTraceOn(true);
        UnrecoverableErrorHandlerDialog.register();

        VerticalPanel contentPanel = new VerticalPanel();

        RootPanel.get().add(GlassPanel.instance());
        RootPanel.get().add(contentPanel);
        contentPanel.setWidth("100%");

        //========== Report ==========//
        contentPanel.add(new HTML("<b>Report Test:</b>"));

        MainForm mainForm = new MainForm();
        mainForm.initContent();

        contentPanel.add(mainForm);

        mainForm.populate(DomainFactory.createEntityI());
    }

}
