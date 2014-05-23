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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

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

    @SuppressWarnings("unchecked")
    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setH3(++row, 0, 2, i18n.tr("Vehicle Data"));

        panel.setWidget(++row, 0, inject(proto().make(), new FieldDecoratorBuilder(10).build()));
        panel.setWidget(++row, 0, inject(proto().model(), new FieldDecoratorBuilder(10).build()));
        panel.setWidget(++row, 0, inject(proto().color(), new FieldDecoratorBuilder(10).build()));
        panel.setWidget(++row, 0, inject(proto().year(), new FieldDecoratorBuilder(5).build()));

        row = 0; // skip header
        panel.setWidget(++row, 1, inject(proto().plateNumber(), new FieldDecoratorBuilder(10).build()));
        panel.setWidget(++row, 1, inject(proto().province(), province, new FieldDecoratorBuilder(17).build()));
        panel.setWidget(++row, 1, inject(proto().country(), new FieldDecoratorBuilder(13).build()));

        get(proto().country()).addValueChangeHandler(new ValueChangeHandler<ISOCountry>() {
            @Override
            public void onValueChange(ValueChangeEvent<ISOCountry> event) {
                province.setCountry(event.getValue());
            }
        });

        removeMandatory();

        return panel;
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
    }
}
