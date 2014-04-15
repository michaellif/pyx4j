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

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;

import com.propertyvista.domain.security.LoginNotificationsConfigurationDTO;

public class LoginNotificationsConfigurationForm extends CEntityForm<LoginNotificationsConfigurationDTO> {

    public LoginNotificationsConfigurationForm() {
        super(LoginNotificationsConfigurationDTO.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel contentPanel = new TwoColumnFlexFormPanel();

        contentPanel.setWidget(0, 0, inject(proto().isEmailNotificationEnabled(), new FormDecoratorBuilder(5).build()));
        contentPanel.setWidget(0, 1, inject(proto().email(), new FormDecoratorBuilder(22).build()));

        get(proto().isEmailNotificationEnabled()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                get(proto().email()).setEnabled(Boolean.TRUE.equals(event.getValue())); // must use this syntax because not sure that value is never null
            }
        });

        contentPanel.getColumnFormatter().setWidth(0, "20%");
        contentPanel.getColumnFormatter().setWidth(1, "80%");

        return contentPanel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (isEditable()) {
            get(proto().email()).setEnabled(getValue().isEmailNotificationEnabled().getValue(false));
        } else {
            get(proto().email()).setVisible(getValue().isEmailNotificationEnabled().getValue(false));
        }
    }
}