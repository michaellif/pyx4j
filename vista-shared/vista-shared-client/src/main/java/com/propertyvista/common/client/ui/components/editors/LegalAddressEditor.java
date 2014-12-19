/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 17, 2014
 * @author stanp
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.common.client.ui.components.c.CCountryComboBox;
import com.propertyvista.domain.contact.LegalAddress;
import com.propertyvista.domain.ref.ISOCountry;

public class LegalAddressEditor extends InternationalAddressEditorBase<LegalAddress> {

    public LegalAddressEditor() {
        super(LegalAddress.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().country(), new CCountryComboBox(ISOCountry.Canada, ISOCountry.UnitedStates)).decorate();
        formPanel.append(Location.Left, proto().streetNumber()).decorate();
        formPanel.append(Location.Left, proto().streetName()).decorate();
        formPanel.append(Location.Left, proto().streetType()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().streetDirection()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().suiteNumber()).decorate();

        formPanel.append(Location.Right, proto().province(), province).decorate();
        formPanel.append(Location.Right, proto().city()).decorate();
        formPanel.append(Location.Right, proto().postalCode()).decorate().componentWidth(120);

        return formPanel;
    }

    @Override
    protected void showDetails(boolean visible) {
        super.showDetails(visible);

        get(proto().streetDirection()).setVisible(visible);
        get(proto().streetType()).setVisible(visible);
    }

    @Override
    public void generateMockData() {
        super.generateMockData();
        get(proto().streetName()).setMockValue("King");
        get(proto().streetType()).setMockValue("St");
        get(proto().streetDirection()).setMockValue("W");
    }
}
