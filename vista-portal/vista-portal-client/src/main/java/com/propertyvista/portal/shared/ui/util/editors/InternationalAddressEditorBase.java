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
 */
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.components.c.CCountryComboBox;
import com.propertyvista.common.client.ui.components.c.CProvinceComboBox;
import com.propertyvista.common.client.ui.components.editors.CountryContextCComponentProvider;
import com.propertyvista.common.client.ui.components.editors.PostalCodeFormatter;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOCountry;

public class InternationalAddressEditorBase<A extends InternationalAddress> extends CForm<A> {

    private final CProvinceComboBox province = new CProvinceComboBox();

    public InternationalAddressEditorBase(Class<A> entityClass) {
        this(entityClass, FieldDecoratorBuilder.LABEL_WIDTH, 20, FieldDecoratorBuilder.CONTENT_WIDTH);
    }

    public InternationalAddressEditorBase(Class<A> entityClass, double labelWidth, double maxCompWidth, double contentWidth) {
        super(entityClass);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().country(), new CCountryComboBox(ISOCountry.Canada, ISOCountry.UnitedStates)).decorate();
        formPanel.append(Location.Left, proto().streetNumber()).decorate();
        formPanel.append(Location.Left, proto().streetName()).decorate();
        formPanel.append(Location.Left, proto().suiteNumber()).decorate();

        formPanel.append(Location.Left, proto().province(), province).decorate();
        formPanel.append(Location.Left, proto().city()).decorate();
        formPanel.append(Location.Left, proto().postalCode()).decorate().componentWidth(120);

        return formPanel;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        CComponent<?, ISOCountry, ?, ?> country = get(proto().country());
        CTextField postalCode = (CTextField) get(proto().postalCode());

        postalCode.setFormatter(new PostalCodeFormatter(new CountryContextCComponentProvider(country)));
        postalCode.addComponentValidator(new ZipCodeValueValidator(this, proto().country()));

        country.addValueChangeHandler(new RevalidationTrigger<ISOCountry>(postalCode));
        country.addValueChangeHandler(new ValueChangeHandler<ISOCountry>() {
            @Override
            public void onValueChange(ValueChangeEvent<ISOCountry> event) {
                onCountrySelected(event.getValue());
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        onCountrySelected(getValue().country().getValue());
    }

    private void onCountrySelected(ISOCountry country) {
        province.setCountry(country);
        if (country == null) {
            applyDefaultSettings();
            showDetails(false);
        } else {
            showDetails(true);
            switch (country) {
            case Canada:
                get(proto().streetNumber()).setVisible(true);
                get(proto().streetName()).setTitle(proto().streetName().getMeta().getCaption());
                get(proto().suiteNumber()).setTitle(proto().suiteNumber().getMeta().getCaption());
                // ensure correct option list for "known" countries
                province.setVisible(true);
                province.setTitle("Province");
                get(proto().postalCode()).setTitle("Postal Code");
                break;
            case UnitedStates:
                get(proto().streetNumber()).setVisible(true);
                get(proto().streetName()).setTitle(proto().streetName().getMeta().getCaption());
                get(proto().suiteNumber()).setTitle(proto().suiteNumber().getMeta().getCaption());
                province.setVisible(true);
                province.setTitle("State");
                get(proto().postalCode()).setTitle("Zip Code");
                break;
            case UnitedKingdom:
                get(proto().streetNumber()).setVisible(true);
                get(proto().streetName()).setTitle(proto().streetName().getMeta().getCaption());
                get(proto().suiteNumber()).setTitle(proto().suiteNumber().getMeta().getCaption());
                province.setVisible(false);
                get(proto().postalCode()).setTitle("Postal Code");
                break;
            default:
                applyDefaultSettings();
            }
        }
    }

    private void showDetails(boolean visible) {
        get(proto().streetNumber()).setVisible(visible);
        get(proto().streetName()).setVisible(visible);
        get(proto().suiteNumber()).setVisible(visible);
        get(proto().province()).setVisible(visible);
        get(proto().city()).setVisible(visible);
        get(proto().postalCode()).setVisible(visible);

    }

    // International
    private void applyDefaultSettings() {
        get(proto().streetNumber()).setVisible(false);
        get(proto().streetName()).setTitle("Address Line 1");
        get(proto().suiteNumber()).setTitle("Address Line 2");
        province.setVisible(true);
        province.setTitle(proto().province().getMeta().getCaption());
        get(proto().postalCode()).setVisible(true);
        get(proto().postalCode()).setTitle(proto().postalCode().getMeta().getCaption());
    }

    @Override
    public void generateMockData() {
        get(proto().streetNumber()).setMockValue("100");
        get(proto().streetName()).setMockValue("King St W");
        get(proto().city()).setMockValue("Toronto");
        get(proto().province()).setMockValue("Ontario");
        get(proto().country()).setMockValue(ISOCountry.Canada);
        get(proto().postalCode()).setMockValue("M5H 1A1");
    }
}
