/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.forms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.client.ui.residents.insurancemockup.forms.AgreeFolder;
import com.propertyvista.portal.rpc.shared.dto.tenantinsurance.tenantsure.TenantSurePersonalDisclaimerHolderDTO;

public class TenantSurePersonalDisclaimerForm extends CEntityDecoratableForm<TenantSurePersonalDisclaimerHolderDTO> {

    public TenantSurePersonalDisclaimerForm() {
        super(TenantSurePersonalDisclaimerHolderDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, inject(proto().content().content(), new CLabel<String>()));
        contentPanel.setBR(++row, 0, 1);
        contentPanel.setWidget(++row, 0, inject(proto().agrees(), new AgreeFolder(isEditable())));

        return contentPanel;
    }

}
