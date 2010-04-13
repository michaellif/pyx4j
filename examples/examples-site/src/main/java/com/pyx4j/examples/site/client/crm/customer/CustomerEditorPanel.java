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
 * Created on Feb 23, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.crud.AbstractEntityEditorPanel;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextArea;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public class CustomerEditorPanel extends AbstractEntityEditorPanel<Customer> {

    CustomerEditorPanel() {
        super(Customer.class);

        VerticalPanel contentPanel = new VerticalPanel();

        setWidget(contentPanel);

        CTextArea notesEditor = (CTextArea) create(meta().note());
        notesEditor.setColumns(80);

        CComponent<?>[][] advancedSearchComponents = new CComponent[][] {

        { create(meta().name()), create(meta().phone()) },

        { create(meta().street()), create(meta().zip()) },

        { notesEditor, notesEditor },

        };

        Widget formWidget = CForm.createFormWidget(LabelAlignment.LEFT, advancedSearchComponents);
        contentPanel.add(formWidget);

        Button viewButton = new Button("Save");
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                //TODO

            }
        });
        viewButton.getElement().getStyle().setProperty("margin", "8px 0px 3px 0px");
        contentPanel.add(viewButton);

    }

}
