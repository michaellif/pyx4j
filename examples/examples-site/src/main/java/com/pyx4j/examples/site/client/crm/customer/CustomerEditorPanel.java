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
import com.pyx4j.entity.client.ui.crud.EntityEditorForm;
import com.pyx4j.entity.client.ui.crud.AbstractEntityEditorPanel;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public class CustomerEditorPanel extends AbstractEntityEditorPanel<Customer> {

    private final EntityEditorForm<Customer> form;

    CustomerEditorPanel() {

        VerticalPanel contentPanel = new VerticalPanel();

        setWidget(contentPanel);

        form = EntityEditorForm.create(Customer.class);

        CComponent<?>[][] advancedSearchComponents = new CComponent[][] {

        { form.create(form.meta().name()) },

        // TODO Should probably use textarea 
                //{ form.create(form.meta().phone()) },

                { form.create(form.meta().street()) },

                { form.create(form.meta().zip()) },

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
        viewButton.getElement().getStyle().setProperty("margin", "5px 0px 5px 150px");
        contentPanel.add(viewButton);

    }

    @Override
    public void populateForm(Customer customer) {
        form.populate(customer);
    }

    @Override
    public Customer getEntity() {
        return form.getValue();
    }
}
