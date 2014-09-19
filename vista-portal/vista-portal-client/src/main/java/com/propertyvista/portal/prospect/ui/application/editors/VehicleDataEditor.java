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
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.lease.extradata.Vehicle;

public class VehicleDataEditor extends CForm<Vehicle> {

    private static final I18n i18n = I18n.get(VehicleDataEditor.class);

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

        formPanel.append(Location.Left, proto().make()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().model()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().color()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().year()).decorate().componentWidth(60);

        formPanel.append(Location.Left, proto().plateNumber()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().province()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().country()).decorate().componentWidth(150);

        return formPanel;
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
