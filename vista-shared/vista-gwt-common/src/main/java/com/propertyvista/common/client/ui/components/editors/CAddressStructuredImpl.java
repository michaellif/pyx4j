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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class CAddressStructuredImpl<A extends AddressStructured> extends CEntityDecoratableEditor<A> {

    private final boolean showUnit;

    private final boolean twoColumns;

    public CAddressStructuredImpl(Class<A> clazz) {
        this(clazz, true);
    }

    public CAddressStructuredImpl(Class<A> clazz, boolean twoColumns) {
        this(clazz, twoColumns, true);
    }

    public CAddressStructuredImpl(Class<A> clazz, boolean twoColumns, boolean showUnit) {
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

        int row = 0;
        int column = 0;
        if (showUnit) {
            main.setWidget(row++, column, new DecoratorBuilder(inject(proto().suiteNumber()), 12).build());
        }

        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetNumber()), 5).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetNumberSuffix()), 5).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetName()), 15).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetType()), 10).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().streetDirection()), 10).build());

        if (twoColumns) {
            row = 0;
            column = 1;
        }
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().city()), 15).build());
        main.setWidget(row++, column, new DecoratorBuilder(inject(proto().county()), 15).build());

        // Need local variables to avoid extended casting that make the code unreadable
        CComponent<Province, ?> province = (CComponent<Province, ?>) inject(proto().province());
        main.setWidget(row++, column, new DecoratorBuilder(province, 17).build());

        CComponent<Country, ?> country = (CComponent<Country, ?>) inject(proto().country());
        main.setWidget(row++, column, new DecoratorBuilder(country, 15).build());

        CComponent<String, ?> postalCode = (CComponent<String, ?>) inject(proto().postalCode());
        main.setWidget(row++, column, new DecoratorBuilder(postalCode, 7).build());

        attachFilters(proto(), province, country, postalCode);

        return main;
    }

    private void attachFilters(final AddressStructured proto, CComponent<Province, ?> province, CComponent<Country, ?> country,
            CComponent<String, ?> postalCode) {
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
}