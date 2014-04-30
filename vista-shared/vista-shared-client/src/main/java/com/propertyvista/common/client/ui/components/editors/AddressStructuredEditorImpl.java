/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 21, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class AddressStructuredEditorImpl<A extends AddressStructured> extends CForm<A> {

    private final boolean showUnit;

    public AddressStructuredEditorImpl(Class<A> clazz) {
        this(clazz, true);
    }

    public AddressStructuredEditorImpl(Class<A> clazz, boolean showUnit) {
        super(clazz);
        this.showUnit = showUnit;
    }

    protected BasicCFormPanel internalCreateContent() {
        BasicCFormPanel content = new BasicCFormPanel(this);

        if (showUnit) {
            content.append(Location.Left, proto().suiteNumber()).decorate().componentWidth(100);
        }
        content.append(Location.Left, proto().streetNumber()).decorate().componentWidth(100);
        content.append(Location.Left, proto().streetNumberSuffix()).decorate().componentWidth(100);
        content.append(Location.Left, proto().streetName()).decorate().componentWidth(160);
        content.append(Location.Left, proto().streetType()).decorate().componentWidth(100);
        content.append(Location.Left, proto().streetDirection()).decorate().componentWidth(100);

        content.append(Location.Right, proto().city()).decorate().componentWidth(150);
        content.append(Location.Right, proto().county()).decorate().componentWidth(150);
        content.append(Location.Right, proto().province()).decorate().componentWidth(150);
        content.append(Location.Right, proto().country()).decorate().componentWidth(150);
        content.append(Location.Right, proto().postalCode()).decorate().componentWidth(100);

        CComponent<?, Country, ?> country = get(proto().country());
        CComponent<?, Province, ?> province = get(proto().province());
        CComponent<?, String, ?> postalCode = get(proto().postalCode());

        if (postalCode instanceof CTextFieldBase) {
            @SuppressWarnings("unchecked")
            CTextFieldBase<String, ?> comp = ((CTextFieldBase<String, ?>) postalCode);
            comp.setFormatter(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        }

        // tweaks:
        attachFilters(proto(), province, country, postalCode);
        get(proto().country()).addValueChangeHandler(new ValueChangeHandler<Country>() {
            @Override
            public void onValueChange(ValueChangeEvent<Country> event) {
                checkCountry();
            }
        });

        return content;
    }

    private void attachFilters(final AddressStructured proto, CComponent<?, Province, ?> province, CComponent<?, Country, ?> country,
            CComponent<?, String, ?> postalCode) {
        postalCode.addComponentValidator(new ZipCodeValueValidator(this, proto.country()));
        country.addValueChangeHandler(new RevalidationTrigger<Country>(postalCode));

        // The filter does not use the CEditableComponent<Country, ?> and use Model directly. So it work fine on populate.
        ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
            @Override
            public boolean acceptOption(Province entity) {
                if (getValue() == null) {
                    return true;
                } else {
                    Country country = (Country) getValue().getMember(proto.country().getPath());
                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                }
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        checkCountry();
    }

    private void checkCountry() {
        if (getValue() != null) {
            if (getValue().country().name().getStringView().compareTo("Canada") == 0) {
                get(proto().county()).setVisible(false);
                get(proto().province()).setVisible(true);
                get(proto().province()).setTitle("Province");
                get(proto().postalCode()).setTitle("Postal Code");
            } else if (getValue().country().name().getStringView().compareTo("United States") == 0) {
                get(proto().county()).setVisible(true);
                get(proto().province()).setVisible(true);
                get(proto().province()).setTitle("State");
                get(proto().postalCode()).setTitle("Zip Code");
            } else if (getValue().country().name().getStringView().compareTo("United Kingdom") == 0) {
                get(proto().county()).setVisible(false);
                get(proto().province()).setVisible(false);
                get(proto().postalCode()).setTitle("Postal Code");
            } else {
                //TODO generic case for other countries
            }
        }
    }

    @Override
    public void generateMockData() {
        get(proto().province()).setMockValueByString("Ontario");
        get(proto().city()).setMockValue("Toronto");
        get(proto().postalCode()).setMockValue("M5H 1A1");

        get(proto().streetNumber()).setMockValue("100");
        get(proto().streetName()).setMockValue("King");
        get(proto().streetType()).setMockValue(StreetType.street);
        get(proto().streetDirection()).setMockValue(StreetDirection.west);
    }
}