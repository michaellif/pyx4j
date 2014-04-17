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
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

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
        main.setWidget(++row, 0, inject(proto().type(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(++row, 0, 2, inject(proto().description(), new FieldDecoratorBuilder(true).build()));
        main.setWidget(++row, 0, 2, inject(proto().conditionNotes(), new FieldDecoratorBuilder(true).build()));

        row = 3;
        main.setH1(++row, 0, 2, i18n.tr("Details"));
        main.setWidget(++row, 0, inject(proto().wallColor(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(++row, 0, inject(proto().flooringType(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(++row, 0, inject(proto().flooringInstallDate(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 0, inject(proto().flooringValue(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 0, inject(proto().counterTopType(), new FieldDecoratorBuilder(10).build()));

        row = 4;
        main.setWidget(++row, 1, inject(proto().counterTopInstallDate(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 1, inject(proto().counterTopValue(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 1, inject(proto().cabinetsType(), new FieldDecoratorBuilder(10).build()));
        main.setWidget(++row, 1, inject(proto().cabinetsInstallDate(), new FieldDecoratorBuilder(9).build()));
        main.setWidget(++row, 1, inject(proto().cabinetsValue(), new FieldDecoratorBuilder(9).build()));

        setTabBarVisible(false);
        selectTab(addTab(main));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().flooringInstallDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().counterTopInstallDate()).addComponentValidator(new PastDateIncludeTodayValidator());
        get(proto().cabinetsInstallDate()).addComponentValidator(new PastDateIncludeTodayValidator());
    }
}
