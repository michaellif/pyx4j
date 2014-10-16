/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CProvinceComboBox;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

public class VehicleDataEditor extends CForm<Vehicle> {

    private static final I18n i18n = I18n.get(VehicleDataEditor.class);

    private final CProvinceComboBox province = new CProvinceComboBox();

    public VehicleDataEditor() {
        super(Vehicle.class);
    }

    public VehicleDataEditor(IEditableComponentFactory factory) {
        super(Vehicle.class, factory);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h3(i18n.tr("Vehicle Data"));

        formPanel.append(Location.Left, proto().make()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().model()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().color()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().year()).decorate().componentWidth(70);

        formPanel.append(Location.Right, proto().plateNumber()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().province(), province).decorate().componentWidth(170);
        formPanel.append(Location.Right, proto().country()).decorate().componentWidth(170);

        get(proto().country()).addValueChangeHandler(new ValueChangeHandler<ISOCountry>() {
            @Override
            public void onValueChange(ValueChangeEvent<ISOCountry> event) {
                onCountrySelected(event.getValue());
            }
        });

        removeMandatory();

        return formPanel;
    }

    public void removeMandatory() {
        get(proto().make()).setMandatory(false);
        get(proto().model()).setMandatory(false);
        get(proto().color()).setMandatory(false);
        get(proto().year()).setMandatory(false);
        get(proto().plateNumber()).setMandatory(false);
        get(proto().province()).setMandatory(false);
        get(proto().country()).setMandatory(false);
    }

    @Override
    public void generateMockData() {
        get(proto().make()).setMockValue("BMW");
        get(proto().model()).setMockValue("i666");
        get(proto().color()).setMockValue("Rose");
        get(proto().year()).setMockValue(new LogicalDate());
        get(proto().plateNumber()).setMockValue("LastTimeDrive");
        get(proto().province()).setMockValueByString("Ontario");
        get(proto().country()).setMockValueByString("Canada");
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        onCountrySelected(getValue().country().getValue());
    }

    private void onCountrySelected(ISOCountry country) {
        province.setCountry(country);
        if (country == null) {
            applyDefaultSettings();
            province.setVisible(false);
        } else {
            switch (country) {
            case Canada:
                province.setVisible(true);
                province.setTitle("Province");
                break;
            case UnitedStates:
                province.setVisible(true);
                province.setTitle("State");
                break;
            case UnitedKingdom:
                province.setVisible(false);
                break;
            default:
                applyDefaultSettings();
            }
        }
    }

    // International
    private void applyDefaultSettings() {
        province.setVisible(true);
        province.setTitle(proto().province().getMeta().getCaption());
    }
}
