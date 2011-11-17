/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 17, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.boxes;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CPasswordTextField;

import com.propertyvista.common.client.ui.components.OkCancelBox;

public class NewPasswordBox extends OkCancelBox {

    private final CPasswordTextField oldPassword = new CPasswordTextField();

    private final CPasswordTextField newPassword1 = new CPasswordTextField();

    private final CPasswordTextField newPassword2 = new CPasswordTextField();

    private final boolean showOldPassword;

    public NewPasswordBox(boolean showOldPassword) {
        super(i18n.tr("Change password"));
        this.showOldPassword = showOldPassword;
        setContent(createContent());
    }

    protected Widget createContent() {
        okButton.setEnabled(true);

        VerticalPanel content = new VerticalPanel();

        if (showOldPassword) {
            content.add(new HTML(i18n.tr("Enter old password:")));
            content.add(oldPassword);
        }

        content.add(new HTML(i18n.tr("Enter new password:")));
        content.add(newPassword1);

        content.add(new HTML(i18n.tr("Confirm new password:")));
        content.add(newPassword2);

        oldPassword.setWidth("100%");
        newPassword1.setWidth("100%");
        newPassword2.setWidth("100%");

        content.setWidth("100%");
        return content.asWidget();

    }

    @Override
    public boolean isOk() {
        if (super.isOk()) {
            if (showOldPassword) {
                return (!oldPassword.getValue().isEmpty() && !newPassword1.getValue().isEmpty() && !newPassword2.getValue().isEmpty());
            } else {
                return (!newPassword1.getValue().isEmpty() && !newPassword2.getValue().isEmpty());
            }
        }
        return false;
    }

    @Override
    protected void setSize() {
        setSize("350px", "100px");
    }

    public String getOldPassword() {
        return oldPassword.getValue();
    }

    public String getNewPassword1() {
        return newPassword1.getValue();
    }

    public String getNewPassword2() {
        return newPassword2.getValue();
    }
}