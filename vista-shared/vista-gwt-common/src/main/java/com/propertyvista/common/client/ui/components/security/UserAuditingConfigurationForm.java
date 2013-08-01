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
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.security;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.security.UserAuditingConfigurationDTO;

public class UserAuditingConfigurationForm extends CEntityDecoratableForm<UserAuditingConfigurationDTO> {

    private static final I18n i18n = I18n.get(UserAuditingConfigurationForm.class);

    public UserAuditingConfigurationForm() {
        super(UserAuditingConfigurationDTO.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();
        int row = -1;

        contentPanel.setH1(++row, 0, 2, i18n.tr("Login Notifications"));
        contentPanel.setWidget(++row, 0, 2, inject(proto().loginNotifications(), new LoginNotificationsConfigurationForm()));

        return contentPanel;
    }

}
