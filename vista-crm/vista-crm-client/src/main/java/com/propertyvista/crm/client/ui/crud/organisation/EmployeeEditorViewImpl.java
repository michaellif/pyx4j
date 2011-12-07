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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.OkBox.OkResult;
import com.propertyvista.crm.client.ui.components.boxes.NewPasswordBox;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;

public class EmployeeEditorViewImpl extends CrmEditorViewImplBase<EmployeeDTO> implements EmployeeEditorView {

    private final Button passwordAction;

    public EmployeeEditorViewImpl() {
        super(CrmSiteMap.Organization.Employee.class, new EmployeeEditorForm());

        // Add actions:
        passwordAction = new Button(i18n.tr("Change password"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final NewPasswordBox box = new NewPasswordBox(form.getValue().user().equals(ClientContext.getUserVisit()));
                box.run(new OkResult() {
                    @Override
                    public void onOk() {
                        ((EmployeeEditorView.Presenter) presenter).changePassword(box.getOldPassword(), box.getNewPassword1());
                    }
                });
            }
        });
        addToolbarItem(passwordAction.asWidget());
    }
}
