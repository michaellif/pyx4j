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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CGroupBoxPanel;
import com.pyx4j.widgets.client.style.StyleManger;
import com.pyx4j.widgets.client.style.window.WindowsTheme;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FormsDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(FormsDemo.class);

    public void onModuleLoad() {

        StyleManger.installTheme(new WindowsTheme());

        VerticalPanel contentPanel = new VerticalPanel();
        RootPanel.get().add(contentPanel);

        CComponent<?>[][] components = new CComponent[][] {

        { new CCheckBox("CCheckBox") },

        };

        CForm form = new CForm();

        form.setComponents(components);
        CGroupBoxPanel boxPanel = new CGroupBoxPanel("CGroupBoxPanel");
        boxPanel.setExpended(false);
        boxPanel.addComponent(form);

        contentPanel.add((Widget) boxPanel.initNativeComponent());

    }

}
