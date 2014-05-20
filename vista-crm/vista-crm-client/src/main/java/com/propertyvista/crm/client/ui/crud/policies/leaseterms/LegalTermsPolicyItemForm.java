/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;

import com.propertyvista.domain.policy.policies.domain.LegalTermsPolicyItem;

public class LegalTermsPolicyItemForm extends CForm<LegalTermsPolicyItem> {

    public LegalTermsPolicyItemForm(boolean isEditable) {
        super(LegalTermsPolicyItem.class);
        setEditable(isEditable);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().enabled()).decorate();
        formPanel.append(Location.Dual, proto().caption()).decorate();
        formPanel.append(Location.Dual, proto().content()).decorate();

        return formPanel;
    }
}
