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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.legal.l1.L1TenantContactInfo;

public class L1TenantContactInfoForm extends CForm<L1TenantContactInfo> {

    private static final I18n i18n = I18n.get(L1TenantContactInfo.class);

    public L1TenantContactInfoForm() {
        super(L1TenantContactInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel panel = new FormPanel(this);
        panel.h2(i18n.tr("Mailing Address"));
        panel.append(Location.Left, proto().mailingAddress()).decorate();
        panel.append(Location.Left, proto().unit()).decorate();
        panel.append(Location.Left, proto().municipality()).decorate();
        panel.append(Location.Left, proto().province()).decorate();
        panel.append(Location.Left, proto().postalCode()).decorate();

        panel.h2(i18n.tr("Phones and Email"));
        panel.append(Location.Left, proto().dayPhoneNumber()).decorate();
        panel.append(Location.Left, proto().eveningPhoneNumber()).decorate();
        panel.append(Location.Left, proto().faxNumber()).decorate();
        panel.append(Location.Left, proto().emailAddress()).decorate();

        return panel;
    }

}
