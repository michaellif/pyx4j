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
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.validators.StartEndDateValidation;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.PriorAddress.OwnedRented;

public class PriorAddressEditor extends InternationalAddressEditorBase<PriorAddress> {

    private static final I18n i18n = I18n.get(PriorAddressEditor.class);

    private final boolean mandatoryMoveOutDate;

    public PriorAddressEditor() {
        this(false);
    }

    public PriorAddressEditor(boolean mandatoryMoveOutDate) {
        super(PriorAddress.class);
        this.mandatoryMoveOutDate = mandatoryMoveOutDate;
    }

    @Override
    protected IsWidget createContent() {
        FormPanel main = (FormPanel) super.createContent();

        main.hr();

        main.append(Location.Left, proto().moveInDate()).decorate().componentWidth(120);
        main.append(Location.Left, proto().moveOutDate()).decorate().componentWidth(120);
        main.append(Location.Left, proto().rented()).decorate().componentWidth(180);
        main.append(Location.Left, proto().payment()).decorate().componentWidth(120);

        main.append(Location.Right, proto().propertyCompany()).decorate().componentWidth(200);
        main.append(Location.Right, proto().managerName()).decorate().componentWidth(200);
        main.append(Location.Right, proto().managerPhone()).decorate().componentWidth(200);
        main.append(Location.Right, proto().managerEmail()).decorate().componentWidth(200);

        // tweaks:
        @SuppressWarnings("unchecked")
        CField<OwnedRented, ?> rentedComponent = (CField<OwnedRented, ?>) get(proto().rented());
        rentedComponent.addValueChangeHandler(new ValueChangeHandler<OwnedRented>() {
            @Override
            public void onValueChange(ValueChangeEvent<OwnedRented> event) {
                setVisibility(event.getValue());
            }
        });

        get(proto().moveOutDate()).setMandatory(mandatoryMoveOutDate);

        return main;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setVisibility(getValue().rented().getValue());
    }

    @Override
    public void addValidations() {
        super.addValidations();

        new StartEndDateValidation(get(proto().moveInDate()), get(proto().moveOutDate()), i18n.tr("Move In date must be before Move Out date"));
    }

    private void setVisibility(OwnedRented value) {
        boolean rented = OwnedRented.rented.equals(value);
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
