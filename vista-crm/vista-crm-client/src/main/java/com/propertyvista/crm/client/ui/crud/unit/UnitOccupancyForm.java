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

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

public class UnitOccupancyForm extends CrmEntityForm<AptUnitOccupancySegment> {

    private static final I18n i18n = I18n.get(UnitOccupancyForm.class);

    public UnitOccupancyForm(IFormView<AptUnitOccupancySegment, ?> view) {
        super(AptUnitOccupancySegment.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().dateFrom()).decorate();
        formPanel.append(Location.Left, proto().dateTo()).decorate();
        formPanel.append(Location.Left, proto().status()).decorate();
        formPanel.append(Location.Left, proto().offMarket()).decorate();
        formPanel.append(Location.Left, proto().lease()).decorate();
        formPanel.append(Location.Left, proto().description()).decorate();

        get(proto().status()).addValueChangeHandler(new ValueChangeHandler<AptUnitOccupancySegment.Status>() {
            @Override
            public void onValueChange(ValueChangeEvent<Status> event) {
                get(proto().offMarket()).setVisible(Status.offMarket.equals(getValue().status().getValue()));
                get(proto().lease()).setVisible(Status.occupied.equals(getValue().status().getValue()));
            };
        });

        get(proto().offMarket()).setVisible(false);
        get(proto().lease()).setVisible(false);

        selectTab(addTab(formPanel, i18n.tr("General")));

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