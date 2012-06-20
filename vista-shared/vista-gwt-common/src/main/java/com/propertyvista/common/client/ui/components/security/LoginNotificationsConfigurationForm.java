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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.security.LoginNotificationsConfigurationDTO;

public class LoginNotificationsConfigurationForm extends CEntityDecoratableForm<LoginNotificationsConfigurationDTO> {

    public LoginNotificationsConfigurationForm() {
        super(LoginNotificationsConfigurationDTO.class);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel contentPanel = new FormFlexPanel();
        int row = -1;

        contentPanel.setWidget(++row, 0, inject(proto().isEmailNotificationEnabled()));
        CComponent<Boolean, ?> isEmailNotificationEnabled = get(proto().isEmailNotificationEnabled());
        isEmailNotificationEnabled.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().email()).setEnabled(Boolean.TRUE.equals(event.getValue())); // must use this syntax because not sure that value is never null
            }
        });

        contentPanel.setWidget(row, 1, new DecoratorBuilder(inject(proto().email())).labelWidth(5).build());
        CComponent<String, ?> email = get(proto().email());
        email.setVisible(true);

        return contentPanel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        get(proto().email()).setEnabled(getValue().isEmailNotificationEnabled().isBooleanTrue());
    }
}