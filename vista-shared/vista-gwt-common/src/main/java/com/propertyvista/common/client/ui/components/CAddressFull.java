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
package com.propertyvista.common.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.IAddressFull;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class CAddressFull<A extends IAddressFull> extends CDecoratableEntityEditor<A> {

    private final boolean showUnit;

    private final boolean twoColumns;

    public CAddressFull(Class<A> clazz) {
        this(clazz, true);
    }

    public CAddressFull(Class<A> clazz, boolean twoColumns) {
        this(clazz, twoColumns, true);
    }

    public CAddressFull(Class<A> clazz, boolean twoColumns, boolean showUnit) {
        super(clazz);
        this.twoColumns = twoColumns;
        this.showUnit = showUnit;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = internalCreateContent();
        main.setWidth("100%");

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    protected FormFlexPanel internalCreateContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        int column = 0;
        if (showUnit) {
            main.setWidget(row++, column, decorate(inject(proto().unitNumber()), 12));
        }

        main.setWidget(row++, column, decorate(inject(proto().streetNumber()), 5));
        main.setWidget(row++, column, decorate(inject(proto().streetNumberSuffix()), 5));
        main.setWidget(row++, column, decorate(inject(proto().streetName()), 15));
        main.setWidget(row++, column, decorate(inject(proto().streetType()), 10));
        main.setWidget(row++, column, decorate(inject(proto().streetDirection()), 10));

        if (twoColumns) {
            row = 0;
            column = 1;
        }
        main.setWidget(row++, column, decorate(inject(proto().city()), 15));
        main.setWidget(row++, column, decorate(inject(proto().county()), 15));

        // Need local variables to avoid extended casting that make the code unreadable
        CEditableComponent<Province, ?> province = (CEditableComponent<Province, ?>) inject(proto().province());
        main.setWidget(row++, column, decorate(province, 17));

        CEditableComponent<Country, ?> country = (CEditableComponent<Country, ?>) inject(proto().country());
        main.setWidget(row++, column, decorate(country, 15));

        CEditableComponent<String, ?> postalCode = (CEditableComponent<String, ?>) inject(proto().postalCode());
        main.setWidget(row++, column, decorate(postalCode, 7));

        attachFilters(proto(), province, country, postalCode);

        return main;
    }

    protected boolean isTwoColumns() {
        return twoColumns;
    }

    private void attachFilters(final IAddressFull proto, CEditableComponent<Province, ?> province, CEditableComponent<Country, ?> country,
            CEditableComponent<String, ?> postalCode) {
        postalCode.addValueValidator(new ZipCodeValueValidator(this, proto.country()));
        country.addValueChangeHandler(new RevalidationTrigger(postalCode));

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