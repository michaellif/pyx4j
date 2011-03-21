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
package com.propertyvista.portal.client.ptapp.ui.components;

import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.client.ptapp.ui.validators.ProvinceContryFilters;
import com.propertyvista.portal.client.ptapp.ui.validators.RevalidationTrigger;
import com.propertyvista.portal.client.ptapp.ui.validators.ZipCodeValueValidator;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.ref.Country;
import com.propertyvista.portal.domain.ref.Province;

import com.pyx4j.entity.client.ui.flex.CEntityEditableComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class AddressUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void injectIAddress(VistaDecoratorsFlowPanel main, IAddress proto, CEntityEditableComponent<?> parent) {

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

        postalCode.addValueValidator(new ZipCodeValueValidator(country));
        country.addValueChangeHandler(new RevalidationTrigger(postalCode));

        ProvinceContryFilters.attachFilters(province, country);
    }

}
