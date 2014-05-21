/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.c.CProvinceComboBox;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.Country;

public class InternationalAddressEditor extends CForm<InternationalAddress> {

    private final CProvinceComboBox province = new CProvinceComboBox();

    public InternationalAddressEditor() {
        this(FieldDecoratorBuilder.LABEL_WIDTH, 20, FieldDecoratorBuilder.CONTENT_WIDTH);
    }

    public InternationalAddressEditor(double labelWidth, double maxCompWidth, double contentWidth) {
        super(InternationalAddress.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().streetNumber()).decorate();
        formPanel.append(Location.Left, proto().streetName()).decorate();
        formPanel.append(Location.Left, proto().unitNumber()).decorate();
        formPanel.append(Location.Left, proto().city()).decorate();

        formPanel.append(Location.Right, proto().province(), province).decorate();
        formPanel.append(Location.Right, proto().country()).decorate();
        formPanel.append(Location.Right, proto().postalCode()).decorate().componentWidth(120);

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        CComponent<?, Country, ?> country = get(proto().country());
        CTextField postalCode = (CTextField) get(proto().postalCode());

        postalCode.setFormatter(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        postalCode.addComponentValidator(new ZipCodeValueValidator(this, proto().country()));

        country.addValueChangeHandler(new RevalidationTrigger<Country>(postalCode));
        country.addValueChangeHandler(new ValueChangeHandler<Country>() {
            @Override
            public void onValueChange(ValueChangeEvent<Country> event) {
                onCountrySelected(event.getValue());
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        onCountrySelected(getValue().country());
    }

    private void onCountrySelected(Country country) {
        if (!country.isEmpty()) {
            if (country.name().getStringView().compareTo("Canada") == 0) {
                get(proto().streetNumber()).setVisible(true);
                get(proto().streetName()).setTitle(proto().streetName().getMeta().getCaption());
                get(proto().unitNumber()).setTitle(proto().streetNumber().getMeta().getCaption());
                province.setCountry(country);
                province.setVisible(true);
                province.setTitle("Province");
                get(proto().postalCode()).setTitle("Postal Code");
            } else if (country.name().getStringView().compareTo("United States") == 0) {
                get(proto().streetNumber()).setVisible(true);
                get(proto().streetName()).setTitle(proto().streetName().getMeta().getCaption());
                get(proto().unitNumber()).setTitle(proto().streetNumber().getMeta().getCaption());
                province.setCountry(country);
                province.setVisible(true);
                province.setTitle("State");
                get(proto().postalCode()).setTitle("Zip Code");
            } else if (country.name().getStringView().compareTo("United Kingdom") == 0) {
                get(proto().streetNumber()).setVisible(true);
                get(proto().streetName()).setTitle(proto().streetName().getMeta().getCaption());
                get(proto().unitNumber()).setTitle(proto().streetNumber().getMeta().getCaption());
                province.setVisible(false);
                get(proto().postalCode()).setTitle("Postal Code");
            } else {
                // International
                get(proto().streetNumber()).setVisible(false);
                get(proto().streetName()).setTitle("Address Line 1");
                get(proto().unitNumber()).setTitle("Address Line 2");
                province.setTextMode(true);
                province.setVisible(true);
                province.setTitle(proto().province().getMeta().getCaption());
                get(proto().postalCode()).setVisible(true);
                get(proto().postalCode()).setTitle(proto().postalCode().getMeta().getCaption());
            }
        }
    }
}
