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
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.forms.client.events.DevShortcutEvent;
import com.pyx4j.forms.client.events.DevShortcutHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
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
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class AddressSimpleEditor extends CEntityForm<AddressSimple> {

    public AddressSimpleEditor() {
        super(AddressSimple.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();

        int row = -1;

        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().street1())).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().street2())).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().city())).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().province())).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().country())).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().postalCode())).build());

        return main;
    }

    @Override
    public void addValidations() {
        super.addValidations();

        CComponent<Province> province = get(proto().province());
        CComponent<Country> country = get(proto().country());

        @SuppressWarnings("unchecked")
        CTextFieldBase<String, ?> postalCode = (CTextFieldBase<String, ?>) get(proto().postalCode());
        postalCode.setFormat(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        postalCode.addValueValidator(new ZipCodeValueValidator(this, proto().country()));

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
