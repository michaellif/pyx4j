/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 19, 2012
 * @author ArtyomB
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.security.UserAuditingConfigurationDTO;

public class UserAuditingConfigurationForm extends CForm<UserAuditingConfigurationDTO> {

    private static final I18n i18n = I18n.get(UserAuditingConfigurationForm.class);

    public UserAuditingConfigurationForm() {
        super(UserAuditingConfigurationDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.h1(i18n.tr("Login Notifications"));
        formPanel.append(Location.Dual, proto().loginNotifications(), new LoginNotificationsConfigurationForm());

        return formPanel;
    }

}
