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
package com.propertyvista.portal.prospect.ui.application.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class VehicleDataEditor extends CForm<Vehicle> {

    private static final I18n i18n = I18n.get(VehicleDataEditor.class);

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
        panel.setH3(++row, 0, 1, i18n.tr("Vehicle Data"));

        panel.setWidget(++row, 0, inject(proto().make(), new FieldDecoratorBuilder(120).build()));
        panel.setWidget(++row, 0, inject(proto().model(), new FieldDecoratorBuilder(120).build()));
        panel.setWidget(++row, 0, inject(proto().color(), new FieldDecoratorBuilder(120).build()));
        panel.setWidget(++row, 0, inject(proto().year(), new FieldDecoratorBuilder(60).build()));

        panel.setWidget(++row, 0, inject(proto().plateNumber(), new FieldDecoratorBuilder(120).build()));
        panel.setWidget(++row, 0, inject(proto().province(), new FieldDecoratorBuilder(200).build()));
        panel.setWidget(++row, 0, inject(proto().country(), new FieldDecoratorBuilder(150).build()));

        return panel;
    }

    @Override
    public void generateMockData() {
        get(proto().make()).setMockValue("Rolls Royce");
        get(proto().model()).setMockValue("Phantom");
        get(proto().color()).setMockValue("Black");
        get(proto().year()).setMockValue(new LogicalDate(108, 0, 0));
        get(proto().plateNumber()).setMockValue("777");
        get(proto().country()).setMockValueByString("Canada");
        get(proto().province()).setMockValueByString("Ontario");
    }

}
