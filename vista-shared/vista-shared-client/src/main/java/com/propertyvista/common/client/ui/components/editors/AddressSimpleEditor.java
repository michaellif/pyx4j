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
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.OptionsFilter;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.ProvinceContryFilters;
import com.propertyvista.common.client.ui.validators.ZipCodeValueValidator;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class AddressSimpleEditor extends CEntityForm<AddressSimple> {

    private static final I18n i18n = I18n.get(AddressSimpleEditor.class);

    private final boolean oneColumn;

    private final double maxCompWidth;

    private final double contentWidth;

    private final double labelWidth;

    public AddressSimpleEditor() {
        this(true);
    }

    public AddressSimpleEditor(boolean oneColumn) {
        this(oneColumn, FormDecoratorBuilder.LABEL_WIDTH, 20, FormDecoratorBuilder.CONTENT_WIDTH);
    }

    public AddressSimpleEditor(boolean oneColumn, double labelWidth, double maxCompWidth, double contentWidth) {
        super(AddressSimple.class);

        this.labelWidth = labelWidth;
        this.oneColumn = oneColumn;
        this.maxCompWidth = maxCompWidth;
        this.contentWidth = contentWidth;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = (oneColumn ? new BasicFlexFormPanel() : new TwoColumnFlexFormPanel());

        int row = -1;
        int col = (oneColumn ? 0 : 1);

        main.setWidget(++row, 0, decorator(inject(proto().street1()), maxCompWidth).build());
        main.setWidget(++row, 0, decorator(inject(proto().street2()), maxCompWidth).build());
        main.setWidget(++row, 0, decorator(inject(proto().city()), maxCompWidth).build());

        row = (oneColumn ? row : -1);

        CComponent<Province> province = (CComponent<Province>) inject(proto().province());
        main.setWidget(++row, col, decorator(province, maxCompWidth).build());

        final CComponent<Country> country = (CComponent<Country>) inject(proto().country());
        main.setWidget(++row, col, decorator(country, maxCompWidth).build());

        CComponent<String> postalCode = (CComponent<String>) inject(proto().postalCode());
        if (postalCode instanceof CTextFieldBase) {
            ((CTextFieldBase<String, ?>) postalCode).setFormat(new PostalCodeFormat(new CountryContextCComponentProvider(country)));
        }
        main.setWidget(++row, col, decorator(postalCode, 10).build());

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

    private FormDecoratorBuilder decorator(CComponent<?> comp, double compWidth) {
        return new FormDecoratorBuilder(comp, labelWidth, (compWidth <= contentWidth ? compWidth : contentWidth), contentWidth);
    }
}
