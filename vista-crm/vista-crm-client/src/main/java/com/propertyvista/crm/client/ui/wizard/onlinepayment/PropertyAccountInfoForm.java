/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.onlinepayment;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO;
import com.propertyvista.dto.vista2pmc.OnlinePaymentSetupDTO.PropertyAccountInfo;

public class PropertyAccountInfoForm extends CForm<OnlinePaymentSetupDTO.PropertyAccountInfo> {

    public PropertyAccountInfoForm() {
        super(PropertyAccountInfo.class);

    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().averageMonthlyRent()).decorate();
        formPanel.append(Location.Dual, proto().numberOfRentedUnits()).decorate();
        formPanel.append(Location.Dual, new HTML("&nbsp;"));
        formPanel.append(Location.Dual, new Image(VistaImages.INSTANCE.eChequeGuide()));
        formPanel.append(Location.Dual, proto().transitNumber()).decorate();
        formPanel.append(Location.Dual, proto().institutionNumber()).decorate();
        formPanel.append(Location.Dual, proto().accountNumber()).decorate();
        return formPanel;
    }
}