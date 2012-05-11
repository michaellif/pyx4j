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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

public class UnitOccupancyForm extends CrmEntityForm<AptUnitOccupancySegment> {

    private static final I18n i18n = I18n.get(UnitOccupancyForm.class);

    public UnitOccupancyForm() {
        this(false);
    }

    public UnitOccupancyForm(boolean viewMode) {
        super(AptUnitOccupancySegment.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dateFrom()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().dateTo()), 9).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().offMarket()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().lease()), 25).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 50).build());

        get(proto().status()).addValueChangeHandler(new ValueChangeHandler<AptUnitOccupancySegment.Status>() {
            @Override
            public void onValueChange(ValueChangeEvent<Status> event) {
                get(proto().offMarket()).setVisible(Status.offMarket.equals(getValue().status().getValue()));
                get(proto().lease()).setVisible(Status.leased.equals(getValue().status().getValue()));
            };
        });

        get(proto().offMarket()).setVisible(false);
        get(proto().lease()).setVisible(false);

        return new ScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        if (getValue().isNull()) {
            get(proto().offMarket()).setVisible(false);
            get(proto().lease()).setVisible(false);
        } else {
            get(proto().offMarket()).setVisible(Status.offMarket.equals(getValue().status().getValue()));
            get(proto().lease()).setVisible(Status.leased.equals(getValue().status().getValue()));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();
        new StartEndDateValidation(get(proto().dateFrom()), get(proto().dateTo()));
    }
}