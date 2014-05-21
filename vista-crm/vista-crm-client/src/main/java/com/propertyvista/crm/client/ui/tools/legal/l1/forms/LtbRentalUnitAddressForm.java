/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.legal.ltbcommon.LtbRentalUnitAddress;

public class LtbRentalUnitAddressForm extends CForm<LtbRentalUnitAddress> {

    public LtbRentalUnitAddressForm() {
        super(LtbRentalUnitAddress.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel panel = new FormPanel(this);
        panel.append(Location.Left, proto().streetNumber()).decorate();
        panel.append(Location.Left, proto().streetName()).decorate();
        panel.append(Location.Left, proto().streetType()).decorate();
        panel.append(Location.Left, proto().direction()).decorate();
        panel.append(Location.Left, proto().unit()).decorate();
        panel.append(Location.Left, proto().municipality()).decorate();
        panel.append(Location.Left, proto().postalCode()).decorate();
        return panel;
    }

}
