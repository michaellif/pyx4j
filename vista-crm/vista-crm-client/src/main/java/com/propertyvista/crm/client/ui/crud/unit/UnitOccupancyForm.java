/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.unit;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

public class UnitOccupancyForm extends CrmEntityForm<AptUnitOccupancySegment> {

    private static final I18n i18n = I18n.get(UnitOccupancyForm.class);

    public UnitOccupancyForm(IForm<AptUnitOccupancySegment> view) {
        super(AptUnitOccupancySegment.class, view);

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, inject(proto().dateFrom(), new FormDecoratorBuilder(9).build()));
        content.setWidget(++row, 0, inject(proto().dateTo(), new FormDecoratorBuilder(9).build()));
        content.setWidget(++row, 0, inject(proto().status(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().offMarket(), new FormDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().lease(), new FormDecoratorBuilder(25).build()));
        content.setWidget(++row, 0, inject(proto().description(), new FormDecoratorBuilder(50).build()));

        get(proto().status()).addValueChangeHandler(new ValueChangeHandler<AptUnitOccupancySegment.Status>() {
            @Override
            public void onValueChange(ValueChangeEvent<Status> event) {
                get(proto().offMarket()).setVisible(Status.offMarket.equals(getValue().status().getValue()));
                get(proto().lease()).setVisible(Status.occupied.equals(getValue().status().getValue()));
            };
        });

        get(proto().offMarket()).setVisible(false);
        get(proto().lease()).setVisible(false);

        selectTab(addTab(content));

    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getValue().isNull()) {
            get(proto().offMarket()).setVisible(false);
            get(proto().lease()).setVisible(false);
        } else {
            get(proto().offMarket()).setVisible(Status.offMarket.equals(getValue().status().getValue()));
            get(proto().lease()).setVisible(Status.occupied.equals(getValue().status().getValue()));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();
        new StartEndDateValidation(get(proto().dateFrom()), get(proto().dateTo()));
    }
}