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
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;

public class PriorAddressEditor extends AddressStructuredEditorImpl<PriorAddress> {

    public PriorAddressEditor() {
        super(PriorAddress.class);
    }

    public PriorAddressEditor(boolean twoColumns) {
        super(PriorAddress.class, twoColumns);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = internalCreateContent();

        int row0 = main.getRowCount();

        int row1 = row0 + 1;
        main.setHR(++row0, 0, 2);

        main.setWidget(++row0, 0, new FormDecoratorBuilder(inject(proto().moveInDate()), 10).build());
        main.setWidget(++row0, 0, new FormDecoratorBuilder(inject(proto().moveOutDate()), 10).build());

        int col = 1;

        @SuppressWarnings("unchecked")
        CComponent<OwnedRented> rentedComponent = (CComponent<OwnedRented>) inject(proto().rented());
        rentedComponent.addValueChangeHandler(new ValueChangeHandler<OwnedRented>() {
            @Override
            public void onValueChange(ValueChangeEvent<OwnedRented> event) {
                setVisibility(getValue());
            }
        });

        main.setWidget(++row1, col, new FormDecoratorBuilder(rentedComponent, 15).build());
        main.setWidget(++row1, col, new FormDecoratorBuilder(inject(proto().payment()), 8).build());
        main.setWidget(++row1, col, new FormDecoratorBuilder(inject(proto().propertyCompany()), 20).build());
        main.setWidget(++row1, col, new FormDecoratorBuilder(inject(proto().managerName()), 20).build());
        main.setWidget(++row1, col, new FormDecoratorBuilder(inject(proto().managerPhone()), 20).build());
        main.setWidget(++row1, col, new FormDecoratorBuilder(inject(proto().managerEmail()), 20).build());

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setVisibility(getValue());
    }

    private void setVisibility(PriorAddress value) {
        boolean rented = OwnedRented.rented.equals(value.rented().getValue());
        get(proto().payment()).setVisible(rented);
        get(proto().propertyCompany()).setVisible(rented);
        get(proto().managerName()).setVisible(rented);
        get(proto().managerPhone()).setVisible(rented);
        get(proto().managerEmail()).setVisible(rented);
    }

    @Override
    protected void devGenerateAddress() {
        super.devGenerateAddress();
        get(proto().moveInDate()).setValue(new LogicalDate(101, 5, 21));
        get(proto().rented()).setValue(OwnedRented.owned);
    }
}
