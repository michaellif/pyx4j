/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.forms;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;

import com.propertyvista.domain.dashboard.gadgets.type.ArrearsStatusGadgetMetadata;

public class ArrearsStatusGadgetMetadataForm extends CEntityForm<ArrearsStatusGadgetMetadata> {

    public ArrearsStatusGadgetMetadataForm() {
        super(ArrearsStatusGadgetMetadata.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel p = new TwoColumnFlexFormPanel();
        int row = -1;
        p.setWidget(++row, 0, inject(proto().refreshInterval(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, inject(proto().arrearsStatusListerSettings().pageSize(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, inject(proto().filterByCategory(), new FormDecoratorBuilder().build()));
        get(proto().filterByCategory()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().category()).setVisible(event.getValue() == true); // value can be null
                if (event.getValue() == false) {
                    get(proto().category()).setValue(null);
                }
            }
        });
        p.setWidget(++row, 0, inject(proto().category(), new FormDecoratorBuilder().build()));
        p.setWidget(++row, 0, inject(proto().customizeDate(), new FormDecoratorBuilder().build()));
        get(proto().customizeDate()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().asOf()).setVisible(event.getValue() == true); // value can be null
                if (event.getValue() == false) {
                    get(proto().asOf()).setValue(null);
                }
            }
        });
        p.setWidget(++row, 0, inject(proto().asOf(), new FormDecoratorBuilder().build()));
        get(proto().category()).setVisible(false);
        get(proto().asOf()).setVisible(false);
        return p;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().asOf()).setVisible(getValue().customizeDate().getValue(false));
    }

}
