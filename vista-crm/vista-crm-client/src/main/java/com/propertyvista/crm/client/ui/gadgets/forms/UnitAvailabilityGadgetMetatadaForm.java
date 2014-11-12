/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilityGadgetMetadata;

public class UnitAvailabilityGadgetMetatadaForm extends CForm<UnitAvailabilityGadgetMetadata> {

    public UnitAvailabilityGadgetMetatadaForm() {
        super(UnitAvailabilityGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().refreshInterval()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().unitStatusListerSettings().pageSize()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().filterPreset()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().customizeDate()).decorate().componentWidth(80);
        get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue() != null) {
                    get(proto().asOf()).setVisible(event.getValue());
                }
            }
        });
        formPanel.append(Location.Left, proto().asOf()).decorate().componentWidth(80);
        get(proto().asOf()).setVisible(false);
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().asOf()).setVisible(getValue().customizeDate().getValue(false));
    }

}
