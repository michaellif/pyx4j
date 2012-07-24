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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
        FormFlexPanel left = new FormFlexPanel();
        int row = 0;

        final CComponent<Country, ?> country = (CComponent<Country, ?>) inject(proto().country());
        left.setWidget(row++, 0, new DecoratorBuilder(country, 20).build());

        final CComponent<Province, ?> province = (CComponent<Province, ?>) inject(proto().province());
        left.setWidget(row++, 0, new DecoratorBuilder(province, 20).build());

        final VerticalPanel county = new VerticalPanel();
        county.add(new DecoratorBuilder(inject(proto().county()), 20).build());
        left.setWidget(row++, 0, county);

        left.setWidget(row++, 0, new DecoratorBuilder(inject(proto().city()), 20).build());

        final CComponent<String, ?> postalCode = (CComponent<String, ?>) inject(proto().postalCode());
        left.setWidget(row++, 0, new DecoratorBuilder(postalCode, 7).build());

        FormFlexPanel right = new FormFlexPanel();
        row = 0;

        if (showUnit) {
            right.setWidget(row++, 0, new DecoratorBuilder(inject(proto().suiteNumber()), 5).build());
        }

        right.setWidget(row++, 0, new DecoratorBuilder(inject(proto().streetNumber()), 5).build());
        right.setWidget(row++, 0, new DecoratorBuilder(inject(proto().streetNumberSuffix()), 5).build());
        right.setWidget(row++, 0, new DecoratorBuilder(inject(proto().streetName()), 20).build());
        right.setWidget(row++, 0, new DecoratorBuilder(inject(proto().streetType()), 10).build());
        right.setWidget(row++, 0, new DecoratorBuilder(inject(proto().streetDirection()), 10).build());

        attachFilters(proto(), province, country, postalCode);

        get(proto().country()).addValueChangeHandler(new ValueChangeHandler<Country>() {
            @Override
            public void onValueChange(ValueChangeEvent<Country> event) {
                checkCountry();
            }
        });

        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, left);
        if (twoColumns) {
            main.setWidget(0, 1, right);
            main.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
            main.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        } else {
            main.setWidget(1, 0, right);
        }
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
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
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