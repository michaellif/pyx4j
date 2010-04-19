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
package com.pyx4j.forms.demo.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.CFlexForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CLayoutConstraints;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FormsDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(FormsDemo.class);

    public void onModuleLoad() {

        StyleManger.installTheme(new WindowsTheme());

        ScrollPanel contentPanel = new ScrollPanel();
        contentPanel.setSize("200px", "200px");
        RootPanel.get().add(contentPanel);

        CFlexForm form = new CFlexForm();

        for (int i = 0; i < 20; i++) {
            CTextArea l = new CTextArea(i + "");
            l.setWidth("100%");
            l.setHeight("100%");
            l.setValue(i + "");
            CLayoutConstraints constraint = new CLayoutConstraints();
            if (i == 1) {
                constraint.colSpan = 2;
                constraint.rowSpan = 2;
            }
            l.setConstraints(constraint);
            form.addComponent(l);

        }

        contentPanel.setWidget(form.initNativeComponent());

    }
}
