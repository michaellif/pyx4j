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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemForm extends CrmEntityForm<AptUnitItem> {

    private static final I18n i18n = I18n.get(UnitItemForm.class);

    public UnitItemForm(IForm<AptUnitItem> view) {
        super(AptUnitItem.class, view);

        FormFlexPanel main = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().type()), 10).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description()), 35).build());
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().conditionNotes()), 40).build());

        FormFlexPanel details = new FormFlexPanel();

        int detailsRow = -1;
        details.setWidget(++detailsRow, 0, new FormDecoratorBuilder(inject(proto().wallColor()), 10).build());
        details.setWidget(++detailsRow, 0, new FormDecoratorBuilder(inject(proto().flooringType()), 10).build());
        details.setWidget(++detailsRow, 0, new FormDecoratorBuilder(inject(proto().flooringInstallDate()), 9).build());
        details.setWidget(++detailsRow, 0, new FormDecoratorBuilder(inject(proto().flooringValue()), 9).build());
        details.setWidget(++detailsRow, 0, new FormDecoratorBuilder(inject(proto().counterTopType()), 10).build());

        detailsRow = -1;
        details.setWidget(++detailsRow, 1, new FormDecoratorBuilder(inject(proto().counterTopInstallDate()), 9).build());
        details.setWidget(++detailsRow, 1, new FormDecoratorBuilder(inject(proto().counterTopValue()), 9).build());
        details.setWidget(++detailsRow, 1, new FormDecoratorBuilder(inject(proto().cabinetsType()), 10).build());
        details.setWidget(++detailsRow, 1, new FormDecoratorBuilder(inject(proto().cabinetsInstallDate()), 9).build());
        details.setWidget(++detailsRow, 1, new FormDecoratorBuilder(inject(proto().cabinetsValue()), 9).build());

        // add details to main:
        main.setH1(++row, 0, 2, i18n.tr("Details"));
        main.setWidget(++row, 0, details);

        selectTab(addTab(main));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        new PastDateValidation(get(proto().flooringInstallDate()));
        new PastDateValidation(get(proto().counterTopInstallDate()));
        new PastDateValidation(get(proto().cabinetsInstallDate()));
    }
}
