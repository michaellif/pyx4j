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
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class AddressSimpleEditorImpl<A extends AddressSimple> extends CEntityDecoratableForm<A> {

    public AddressSimpleEditorImpl(Class<A> clazz) {
        super(clazz);
    }

    @SuppressWarnings("unchecked")
    protected FormFlexPanel internalCreateContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().street1()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().street2()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().city()), 15).build());

        CComponent<Province> province = (CComponent<Province>) inject(proto().province());
        main.setWidget(++row, 0, new DecoratorBuilder(province, 17).build());

        final CComponent<Country> country = (CComponent<Country>) inject(proto().country());
        main.setWidget(++row, 0, new DecoratorBuilder(country, 15).build());

        CComponent<String> postalCode = (CComponent<String>) inject(proto().postalCode());
        if (postalCode instanceof CTextFieldBase) {
            ((CTextFieldBase<String, ?>) postalCode).setFormat(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        }
        main.setWidget(++row, 0, new DecoratorBuilder(postalCode, 7).build());

        attachFilters(proto(), province, country, postalCode);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isViewable()) {
            get(proto().street2()).setVisible(!getValue().street2().isNull());
        }
    }

    private void attachFilters(final AddressSimple proto, CComponent<Province> province, CComponent<Country> country, CComponent<String> postalCode) {
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