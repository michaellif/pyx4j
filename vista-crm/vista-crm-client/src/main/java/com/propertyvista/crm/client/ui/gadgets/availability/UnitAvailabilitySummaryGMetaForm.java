/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.availability;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailabilitySummaryGMeta;

public class UnitAvailabilitySummaryGMetaForm extends CEntityDecoratableForm<UnitAvailabilitySummaryGMeta> {

    public UnitAvailabilitySummaryGMetaForm() {
        super(UnitAvailabilitySummaryGMeta.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel p = new FormFlexPanel();
        int row = -1;
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().refreshInterval())).build());
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().customizeDate())).build());
        get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue() != null) {
                    get(proto().asOf()).setVisible(event.getValue());
                }
            }
        });
        p.setWidget(++row, 0, new DecoratorBuilder(inject(proto().asOf())).build());
        get(proto().asOf()).setVisible(false);
        return p;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        get(proto().asOf()).setVisible(getValue().customizeDate().isBooleanTrue());
    }

}
