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
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class AddressStructuredEditorImpl<A extends AddressStructured> extends CEntityDecoratableForm<A> {

    private final boolean showUnit;

    private final boolean twoColumns;

    public AddressStructuredEditorImpl(Class<A> clazz) {
        this(clazz, true);
    }

    public AddressStructuredEditorImpl(Class<A> clazz, boolean twoColumns) {
        this(clazz, twoColumns, true);
    }

    public AddressStructuredEditorImpl(Class<A> clazz, boolean twoColumns, boolean showUnit) {
        super(clazz);
        this.twoColumns = twoColumns;
        this.showUnit = showUnit;
    }

    protected boolean isTwoColumns() {
        return twoColumns;
    }

    @SuppressWarnings("unchecked")
    protected FormFlexPanel internalCreateContent() {
        FormFlexPanel main = new FormFlexPanel();
        final VerticalPanel county = new VerticalPanel();

        int row = 0;
        int column = 0;
        final CComponent<Country, ?> country = (CComponent<Country, ?>) inject(proto().country());
        main.setWidget(row++, column, new DecoratorBuilder(country, 15).build());

        final CComponent<Province, ?> province = (CComponent<Province, ?>) inject(proto().province());
        main.setWidget(row++, column, new DecoratorBuilder(province, 17).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().city()), 15).build());

        // Need local variables to avoid extended casting that make the code unreadable

        final CComponent<String, ?> postalCode = (CComponent<String, ?>) inject(proto().postalCode());
        main.setWidget(row++, column, new DecoratorBuilder(postalCode, 7).build());
        county.add(new DecoratorBuilder(inject(proto().county()), 15).build());
        main.setWidget(row++, column, county);

        if (twoColumns) {
            row = 0;
            column = 1;
        }
        if (showUnit) {
            main.setWidget(row++, column, new DecoratorBuilder(inject(proto().suiteNumber()), 12).build());
        }

        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetNumber()), 5).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetNumberSuffix()), 5).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetName()), 15).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetType()), 10).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetDirection()), 10).build());

        attachFilters(proto(), province, country, postalCode);

        get(proto().country()).addValueChangeHandler(new ValueChangeHandler<Country>() {

            @Override
            public void onValueChange(ValueChangeEvent<Country> event) {
                checkCountry();
            }
        });

        return main;
    }

    private void attachFilters(final AddressStructured proto, CComponent<Province, ?> province, CComponent<Country, ?> country, CComponent<String, ?> postalCode) {
        postalCode.addValueValidator(new ZipCodeValueValidator(this, proto.country()));
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
    protected void onSetValue(boolean populate) {
        super.onSetValue(populate);
        if (isValueEmpty()) {
            return;
        }

        checkCountry();
    }

    private void checkCountry() {
        if (getValue() != null) {
            if (getValue().country().name().getStringView().compareTo("United States") == 0) {
                get(proto().county()).setVisible(true);
                get(proto().postalCode()).setTitle("Zip Code");
                get(proto().province()).setTitle("State");
            } else if (getValue().country().name().getStringView().compareTo("Canada") == 0) {
                get(proto().county()).setVisible(false);
                get(proto().postalCode()).setTitle("Postal Code");
                get(proto().province()).setTitle("Province");
            } else {
                //TODO generic case for other countries
            }
        }
    }
}