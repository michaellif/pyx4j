/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateIncludeTodayValidator;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemForm extends CrmEntityForm<AptUnitItem> {

    private static final I18n i18n = I18n.get(UnitItemForm.class);

    public UnitItemForm(IForm<AptUnitItem> view) {
        super(AptUnitItem.class, view);

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("General"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type()), 10).build());
        main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().description()), true).build());
        main.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().conditionNotes()), true).build());

        row = 3;
        main.setH1(++row, 0, 2, i18n.tr("Details"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().wallColor()), 10).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().flooringType()), 10).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().flooringInstallDate()), 9).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().flooringValue()), 9).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().counterTopType()), 10).build());

        row = 4;
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().counterTopInstallDate()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().counterTopValue()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().cabinetsType()), 10).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().cabinetsInstallDate()), 9).build());
        main.setWidget(++row, 1, new FormDecoratorBuilder(inject(proto().cabinetsValue()), 9).build());

        setTabBarVisible(false);
        selectTab(addTab(main));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().flooringInstallDate()).addValueValidator(new PastDateIncludeTodayValidator());
        get(proto().counterTopInstallDate()).addValueValidator(new PastDateIncludeTodayValidator());
        get(proto().cabinetsInstallDate()).addValueValidator(new PastDateIncludeTodayValidator());
    }
}
