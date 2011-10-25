/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;

public class CPriorAddress extends CAddressFull<PriorAddress> {

    public CPriorAddress() {
        super(PriorAddress.class);
    }

    public CPriorAddress(boolean twoColumns) {
        super(PriorAddress.class, twoColumns);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public IsWidget createContent() {
        FormFlexPanel main = internalCreateContent();

        int row0 = main.getRowCount();
        int row1 = row0;

        main.setWidget(++row0, 0, decorate(inject(proto().moveInDate()), 8.2));
        main.setWidget(++row0, 0, decorate(inject(proto().moveOutDate()), 8.2));
        main.setWidget(++row0, 0, decorate(inject(proto().phone()), 15));

        CEditableComponent<?, ?> rentedComponent = inject(proto().rented());
        rentedComponent.addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                setVizibility(getValue());
            }
        });

        if (!isTwoColumns()) {
            row1 = row0;
        }

        main.setWidget(++row1, 1, decorate(rentedComponent, 15));
        main.setWidget(++row1, 1, decorate(inject(proto().payment()), 8));
        main.setWidget(++row1, 1, decorate(inject(proto().managerName()), 15));

        main.setWidth("100%");
        if (isTwoColumns()) {
            main.getColumnFormatter().setWidth(0, "50%");
            main.getColumnFormatter().setWidth(1, "50%");
        }

        return main;
    }

    @Override
    public void populate(PriorAddress value) {
        super.populate(value);
        setVizibility(value);
    }

    private void setVizibility(PriorAddress value) {
        boolean rented = OwnedRented.rented.equals(value.rented().getValue());
        get(proto().payment()).setVisible(rented);
        get(proto().managerName()).setVisible(rented);
    }
}
