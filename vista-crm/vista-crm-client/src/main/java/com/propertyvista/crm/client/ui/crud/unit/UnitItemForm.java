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

import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemForm extends CrmEntityForm<AptUnitItem> {

    private static final I18n i18n = I18n.get(UnitItemForm.class);

    public UnitItemForm() {
        this(false);
    }

    public UnitItemForm(boolean viewMode) {
        super(AptUnitItem.class, viewMode);
    }

    @Override
    public void createTabs() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setH1(++row, 0, 2, i18n.tr("Information"));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 35).build());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().conditionNotes()), 50).build());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setH1(++row, 0, 2, i18n.tr("Details"));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().wallColor()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().flooringType()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().flooringInstallDate()), 9).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().flooringValue()), 9).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().counterTopType()), 10).build());

        row = 4;
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().counterTopInstallDate()), 9).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().counterTopValue()), 9).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().cabinetsType()), 10).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().cabinetsInstallDate()), 9).build());
        content.setWidget(++row, 1, new DecoratorBuilder(inject(proto().cabinetsValue()), 9).build());

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        validateInstallDates();

        selectTab(addTab(content, i18n.tr("General")));

    }

    private void validateInstallDates() {
        new PastDateValidation(get(proto().flooringInstallDate()));
        new PastDateValidation(get(proto().counterTopInstallDate()));
        new PastDateValidation(get(proto().cabinetsInstallDate()));
    }
}
