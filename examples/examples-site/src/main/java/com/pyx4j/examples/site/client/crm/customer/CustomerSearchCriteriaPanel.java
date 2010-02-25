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

import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.pyx4j.entity.client.ui.crud.AbstractEntitySearchCriteriaPanel;
import com.pyx4j.entity.client.ui.crud.EntitySearchCriteriaForm;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;
import com.pyx4j.entity.shared.criterion.PathSearch;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.domain.crm.Order.Status;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CGroupBoxPanel;
import com.pyx4j.forms.client.ui.CIntegerField;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.CGroupBoxPanel.Layout;

public class CustomerSearchCriteriaPanel extends AbstractEntitySearchCriteriaPanel<Customer> {

    private static Logger log = LoggerFactory.getLogger(CustomerSearchCriteriaPanel.class);

    private final EntitySearchCriteriaForm<Customer> form;

    private final CustomerListWidget customerListWidget;

    private CIntegerField areaRadiusField;

    private CTextField fromLocationZipField;

    public CustomerSearchCriteriaPanel(CustomerListWidget listWidget) {

        this.customerListWidget = listWidget;

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        form = EntitySearchCriteriaForm.create(Customer.class);

        {
            CEditableComponent<?> street = form.create(form.meta().street());

            CComponent<?>[][] components = new CComponent[][] {

            { form.create(form.meta().name()), form.create(form.meta().phone()) },

            { street, null },

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

            areaRadiusField = new CIntegerField("Area Radius (km)");

            fromLocationZipField = new CTextField("From Location (Zip)");

            CComponent<?>[][] components = new CComponent[][] {

            { fromLocationZipField },

            { areaRadiusField },

            };

            form.bind(areaRadiusField, new PathSearch(form.meta().location(), "radius"));

            form.bind(fromLocationZipField, new PathSearch(form.meta().location(), "zip"));

            CGroupBoxPanel group = new CGroupBoxPanel("Location Search", Layout.CHECKBOX_TOGGLE);
            group.setExpended(true);
            CForm form = new CForm(LabelAlignment.LEFT);
            form.setComponents(components);
            group.addComponent(form);
            Widget basicSearchWidget = (Widget) group.initNativeComponent();
            contentPanel.add(basicSearchWidget);
        }

        {
            CComboBox<Status> orderStatus = new CComboBox<Status>("Order Status");
            form.bind(orderStatus, new PathSearch(form.meta().orders().$().status(), null));
            orderStatus.setOptions(EnumSet.allOf(Status.class));

            CComponent<?>[][] advancedSearchComponents = new CComponent[][] {

            { orderStatus },

            { form.create(form.meta().orders().$().dueDate()) },

            };

            Widget advancedSearchWidget = CForm.createDecoratedFormWidget(LabelAlignment.LEFT, advancedSearchComponents, "Advanced By Orders", true, false);
            contentPanel.add(advancedSearchWidget);
        }

        form.populate(null);

        Button viewButton = new Button("View");
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (hasDistanceCriteria()) {
                    MapUtils.obtainLatLang(fromLocationZipField.getValue(), new LatLngCallback() {

                        @Override
                        public void onSuccess(LatLng fromCoordinates) {
                            form.setPropertyValue(new PathSearch(form.meta().location(), "from"), MapUtils.newGeoPointInstance(fromCoordinates));
                            customerListWidget.view();
                        }

                        @Override
                        public void onFailure() {
                            log.warn("Can't find LatLng for distanceOverlay");
                        }
                    });
                } else {
                    customerListWidget.view();
                }
            }

        });
        viewButton.getElement().getStyle().setProperty("margin", "3px 0px 3px 8px");
        contentPanel.add(viewButton);
    }

    @Override
    public EntitySearchCriteria<Customer> getEntityCriteria() {
        return form.getValue();
    }

    @Override
    public void populateEntityCriteria(EntitySearchCriteria<Customer> criteria) {
        form.populate(criteria);
    }

    private boolean hasDistanceCriteria() {
        return areaRadiusField.getValue() != null && areaRadiusField.getValue() > 0 && !fromLocationZipField.isValueEmpty();
    }

}
