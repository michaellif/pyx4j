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
 * Created on Feb 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.EntitySearchCriteriaForm;
import com.pyx4j.entity.client.ui.IEntitySearchCriteriaPanel;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CGroupBoxPanel;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CGroupBoxPanel.Layout;

public class CustomerSearchCriteriaPanel extends SimplePanel implements IEntitySearchCriteriaPanel<Customer> {

    private final EntitySearchCriteriaForm<Customer> form;

    public CustomerSearchCriteriaPanel() {

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        form = EntitySearchCriteriaForm.create(Customer.class);

        {

            CComponent<?>[][] components = new CComponent[][] {

            { form.create(form.meta().name()) },

            { form.create(form.meta().street()) },

            };

            CGroupBoxPanel group = new CGroupBoxPanel("Customer Search", Layout.CHECKBOX_TOGGLE);
            group.setExpended(true);
            CForm form = new CForm(LabelAlignment.LEFT);
            form.setComponents(components);
            group.addComponent(form);
            Widget basicSearchWidget = (Widget) group.initNativeComponent();
            contentPanel.add(basicSearchWidget);
        }

        {
            CComponent<?>[][] components = new CComponent[][] {

            { form.create(form.meta().name()) },

            { form.create(form.meta().orders().$().status()) },

            };

            CGroupBoxPanel group = new CGroupBoxPanel("Location Search", Layout.CHECKBOX_TOGGLE);
            group.setExpended(true);
            CForm form = new CForm(LabelAlignment.LEFT);
            form.setComponents(components);
            group.addComponent(form);
            Widget basicSearchWidget = (Widget) group.initNativeComponent();
            contentPanel.add(basicSearchWidget);
        }

        {
            CComponent<?>[][] advancedSearchComponents = new CComponent[][] {

            { form.create(form.meta().name()) },

            { form.create(form.meta().orders().$().status()) },

            };

            Widget advancedSearchWidget = CForm.createDecoratedFormWidget(LabelAlignment.LEFT, advancedSearchComponents, "Advanced", true, false);
            contentPanel.add(advancedSearchWidget);
        }

        form.populate(null);

        Button viewButton = new Button("View");
        viewButton.getElement().getStyle().setProperty("margin", "5px 0px 5px 150px");
        contentPanel.add(viewButton);
    }

    @Override
    public void onView(Customer criteria) {
        // TODO Auto-generated method stub

    }
}
