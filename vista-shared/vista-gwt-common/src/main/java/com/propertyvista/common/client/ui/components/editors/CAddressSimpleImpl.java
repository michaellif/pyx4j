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
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.RevalidationTrigger;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class CAddressSimpleImpl<A extends AddressSimple> extends CEntityDecoratableEditor<A> {

    public CAddressSimpleImpl(Class<A> clazz) {
        super(clazz);
    }

    @SuppressWarnings("unchecked")
    protected FormFlexPanel internalCreateContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().street1()), 50).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().street2()), 50).build());
        main.setWidget(row++, 0, new DecoratorBuilder(inject(proto().city()), 15).build());

        // Need local variables to avoid extended casting that make the code unreadable
        CEditableComponent<Province, ?> province = (CEditableComponent<Province, ?>) inject(proto().province());
        main.setWidget(row++, 0, new DecoratorBuilder(province, 17).build());

        CEditableComponent<Country, ?> country = (CEditableComponent<Country, ?>) inject(proto().country());
        main.setWidget(row++, 0, new DecoratorBuilder(country, 15).build());

        CEditableComponent<String, ?> postalCode = (CEditableComponent<String, ?>) inject(proto().postalCode());
        main.setWidget(row++, 0, new DecoratorBuilder(postalCode, 7).build());

        attachFilters(proto(), province, country, postalCode);

        return main;
    }

    private void attachFilters(final AddressSimple proto, CEditableComponent<Province, ?> province, CEditableComponent<Country, ?> country,
            CEditableComponent<String, ?> postalCode) {
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