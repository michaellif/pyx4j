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
import com.google.gwt.user.client.ui.HTML;
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

        contentPanel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().isEmailNotificationEnabled()), 5).build());
        get(proto().isEmailNotificationEnabled()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().email()).setVisible(Boolean.TRUE.equals(event.getValue())); // must use this syntax because not sure that value is never null
            }
        });
        contentPanel.setWidget(row, 1, inject(proto().email()));
        CComponent<String, ?> email = get(proto().email());
        email.setVisible(false);
        email.asWidget().setWidth("20em");
        contentPanel.setWidget(row, 2, new HTML("&nbsp")); // for proper layout of the components

        contentPanel.getColumnFormatter().setWidth(0, "10em");
        contentPanel.getColumnFormatter().setWidth(1, "20em");

        return contentPanel;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        get(proto().email()).setVisible(getValue().isEmailNotificationEnabled().isBooleanTrue());
    }
}