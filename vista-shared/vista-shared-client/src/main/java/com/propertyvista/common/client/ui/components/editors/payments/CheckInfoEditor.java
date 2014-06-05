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
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.payment.AccountType;
import com.propertyvista.domain.payment.CheckInfo;

public class CheckInfoEditor extends CForm<CheckInfo> {

    public CheckInfoEditor() {
        super(CheckInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().nameOn()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().bankName()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().accountType()).decorate().componentWidth(150);
        formPanel.append(Location.Left, proto().checkNo()).decorate().componentWidth(100);

        formPanel.append(Location.Right, proto().transitNo()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().institutionNo()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().accountNo()).decorate().componentWidth(200);

        return formPanel;
    }

    @Override
    public void generateMockData() {
        get(proto().nameOn()).setMockValue("Dev");
        get(proto().bankName()).setMockValue("Nowhere Bank");
        get(proto().accountType()).setMockValue(AccountType.Chequing);
        get(proto().checkNo()).setMockValue("1");
        get(proto().institutionNo()).setMockValue("123");
        get(proto().transitNo()).setMockValue("12345");
        get(proto().accountNo()).setMockValueByString(String.valueOf(System.currentTimeMillis() % 10000000));
    }
}
