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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CTextField;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
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
                new ShowPopUpBox<ActionBox>(new ActionBox()) {
                    @Override
                    protected void onClose(ActionBox box) {
                        if (box.isOk()) {
                            ((EmployeeEditorView.Presenter) presenter).changePassword(box.getOldPassword(), box.getNewPassword());
                        }
                    }
                };
            }
        });
        passwordAction.setValue(i18n.tr("Change password"));
        addToolbarItem(passwordAction.asWidget());
    }

    private class ActionBox extends OkCancelBox {

        private final CTextField oldPassword = new CTextField();

        private final CTextField newPassword = new CTextField();

        public ActionBox() {
            super(i18n.tr("Change password"));
            setContent(createContent());
        }

        protected Widget createContent() {
            okButton.setEnabled(true);

            VerticalPanel content = new VerticalPanel();

            content.add(new HTML(i18n.tr("Enter old password:")));
            content.add(oldPassword);

            content.add(new HTML(i18n.tr("Enter new password:")));
            content.add(newPassword);

            oldPassword.setWidth("100%");
            newPassword.setWidth("100%");

            content.setWidth("100%");
            return content.asWidget();

        }

        @Override
        public boolean isOk() {
            return (super.isOk() && !oldPassword.getValue().isEmpty() && !newPassword.getValue().isEmpty());
        }

        @Override
        protected void setSize() {
            setSize("350px", "100px");
        }

        protected String getOldPassword() {
            return oldPassword.getValue();
        }

        protected String getNewPassword() {
            return newPassword.getValue();
        }
    }
}
