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
package com.propertyvista.portal.shared.ui.util.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PriorAddressEditor extends AddressStructuredEditorImpl<PriorAddress> {

    public PriorAddressEditor() {
        super(PriorAddress.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = internalCreateContent();

        int row = main.getRowCount();

        main.setBR(++row, 0, 1);

        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().moveInDate()), 120).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().moveOutDate()), 120).build());

        @SuppressWarnings("unchecked")
        CComponent<OwnedRented> rentedComponent = (CComponent<OwnedRented>) inject(proto().rented());
        rentedComponent.addValueChangeHandler(new ValueChangeHandler<OwnedRented>() {
            @Override
            public void onValueChange(ValueChangeEvent<OwnedRented> event) {
                setVisibility(getValue());
            }
        });

        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(rentedComponent, 150).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().payment()), 100).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().propertyCompany()), 230).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().managerName()), 180).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().managerPhone()), 180).build());
        main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().managerEmail()), 230).build());

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
