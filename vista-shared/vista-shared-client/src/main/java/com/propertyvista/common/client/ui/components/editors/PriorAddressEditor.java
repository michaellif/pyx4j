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
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

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
    protected IsWidget createContent() {
        FormPanel main = internalCreateContent();

        main.hr();

        main.append(Location.Left, inject(proto().moveInDate(), new FieldDecoratorBuilder(10).build()));
        main.append(Location.Left, inject(proto().moveOutDate(), new FieldDecoratorBuilder(10).build()));

        CField<OwnedRented, ?> rentedComponent = (CField<OwnedRented, ?>) inject(proto().rented(), new FieldDecoratorBuilder(15).build());
        rentedComponent.addValueChangeHandler(new ValueChangeHandler<OwnedRented>() {
            @Override
            public void onValueChange(ValueChangeEvent<OwnedRented> event) {
                setVisibility(getValue());
            }
        });

        main.append(Location.Right, rentedComponent);
        main.append(Location.Right, inject(proto().payment(), new FieldDecoratorBuilder(8).build()));
        main.append(Location.Right, inject(proto().propertyCompany(), new FieldDecoratorBuilder(20).build()));
        main.append(Location.Right, inject(proto().managerName(), new FieldDecoratorBuilder(20).build()));
        main.append(Location.Right, inject(proto().managerPhone(), new FieldDecoratorBuilder(20).build()));
        main.append(Location.Right, inject(proto().managerEmail(), new FieldDecoratorBuilder(20).build()));

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
