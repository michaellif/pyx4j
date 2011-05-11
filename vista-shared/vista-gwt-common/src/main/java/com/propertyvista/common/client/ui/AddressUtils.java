/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-21
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.common.client.ui;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.ui.OptionsFilter;
import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;

import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.common.domain.ref.Country;
import com.propertyvista.common.domain.ref.Province;

public class AddressUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void injectIAddress(VistaDecoratorsFlowPanel main, final IAddress proto, final CEntityEditableComponent<?> parent) {

        main.add(parent.inject(proto.street1()), 20);
        main.add(parent.inject(proto.street2()), 20);
        main.add(parent.inject(proto.city()), 15);

        // Need local variables to avoid extended casting that make the code unreadable
        CEditableComponent<Province, ?> province;
        main.add(province = (CEditableComponent<Province, ?>) parent.inject(proto.province()), 17);

        CEditableComponent<Country, ?> country;
        main.add(country = (CEditableComponent<Country, ?>) parent.inject(proto.country()), 15);

        CEditableComponent<String, ?> postalCode;
        main.add(postalCode = (CEditableComponent<String, ?>) parent.inject(proto.postalCode()), 7);

        postalCode.addValueValidator(new com.propertyvista.common.client.ui.validators.ZipCodeValueValidator(parent, proto.country()));
        country.addValueChangeHandler(new com.propertyvista.common.client.ui.validators.RevalidationTrigger(postalCode));

        // The filter does not use the CEditableComponent<Country, ?> and use Model directly. So it work fine on populate.
        com.propertyvista.common.client.ui.validators.ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
            @Override
            public boolean acceptOption(Province entity) {
                if (parent.getValue() == null) {
                    return true;
                } else {
                    Country country = (Country) parent.getValue().getMember(proto.country().getPath());
                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                }
            }
        });
    }
}
