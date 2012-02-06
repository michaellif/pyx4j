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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.PastDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.property.asset.unit.AptUnitItem;

public class UnitItemEditorForm extends CrmEntityForm<AptUnitItem> {

    private static final I18n i18n = I18n.get(UnitItemEditorForm.class);

    public UnitItemEditorForm() {
        this(false);
    }

    public UnitItemEditorForm(boolean viewMode) {
        super(AptUnitItem.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row0 = -1;
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().type()), 10).build());
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().description()), 35).build());
        main.getFlexCellFormatter().setColSpan(row0, 0, 2);
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().conditionNotes()), 50).build());
        main.getFlexCellFormatter().setColSpan(row0, 0, 2);

        main.setH1(++row0, 0, 2, i18n.tr("Details"));

        int row1 = row0; // from second column from here..
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().wallColor()), 10).build());
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().flooringType()), 10).build());
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().flooringInstallDate()), 9).build());
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().flooringValue()), 9).build());
        main.setWidget(++row0, 0, new DecoratorBuilder(inject(proto().counterTopType()), 10).build());

        main.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().counterTopInstallDate()), 9).build());
        main.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().counterTopValue()), 9).build());
        main.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().cabinetsType()), 10).build());
        main.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().cabinetsInstallDate()), 9).build());
        main.setWidget(++row1, 1, new DecoratorBuilder(inject(proto().cabinetsValue()), 9).build());

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        validateInstallDates();
        return new CrmScrollPanel(main);
    }

    private void validateInstallDates() {
        new PastDateValidation(get(proto().flooringInstallDate()));
        new PastDateValidation(get(proto().counterTopInstallDate()));
        new PastDateValidation(get(proto().cabinetsInstallDate()));
    }
}
