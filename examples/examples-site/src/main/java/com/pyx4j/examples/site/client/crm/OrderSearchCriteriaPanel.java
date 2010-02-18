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
package com.pyx4j.examples.site.client.crm;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.EntityCriteriaForm;
import com.pyx4j.entity.client.ui.EntityCriteriaPanel;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.Order;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public class OrderSearchCriteriaPanel extends SimplePanel implements EntityCriteriaPanel<Order> {

    private final EntityCriteriaForm<Order> form;

    public OrderSearchCriteriaPanel() {

        getElement().getStyle().setPadding(30, Unit.PX);
        getElement().getStyle().setPaddingRight(10, Unit.PX);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        form = EntityCriteriaForm.create(Order.class);

        CComponent<?>[][] components = new CComponent[][] {

        { form.create(form.meta().customer().name()) },

        };

        form.populate(null);

        Widget form = CForm.createFormWidget(LabelAlignment.TOP, components);
        form.setWidth("500px");
        contentPanel.add(form);
        Button viewButton = new Button("View");
        viewButton.getElement().getStyle().setProperty("margin", "20px 0px 20px 150px");
        contentPanel.add(viewButton);
    }

    @Override
    public void onView(Order criteria) {
        // TODO Auto-generated method stub

    }
}
