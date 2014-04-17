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
package com.propertyvista.portal.prospect.ui.application.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;
import com.propertyvista.portal.shared.ui.util.editors.AddressStructuredEditor;

public class PriorAddressEditor extends AddressStructuredEditor<PriorAddress> {

    public PriorAddressEditor() {
        super(PriorAddress.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel main = internalCreateContent();

        int row = main.getRowCount();

        main.setBR(++row, 0, 1);

        main.setWidget(++row, 0, inject(proto().moveInDate(), new FieldDecoratorBuilder(120).build()));
        main.setWidget(++row, 0, inject(proto().moveOutDate(), new FieldDecoratorBuilder(120).build()));

        @SuppressWarnings("unchecked")
        CComponent<OwnedRented> rentedComponent = (CComponent<OwnedRented>) inject(proto().rented());
        rentedComponent.setDecorator(new FieldDecoratorBuilder(150).build());
        rentedComponent.addValueChangeHandler(new ValueChangeHandler<OwnedRented>() {
            @Override
            public void onValueChange(ValueChangeEvent<OwnedRented> event) {
                setVisibility(getValue());
            }
        });

        main.setWidget(++row, 0, rentedComponent);
        main.setWidget(++row, 0, inject(proto().payment(), new FieldDecoratorBuilder(100).build()));
        main.setWidget(++row, 0, inject(proto().propertyCompany(), new FieldDecoratorBuilder(230).build()));
        main.setWidget(++row, 0, inject(proto().managerName(), new FieldDecoratorBuilder(180).build()));
        main.setWidget(++row, 0, inject(proto().managerPhone(), new FieldDecoratorBuilder(180).build()));
        main.setWidget(++row, 0, inject(proto().managerEmail(), new FieldDecoratorBuilder(230).build()));

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
    public void generateMockData() {
        super.generateMockData();
        get(proto().moveInDate()).setMockValue(new LogicalDate(101, 5, 21));
        get(proto().rented()).setMockValue(OwnedRented.owned);
    }

}
