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
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.common.client.ui.components.editors.CountryContextCComponentProvider;
import com.propertyvista.common.client.ui.components.editors.PostalCodeFormat;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class AddressSimpleEditor extends CForm<AddressSimple> {

    public AddressSimpleEditor() {
        super(AddressSimple.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();

        int row = -1;

        main.setWidget(++row, 0, inject(proto().street1(), new FieldDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().street2(), new FieldDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().city(), new FieldDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().province(), new FieldDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().country(), new FieldDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().postalCode(), new FieldDecoratorBuilder().build()));

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        CComponent<?, Province, ?> province = get(proto().province());
        CComponent<?, Country, ?> country = get(proto().country());

        @SuppressWarnings("unchecked")
        CTextFieldBase<String, ?> postalCode = (CTextFieldBase<String, ?>) get(proto().postalCode());
        postalCode.setFormatter(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        postalCode.addComponentValidator(new ZipCodeValueValidator(this, proto().country()));

        country.addValueChangeHandler(new RevalidationTrigger<Country>(postalCode));

        // The filter does not use the CEditableComponent<Country, ?> and use Model directly. So it work fine on populate.
        ProvinceContryFilters.attachFilters(province, country, new OptionsFilter<Province>() {
            @Override
            public boolean acceptOption(Province entity) {
                if (getValue() == null) {
                    return true;
                } else {
                    Country country = (Country) getValue().getMember(proto().country().getPath());
                    return country.isNull() || EqualsHelper.equals(entity.country().name(), country.name());
                }
            }
        });

    }

    @Override
    public void generateMockData() {
        get(proto().street1()).setMockValue("100 King St. W");
        get(proto().city()).setMockValue("Toronto");
        get(proto().postalCode()).setMockValue("M5H 1A1");
        get(proto().province()).setMockValueByString("Ontario");
    }

}
