/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on May 2, 2014
 * @author stanp
 */
package com.pyx4j.tester.client.view.widget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.forms.client.ui.AsyncLoadingHandler;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.EntityDataSource;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.tester.client.TesterSite;
import com.pyx4j.tester.client.domain.test.DomainFactory;
import com.pyx4j.tester.client.domain.test.TestAddress;
import com.pyx4j.tester.client.domain.test.TestCountry;
import com.pyx4j.tester.client.ui.event.CComponentBrowserEvent;

public class AddressEditorViewImpl extends ScrollPanel implements AddressEditorView {

    public AddressEditorViewImpl() {
        setSize("100%", "100%");

        AddressEditorForm form = new AddressEditorForm();
        form.init();
        form.asWidget().setWidth("920px");
        form.asWidget().getElement().getStyle().setProperty("marginTop", "20px");
        add(form);
    }

    static class AddressEditorForm extends CForm<TestAddress> {

        private final ProvinceSelector provinceSelector;

        public AddressEditorForm() {
            super(TestAddress.class);
            provinceSelector = new ProvinceSelector();
            provinceSelector.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    TesterSite.getEventBus().fireEvent(new CComponentBrowserEvent(provinceSelector));
                }
            });
        }

        @Override
        protected IsWidget createContent() {
            FormPanel content = new FormPanel(this);

            content.append(Location.Left, proto().country(), new CountrySelector()).decorate().componentWidth(150);
            content.append(Location.Left, proto().addressLine1()).decorate().componentWidth(150);
            content.append(Location.Left, proto().addressLine2()).decorate().componentWidth(150);

            content.append(Location.Right, proto().city()).decorate().componentWidth(150);
            content.append(Location.Right, proto().region(), provinceSelector).decorate().componentWidth(150);
            content.append(Location.Right, proto().postalCode()).decorate().componentWidth(100);

            // tweaks:
            get(proto().country()).addValueChangeHandler(new ValueChangeHandler<TestCountry>() {
                @Override
                public void onValueChange(ValueChangeEvent<TestCountry> event) {
                    onCountrySelected(event.getValue());
                }
            });

            return content;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);
            onCountrySelected(getValue().country());
        }

        private void onCountrySelected(TestCountry country) {
            if (!country.isEmpty()) {
                if (country.name().getStringView().compareTo("Canada") == 0) {
                    provinceSelector.setCountry(country);
                    provinceSelector.setVisible(true);
                    provinceSelector.setTitle("Province");
                    get(proto().postalCode()).setTitle("Postal Code");
                } else if (country.name().getStringView().compareTo("United States") == 0) {
                    provinceSelector.setCountry(country);
                    provinceSelector.setVisible(true);
                    provinceSelector.setTitle("State");
                    get(proto().postalCode()).setTitle("Zip Code");
                } else if (country.name().getStringView().compareTo("United Kingdom") == 0) {
                    provinceSelector.setVisible(false);
                    get(proto().postalCode()).setTitle("Postal Code");
                } else {
                    // International
                    provinceSelector.setTextMode(true);
                    provinceSelector.setVisible(true);
                    provinceSelector.setTitle(proto().region().getMeta().getCaption());
                    get(proto().postalCode()).setVisible(true);
                    get(proto().postalCode()).setTitle(proto().postalCode().getMeta().getCaption());
                }
            }
        }

        static class CountrySelector extends CEntityComboBox<TestCountry> {

            public CountrySelector() {
                super(TestCountry.class, (NotInOptionsPolicy) null, new EntityDataSource<TestCountry>() {

                    @Override
                    public AsyncLoadingHandler obtain(EntityQueryCriteria<TestCountry> criteria,
                            AsyncCallback<EntitySearchResult<TestCountry>> handlingCallback) {
                        EntitySearchResult<TestCountry> result = new EntitySearchResult<>();
                        for (TestCountry c : DomainFactory.getCountries()) {
                            result.add(c);
                        }
                        handlingCallback.onSuccess(result);
                        return null;
                    }

                    @Override
                    public HandlerRegistration addDataChangeHandler(ValueChangeHandler<Class<TestCountry>> handler) {
                        return null;
                    }

                });
            }

        }
    }
}
