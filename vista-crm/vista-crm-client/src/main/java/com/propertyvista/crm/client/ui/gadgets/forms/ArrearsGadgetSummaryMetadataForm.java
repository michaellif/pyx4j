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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.dashboard.gadgets.type.ArrearsSummaryGadgetMetadata;

public class ArrearsGadgetSummaryMetadataForm extends CEntityDecoratableForm<ArrearsSummaryGadgetMetadata> {

    public ArrearsGadgetSummaryMetadataForm() {
        super(ArrearsSummaryGadgetMetadata.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().refreshInterval())).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customizeCategory())).build());
        get(proto().customizeCategory()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().category()).setVisible(event.getValue() == true);
            }
        });
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().category())).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().customizeDate())).build());
        get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().asOf()).setVisible(event.getValue() == true);
            }
        });
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().asOf())).build());
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        get(proto().asOf()).setVisible(getValue().customizeDate().isBooleanTrue());
        get(proto().category()).setVisible(getValue().customizeCategory().isBooleanTrue());
    }

}
