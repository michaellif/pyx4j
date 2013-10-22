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
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class AddressSimpleEditorImpl<A extends AddressSimple> extends CEntityDecoratableForm<A> {

    private static final I18n i18n = I18n.get(AddressSimpleEditorImpl.class);

    private final boolean oneColumn;

    public AddressSimpleEditorImpl(Class<A> clazz) {
        this(clazz, true);
    }

    public AddressSimpleEditorImpl(Class<A> clazz, boolean oneColumn) {
        super(clazz);
        this.oneColumn = oneColumn;
    }

    @SuppressWarnings("unchecked")
    protected BasicFlexFormPanel internalCreateContent() {
        BasicFlexFormPanel main = (oneColumn ? new BasicFlexFormPanel() : new TwoColumnFlexFormPanel());

        int row = -1;
        int col = (oneColumn ? 0 : 1);

        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().street1()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().street2()), 20).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().city()), 20).build());

        row = (oneColumn ? row : -1);

        CComponent<Province> province = (CComponent<Province>) inject(proto().province());
        main.setWidget(++row, col, new FormDecoratorBuilder(province, 20).build());

        final CComponent<Country> country = (CComponent<Country>) inject(proto().country());
        main.setWidget(++row, col, new FormDecoratorBuilder(country, 20).build());

        CComponent<String> postalCode = (CComponent<String>) inject(proto().postalCode());
        if (postalCode instanceof CTextFieldBase) {
            ((CTextFieldBase<String, ?>) postalCode).setFormat(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        }
        main.setWidget(++row, col, new FormDecoratorBuilder(postalCode, 10).build());

        attachFilters(proto(), province, country, postalCode);

        return main;
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