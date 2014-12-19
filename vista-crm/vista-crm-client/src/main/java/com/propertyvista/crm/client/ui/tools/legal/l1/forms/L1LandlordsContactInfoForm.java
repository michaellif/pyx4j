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
 */
package com.propertyvista.crm.client.ui.tools.legal.l1.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.legal.l1.L1LandlordsContactInfo;

public class L1LandlordsContactInfoForm extends CForm<L1LandlordsContactInfo> {

    public L1LandlordsContactInfoForm() {
        super(L1LandlordsContactInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Dual, proto().typeOfLandlord()).decorate();
        formPanel.append(Location.Dual, proto().firstName()).decorate();
        formPanel.append(Location.Dual, proto().lastName()).decorate();
        formPanel.append(Location.Dual, proto().streetAddress()).decorate();
        formPanel.append(Location.Dual, proto().unit()).decorate();
        formPanel.append(Location.Dual, proto().municipality()).decorate();
        formPanel.append(Location.Dual, proto().province()).decorate();
        formPanel.append(Location.Dual, proto().postalCode()).decorate();
        formPanel.append(Location.Dual, proto().dayPhoneNumber()).decorate();
        formPanel.append(Location.Dual, proto().eveningPhoneNumber()).decorate();
        formPanel.append(Location.Dual, proto().faxNumber()).decorate();
        formPanel.append(Location.Dual, proto().emailAddress()).decorate();
        return formPanel;
    }
}
