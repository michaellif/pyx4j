/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 22, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.adminusers;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;

import com.propertyvista.admin.rpc.AdminUserDTO;

public class AdminUserEditorForm extends CrudEntityForm<AdminUserDTO> {

    private final static I18n i18n = I18n.get(AdminUserEditorForm.class);

    public AdminUserEditorForm(boolean viewMode) {
        super(AdminUserDTO.class, viewMode);
    }

    public AdminUserEditorForm() {
        super(AdminUserDTO.class, false);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setWidget(++row, 0, inject(proto().name()));
        content.setWidget(++row, 0, inject(proto().email()));

        content.setH1(++row, 0, 1, i18n.tr("Security"));
        content.setWidget(++row, 0, inject(proto().password()));
        content.setWidget(++row, 0, inject(proto().passwordConfirm()));
        content.setWidget(++row, 0, inject(proto().enabled()));
        content.setWidget(++row, 0, inject(proto().requireChangePasswordOnNextLogIn()));

        return content;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        get(proto().password()).setVisible(isNewUser());
        get(proto().passwordConfirm()).setVisible(isNewUser());
    }

    private boolean isNewUser() {
        return getValue().id().isNull();
    }

}
