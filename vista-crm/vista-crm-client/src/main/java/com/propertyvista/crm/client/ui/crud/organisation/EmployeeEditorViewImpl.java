/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.components.boxes.NewPasswordBox;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeEditorViewImpl extends CrmEditorViewImplBase<EmployeeDTO> implements EmployeeEditorView {

    private final CHyperlink passwordAction;

    public EmployeeEditorViewImpl() {
        super(CrmSiteMap.Organization.Employee.class, new EmployeeEditorForm());

        // Add actions:
        passwordAction = new CHyperlink(new Command() {
            @Override
            public void execute() {
                new ShowPopUpBox<NewPasswordBox>(new NewPasswordBox(form.getValue().user().equals(ClientContext.getUserVisit()))) {
                    @Override
                    protected void onClose(NewPasswordBox box) {
                        if (box.isOk()) {
                            ((EmployeeEditorView.Presenter) presenter).changePassword(box.getOldPassword(), box.getNewPassword1());
                        }
                    }
                };
            }
        });
        passwordAction.setValue(i18n.tr("Change password"));
        addToolbarItem(passwordAction.asWidget());
    }
}
