/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-16
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

import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;

public class ArrearsGadgetSummaryMetadataForm extends CForm<ArrearsSummaryGadgetMetadata> {

    public ArrearsGadgetSummaryMetadataForm() {
        super(ArrearsSummaryGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().refreshInterval()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().customizeCategory()).decorate().componentWidth(120);
        get(proto().customizeCategory()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().category()).setVisible(event.getValue() == true);
            }
        });
        formPanel.append(Location.Left, proto().category()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().filterByLegalStatus()).decorate().componentWidth(120);
        get(proto().filterByLegalStatus()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().legalStatus()).setVisible(event.getValue() == true);
            }
        });
        formPanel.append(Location.Left, proto().legalStatus()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().customizeDate()).decorate().componentWidth(120);
        get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().asOf()).setVisible(event.getValue() == true);
            }
        });
        formPanel.append(Location.Left, proto().asOf()).decorate().componentWidth(120);
        return formPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().asOf()).setVisible(getValue().customizeDate().getValue(false));
        get(proto().category()).setVisible(getValue().customizeCategory().getValue(false));
        get(proto().legalStatus()).setVisible(getValue().filterByLegalStatus().getValue(false));
    }

}
