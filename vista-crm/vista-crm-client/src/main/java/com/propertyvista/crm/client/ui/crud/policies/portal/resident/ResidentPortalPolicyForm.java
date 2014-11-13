/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 5, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.portal.resident;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.ResidentPortalPolicyDTO;

public class ResidentPortalPolicyForm extends PolicyDTOTabPanelBasedForm<ResidentPortalPolicyDTO> {

    private final static I18n i18n = I18n.get(ResidentPortalPolicyForm.class);

    public ResidentPortalPolicyForm(IFormView<ResidentPortalPolicyDTO> view) {
        super(ResidentPortalPolicyDTO.class, view);
        addTab(createDetailsTab(), i18n.tr("Details"));

    }

    private IsWidget createDetailsTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().communicationEnabled()).decorate().componentWidth(60);

        return formPanel;
    }

}
