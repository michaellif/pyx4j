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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.css.StyleManger;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.widgets.client.style.theme.WindowsPalette;
import com.pyx4j.widgets.client.style.theme.WindowsTheme;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FormsDemo implements EntryPoint {

    final Logger log = LoggerFactory.getLogger(FormsDemo.class);

    VerticalPanel wp;

    @Override
    public void onModuleLoad() {
        ClientLogger.setDebugOn(true);
        StyleManger.installTheme(new WindowsTheme(), new WindowsPalette());

        RootPanel.get().getElement().getStyle().setProperty("margin", "0");

        SimplePanel contentPanel = new SimplePanel();
        RootPanel.get().add(contentPanel);

        log.debug("This app is empty");
        //contentPanel.add(new HTML("This app is empty"));

        contentPanel.add(wp = new VerticalPanel());
        wp.add(new HTML("This app is empty"));

        Button b;
        wp.add(b = new Button("Show form"));

        b.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showForm();
            }

        });
    }

    private void showForm() {
        log.debug("will show form now");

        CTextField url = new CTextField("URL 1");
        CTextField url2 = new CTextField("URL 2");

        CComboBox<String> lb = new CComboBox<String>("LB");
        lb.setOptions(Arrays.asList(new String[] { "A", "B" }));

        CComponent[][] comps = new CComponent[][] {

        { url, url },

        { url2, lb } };

        wp.add(CForm.createFormWidget(LabelAlignment.LEFT, comps));
    }
}
